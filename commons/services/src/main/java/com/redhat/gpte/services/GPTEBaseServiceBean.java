package com.redhat.gpte.services;

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

public class GPTEBaseServiceBean {
    
    protected static final String RED_HAT_SUFFIX = "redhat.com";
    private static final String NUMBER_OF_LINES_TO_SKIP = "NUMBER_OF_LINES_TO_SKIP";
	protected static final String STUDENT_COURSES_HEADER = "STUDENT_COURSES";
	protected static final String RULES_FIRED_HEADER = "RULES_FIRED";
    
    private Logger logger = Logger.getLogger(getClass());
    
    @Autowired
    protected CanonicalDomainDAO canonicalDAO;
    
    public void updateStudent(@Body Student student) {
        int companyId = student.getCompanyid();
        
        // Associate RHT companyID to RHT students (as per email address) not yet associated with RHT
        if(companyId == 0) {
            if(student.getEmail().indexOf(RED_HAT_SUFFIX) > 0){
                companyId = canonicalDAO.getCompanyID(Company.RED_HAT_COMPANY_NAME);
                student.setCompanyid(companyId);
            } else 
                throw new RuntimeException(ExceptionCodes.GPTE_CC1000+student.getEmail());
        }
        canonicalDAO.updateStudent(student);
    }
    
    public void updateCompany(@Body Company companyObj) {
        int updatedCount = canonicalDAO.updateCompany(companyObj);
        logger.info(companyObj.getCompanyname()+" : updated count = "+updatedCount);
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
    
    public List<CourseCompletion> selectStudentCoursesByStudent(Exchange exchange) {
    	Object studentIdObj = exchange.getIn().getBody();
    	Integer studentId = 0;
    	if(studentIdObj instanceof String)
    		studentId = Integer.parseInt((String)studentIdObj);
    	else
    		studentId = (Integer)studentIdObj;
        if(studentId == null || studentId == 0)
            throw new RuntimeException("selectStudentCoursesByStudent() must pass a studentId");

        List<CourseCompletion> sCourses = canonicalDAO.selectPassedStudentCoursesByStudent(studentId);
        if(sCourses == null || sCourses.isEmpty()) {
            logger.warn("selectStudentCoursesByStudent() no student courses found of studentId = "+studentId);
        }else {
            CourseCompletion mostRecent = sCourses.get(0);
            sCourses.get(0).setMostRecentCourseCompletion(true);
            StringBuilder sBuilder = new StringBuilder(mostRecent.getStudent().getEmail()+" : Will execute rules on "+sCourses.size()+" CourseCompletion(s).");
            sBuilder.append(" Most recent = "+mostRecent.getCourseName() );
            logger.info(sBuilder.toString());
            exchange.getIn().setHeader(STUDENT_COURSES_HEADER, sCourses);
        }
        return sCourses;
    }
    
    public List<Integer> selectStudentIdsWithUnProcessedStudentCourses() {
        List<Integer> studentIds = canonicalDAO.selectStudentIdsWithStudentCoursesByStatus(StudentCourse.UNPROCESSED);
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
    
    public void changeMapToListOfValues(Exchange exchange) {
    	Map mapBody = (Map)exchange.getIn().getBody();
    	exchange.getIn().setBody(mapBody.values());
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
    	sBuilder.append("\nBody:\n\t");
    	sBuilder.append(exchange.getIn().getBody());
        logger.info(sBuilder.toString());
    }
}
