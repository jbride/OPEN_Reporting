package com.redhat.gpte.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.redhat.gpe.dao.CanonicalDomainDAO;
import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentCourse;
import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpe.domain.helper.GPTEBaseCondition;

public class GPTEBaseServiceBean {
    
    protected static final String RED_HAT_SUFFIX = "redhat.com";
    private static final String NUMBER_OF_LINES_TO_SKIP = "NUMBER_OF_LINES_TO_SKIP";
    protected static final String STUDENT_COURSES_HEADER = "STUDENT_COURSES";
    protected static final String RULES_FIRED_HEADER = "RULES_FIRED";
    protected static final String ORIGINAL_STUDENT_ACCREDS_HEADER="ORIGINAL_STUDENT_ACCREDS";
    public static final String LOW_STUDENT_ID = "LOW_STUDENT_ID";
    public static final String HIGH_STUDENT_ID = "HIGH_STUDENT_ID";
    public static final String ACCENTURE="accenture";
    public static final String REDHAT_HYPHENED="red-hat";
    public static final String REDHAT="redhat";
    public static final String UPLOAD_EXCEPTION_MAP = "UPLOAD_EXCEPTION_MAP";
    public static final String CAMEL_FILE_NAME="CamelFileName";
    public static final String QUERY_LDAP = "queryldap";
    public static final String UPDATE_COMPANY = "updatecompany";
    public static final String COURSE_MAPPINGS_FIRST_LINE = "PrunedCourseId";
    
    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    protected CanonicalDomainDAO canonicalDAO;
    
    /*
     * NOTE:  not ideal; use sparingly
     *        LDAP algorithm found here:  https://github.com/redhat-gpe/OPEN_Admin/blob/master/OPENTLC-WWW-Scripts/import_users.rb#L196-L208
     *        However, even with what appears to be equivalent algorithm here in java, different companyNames get generated
     */
    public String transformToCanonicalCompanyName(String companyName) {
        String tString = companyName.toLowerCase();
        tString = tString.replaceAll("[ ]{2,}", " "); // Not sure
        tString = tString.replaceAll(" - ", "-"); // eliminate spaces before and after dashes
        tString = tString.replaceAll(" ", "-"); //  replace spaces with dash
        //tString = tString.replaceAll("[^\\p{ASCII}]", ""); //  eliminate all non-ascii characters
        tString = tString.replaceAll("[^0-9a-z-]", ""); // eliminate all characters with exception alpha numeric and dash

        if(tString.indexOf(REDHAT_HYPHENED) > -1 )
            tString = REDHAT;

        if(tString.indexOf(ACCENTURE) > -1)
            tString = ACCENTURE;
        else if(tString.indexOf(REDHAT) > -1)
            tString = REDHAT;

        return tString;
    }
    
    public void updateStudent(@Body Student student) throws Exception {
        int companyId = student.getCompanyid();

        Student dbStudent = null;
        
        // Associate RHT companyID to RHT students (as per email address) not yet associated with RHT
        if(companyId == 0) {
            if(student.getEmail().indexOf(RED_HAT_SUFFIX) > 0){
                companyId = canonicalDAO.getCompanyID(Company.RED_HAT_COMPANY_NAME);
                student.setCompanyid(companyId);
            } else {
                try {
                    dbStudent = canonicalDAO.getStudentByEmail(student.getEmail());
                    student.setCompanyid(dbStudent.getCompanyid());
                } catch(org.springframework.dao.EmptyResultDataAccessException x) {
                    throw new RuntimeException(ExceptionCodes.GPTE_CC1000+student.getEmail());
                }
            }
        }
        try {
            if(dbStudent == null)
                dbStudent = canonicalDAO.getStudentByEmail(student.getEmail());

            student.setStudentid(dbStudent.getStudentid());
            student.setCountry(dbStudent.getCountry());
            student.setSkillsbaseStatus(dbStudent.getSkillsbaseStatus());
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {}
        canonicalDAO.updateStudent(student);
    }
    
    public int updateCompany(@Body Company companyObj) {
        int updatedCount = canonicalDAO.updateCompany(companyObj);
        StringBuilder sBuilder = new StringBuilder("updateCompany() just persisted updates to company ");
        sBuilder.append("\n\tcompanyName: "+companyObj.getCompanyname());
        sBuilder.append("\n\tupdateCount: "+updatedCount);
        logger.debug(sBuilder.toString());
        return updatedCount;
    }
    
    public int getCompanyID(String companyName) {
        return canonicalDAO.getCompanyID(companyName);
    }
    
    public boolean isRedHatStudent(@Body Student studentObj) {
        boolean isRHTEmployee = StringUtils.endsWith(studentObj.getEmail(), RED_HAT_SUFFIX) ;
        return isRHTEmployee;
    }
    
    public void updateUserStatusForEmailedAlready(@Body Student student) {
        canonicalDAO.updateStudentStatusForEmailedAlready(student.getEmail());
    }

    public void updateStudentStatusForOk(@Body Student student) {
        canonicalDAO.updateStudentStatusForOk(student.getEmail());
    }
    
    public boolean hasThisUserBeenEmailedBefore(@Body Student student) {
        return canonicalDAO.hasThisStudentBeenEmailedBefore(student.getEmail());
    }
    
    public void triggerStoredProcedure(@Body String storedProcName) {
        canonicalDAO.triggerStoredProcedure(storedProcName);
    }
    
    public List<GPTEBaseCondition> selectStudentCoursesAndAccredsByStudent(Exchange exchange) {
        Object studentIdObj = exchange.getIn().getBody();
        Integer studentId = 0;
        if(studentIdObj instanceof String)
            studentId = Integer.parseInt((String)studentIdObj);
        else
            studentId = (Integer)studentIdObj;
        if(studentId == null || studentId == 0)
            throw new RuntimeException("selectStudentCoursesAndAccredsByStudent() must pass a studentId");

        List<GPTEBaseCondition> sCourses = canonicalDAO.selectPassedStudentCoursesByStudent(studentId);
        if(sCourses == null || sCourses.isEmpty()) {
            logger.warn("selectStudentCoursesAndAccredsByStudent() no student courses found for studentId = "+studentId);
        }else {

            GPTEBaseCondition mostRecent = sCourses.get(0);
           
            // https://github.com/redhat-gpe/OPEN_Reporting/issues/170
            // https://github.com/redhat-gpe/OPEN_Reporting/issues/283
            //    - All studentAccreds for a student should be pulled
            List<Accreditation> accreds = canonicalDAO.selectStudentAccreditationByStudentId(studentId, -1);
            sCourses.addAll(accreds);
            exchange.getIn().setHeader(STUDENT_COURSES_HEADER, sCourses);

            // https://github.com/redhat-gpe/OPEN_Reporting/issues/283
            exchange.getIn().setHeader(ORIGINAL_STUDENT_ACCREDS_HEADER, accreds);

            //sCourses.get(0).setMostRecentCourseCompletion(true);
            StringBuilder sBuilder = new StringBuilder(studentId+" : Will execute rules on "+sCourses.size()+" CourseCompletion(s).");
            sBuilder.append(" Most recent = "+mostRecent.getName() );
            logger.info(sBuilder.toString());
        }
        return sCourses;
    }
    
    public List<Integer> selectStudentIdsWithUnProcessedStudentCourses(Exchange exchange) {
        int lowStudentId = 0;
        int highStudentId = 0;
        if(StringUtils.isNotEmpty((String)exchange.getIn().getHeader(LOW_STUDENT_ID)))
            lowStudentId = Integer.parseInt((String)exchange.getIn().getHeader(LOW_STUDENT_ID));
        if(StringUtils.isNotEmpty((String)exchange.getIn().getHeader(HIGH_STUDENT_ID)))
            highStudentId = Integer.parseInt((String)exchange.getIn().getHeader(HIGH_STUDENT_ID));
        
        List<Integer> studentIds = canonicalDAO.selectStudentIdsWithStudentCoursesByStatus(StudentCourse.UNPROCESSED, lowStudentId, highStudentId);
        if(studentIds == null || studentIds.isEmpty()) {
            logger.info("selectStudentIdsWithUnProcessedStudentCourses() no students found with a StudentCourse with status = "+StudentCourse.UNPROCESSED);
        }
        else {
            logger.info("selectStudentIdsWithUnProcessedStudentCourses() Will evaluate StudentCourses for the following # of students: "+studentIds.size());
        }
        return studentIds;
    }
    
    public int setProcessedOnStudentCourseByStudent(@Body Integer studentId) {
        int numUpdated = canonicalDAO.updateStudentCourseProcessedByStudent(studentId, StudentCourse.PROCESSED_ALL);
        logger.info(studentId+" : set following # of StudentCourses to processed: "+numUpdated);
        return numUpdated;
    }
    
    public void clearException(Exchange exchange) {
        exchange.setException(null);
    }
    
    public void clearHeaders(Exchange exchange) {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        headers.clear();
    }
    
    public void removeHeaderLinesFromSpreadsheet(Exchange exchange) {
        String numLinesSkipString = (String)exchange.getIn().getHeader(NUMBER_OF_LINES_TO_SKIP);
        if(StringUtils.isEmpty(numLinesSkipString))
            throw new RuntimeException("must specify message header: "+NUMBER_OF_LINES_TO_SKIP);
        int numberOfLinesToSkip = Integer.parseInt(numLinesSkipString);
        
        StringBuilder result = new StringBuilder();
        
        // get input string
        Message in = exchange.getIn();    
        String body = in.getBody(String.class);
        
        // split into an array
        String textStr[] = body.split("\\r?\\n");
            
        // now skip the numberOfLines ... combine the rest as result
        for (int i=0; i < textStr.length; i++) {
            
            if (i < numberOfLinesToSkip) {
                continue;
            }
            
            result.append(textStr[i] + "\n");
        }
        
        // update the body
        in.setBody(result.toString());
    }

    public void dumpHeadersAndBody(Exchange exchange) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("\nHEADERS:");
        Map<String,Object> headers = exchange.getIn().getHeaders();
        for(Entry<String,Object> entry : headers.entrySet()) {
            sBuilder.append("\n\t"+entry.getKey()+" : "+entry.getValue());
        }
        if(false) {
            sBuilder.append("\nBody:\n\t");
            sBuilder.append(exchange.getIn().getBody());
        }
        logger.info(sBuilder.toString());
    }

    public void addBodyToCollection(Exchange exchange) {
        Collection cObj = new ArrayList();
        cObj.add(exchange.getIn().getBody());
        exchange.getIn().setBody(cObj);
    }
}
