package com.redhat.gpe.coursecompletion.service;

import com.redhat.gpe.coursecompletion.dokeos.DokeosCourseCompletion;
import com.redhat.gpe.coursecompletion.sumtotal.SumtotalCourseCompletion;
import com.redhat.gpe.domain.canonical.*;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpte.services.ExceptionCodes;
import com.redhat.gpte.services.GPTEBaseServiceBean;

import org.apache.camel.Body;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Producer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseCompletionServiceBean extends GPTEBaseServiceBean {

    
    private static final String ENGLISH = "EN_US";
    private static final String SUMTOTAL_COMPLETED = "COMPLETED";
    private static final byte coursePassingValue = 70;
    public static final String GET_STUDENT_ATTRIBUTES_FROM_IPA_URI = "vm:get-student-attributes-from-ipa";
    private static final String CC_APPEND_COURSE_ISSUES_TO_FILE = "cc_append_course_issues_to_file";
    private static final String COURSE_ISSUES_OUTPUT = "/tmp/gpte/gpte_course_issues.txt";

    private Logger logger = Logger.getLogger(getClass());
    private boolean cc_append_course_issues_to_file = false;
    private File courseIssuesFile = null;
    private Map<String, Course> courseMapTempCache = new HashMap<String, Course>();  // <mappedCourseId, Course obj>
    
    private static Object synchObj = new Object();
    private static final String underscoreFilter="_$";
    private static final String dashFilter="-$";
    private static String langFilter;
    private static int rhtCompanyId = 0;
    
   
    public CourseCompletionServiceBean() throws IOException {
        String x = System.getProperty(CC_APPEND_COURSE_ISSUES_TO_FILE);
        if(StringUtils.isNotEmpty(x)){
            cc_append_course_issues_to_file = Boolean.parseBoolean(x);
            courseIssuesFile = new File(COURSE_ISSUES_OUTPUT);
            courseIssuesFile.createNewFile();
            FileOutputStream fStream = null;
            try {
                fStream = new FileOutputStream(courseIssuesFile);
                String output = "Activity Code,Activity Name";
                fStream.write(output.getBytes());
                fStream.flush();
            } finally {
                if(fStream != null)
                    fStream.close();
            }
            logger.info("CourseCompletionServiceBean:  cc_append_course_issues_to_file = "+cc_append_course_issues_to_file +" : "+courseIssuesFile.getAbsolutePath());
            
        }
        
    }
    
    public void setLangFilter() {
        if(langFilter == null) {
            synchronized(synchObj) {
                if(langFilter == null) {
                    List<Language> langs = canonicalDAO.getLanguages();
                    StringBuilder sBuilder = new StringBuilder();
                    sBuilder.append("(");
                    int y = 0;
                    for(Language lang : langs){
                        sBuilder.append(lang.getLanguageid());
                        sBuilder.append("$");
                        if(y < langs.size() - 1) {
                            sBuilder.append("|");
                            y++;
                        }else {
                            break;
                        }
                    }
                    sBuilder.append(")");
                    langFilter = sBuilder.toString();
                    logger.info("setLangFilter(): langFilter = "+langFilter);
                }
            }
        }
    }
    

/* ***********      Student    ************************ */
    public void insertNewStudentGivenSumtotalCourseCompletion(Exchange exchange) throws Exception {
        SumtotalCourseCompletion stCourseCompletion = (SumtotalCourseCompletion)exchange.getIn().getBody();
        String studentEmail = stCourseCompletion.getEmail();
        String ccDate = stCourseCompletion.getAttemptEndDateString();
        String firstName = stCourseCompletion.getFirstName();
        String lastName = stCourseCompletion.getLastName();
        insertNewStudent(exchange, studentEmail, ccDate, firstName, lastName);
    }
    
    public void insertNewStudentGivenDokeosCourseCompletion(Exchange exchange) throws Exception {
        
        DokeosCourseCompletion dokeosCourseCompletion = (DokeosCourseCompletion) exchange.getIn().getBody();
        String studentEmail = dokeosCourseCompletion.getEmail();
        String ccDate = dokeosCourseCompletion.getAssessmentDate();
        String firstName = dokeosCourseCompletion.calculateFirstName();
        String lastName = dokeosCourseCompletion.calculateLastName();
        insertNewStudent(exchange,studentEmail, ccDate, firstName, lastName );
    }
    
    private void insertNewStudent(Exchange exchange, String studentEmail, String courseCompletionDate, String firstName, String lastName) throws Exception {
        int companyId = 0;

        // 0)  Exchange should return from this function with the same body it came in with
        Object eBody = exchange.getIn().getBody();
        
        // 1)  Determine a companyId that the student is affiliated with
        if(studentEmail.indexOf(RED_HAT_SUFFIX) > 0)
            
            // 1.1) If RHT student, then identify (and cache) companyId for Red Hat
            if(this.rhtCompanyId == 0)
                companyId = canonicalDAO.getCompanyID(Company.RED_HAT_COMPANY_NAME);
            else
                companyId = this.rhtCompanyId;
        else {
            // TO-DO:  https://github.com/redhat-gpe/OPEN_Reporting/issues/40
            
            // 1.2)  If not RHT student, then query GPTE LDAP for student info (to include company name)
            CamelContext cContext = exchange.getContext();
            Student studentIn = new Student();
            studentIn.setEmail(studentEmail);
            Endpoint endpoint = cContext.getEndpoint(GET_STUDENT_ATTRIBUTES_FROM_IPA_URI);
            exchange.setPattern(ExchangePattern.InOut);
            Message in = exchange.getIn();
            in.setBody(studentIn);
            Producer producer = null;
            Exchange getCompanyExchange = null;
            Student studentOut = null;
            try {
                producer = endpoint.createProducer();
                producer.start();
                getCompanyExchange = producer.createExchange();
                getCompanyExchange.setPattern(ExchangePattern.InOut);
                getCompanyExchange.getIn().setBody(studentIn);
                producer.process(getCompanyExchange);
                studentOut = (Student)getCompanyExchange.getIn().getBody();
                String ldapCompanyName = studentOut.getCompanyName();
                logger.info(studentEmail+" : insertNewStudent() about to identify companyId using companyname = "+ldapCompanyName);
                if(StringUtils.isNotEmpty(studentOut.getCompanyName())){
                    try {
                        
                        // 1.3)  Determine if company name retrieved from LDAP has already been persisted in the Companies table
                        companyId = this.canonicalDAO.getCompanyID(ldapCompanyName);
                    } catch(org.springframework.dao.EmptyResultDataAccessException x) {
                        
                        // 1.4)  Company (as per company name retrieved from LDAP) has not yet been persisted in the Companies table
                        //       Create and persist a new Company
                        StringBuilder sBuilder = new StringBuilder("insertNewStudent() Will now persist new company from info retrieved from ldap: ");
                        sBuilder.append("\n\temail: "+studentEmail);
                        sBuilder.append("\n\tcompany: "+ldapCompanyName);
                        logger.info(sBuilder.toString());

                        Company companyObj = new Company();
                        companyObj.setCompanyname(ldapCompanyName);
                        companyObj.setLdapId(ldapCompanyName);
                        companyId = this.canonicalDAO.updateCompany(companyObj);
                    }
                }else {
                    StringBuilder sBuilder = new StringBuilder("insertNewStudent() not able to identify company information for this student");
                    sBuilder.append("\nCourse completion info as follows:");
                    sBuilder.append("\n\temail: "+studentEmail);
                    sBuilder.append("\n\tassessmentDate: "+courseCompletionDate);
                    throw new RuntimeException(sBuilder.toString());
                }
            } finally {
                try {
                    if(producer != null)
                        producer.stop();
                } catch(Exception y) {
                    y.printStackTrace();
                }
            }
        }
        
        // 2)  Create and persist student
        Student studentObj = new Student();
        studentObj.setEmail(studentEmail);
        studentObj.setFirstname(firstName);
        studentObj.setLastname(lastName);
        studentObj.setCompanyid(companyId);
        canonicalDAO.updateStudent(studentObj);

        // reset exchange with original body
        exchange.getIn().setBody(eBody);
    } 
/**
 * @throws IOException ******************************************************************/
    
    
    

/* **************       Student Courses        ********************** */
    
    public CourseCompletion convertSumtotalCourseCompletionToStudentCourse(@Body SumtotalCourseCompletion stCC) throws IOException {
        
        // 1)  Identify student
        Student student = canonicalDAO.getStudentByEmail(stCC.getEmail()); // throws org.springframework.dao.EmptyResultDataAccessException
        
        // 2)  Identify canonical course name given a sumtotal "activity code"
        if(StringUtils.isEmpty(this.langFilter)){
            this.setLangFilter();
        }
        String aCode = stCC.getActivityCode();
        aCode = aCode.replaceAll(this.langFilter, "");
        aCode = aCode.replaceAll(this.underscoreFilter, "");
        aCode = aCode.replaceAll(this.dashFilter, "");
        stCC.setActivityCode(aCode);
        logger.info(stCC.getEmail()+" : converting from sumtotal course completion to canonical StudentCourse. ActivityCode = "+stCC.getActivityCode());
        
        Course course = null;
        if(courseMapTempCache.containsKey(stCC.getActivityCode())){
            course = courseMapTempCache.get(stCC.getActivityCode());
            if(course == null) {
                throw new RuntimeException("Unable to locate a course with the following course Id: \""+stCC.getActivityCode()+"\"");
            }
        } else {
            try {
                course = canonicalDAO.getCourseByCourseId(stCC.getActivityCode());
                this.courseMapTempCache.put(stCC.getActivityCode(), course);
            } catch(org.springframework.dao.EmptyResultDataAccessException x) {
                courseMapTempCache.put(stCC.getActivityCode(), null);
                if(cc_append_course_issues_to_file ) {
                    String output = "\n"+stCC.getActivityCode()+","+stCC.getActivityName();
                    Files.write(Paths.get(courseIssuesFile.getAbsolutePath()), output.getBytes(), StandardOpenOption.APPEND);
                }
                throw new RuntimeException("Unable to locate a course with the following course Id: \""+stCC.getActivityCode()+"\"");
            }
        }
        
        // 3)  Create StudentCourse object
        Language language = new Language();
        language.setLanguageid(ENGLISH);
        StudentCourse sCourse = new StudentCourse();
        sCourse.setStudentid(student.getStudentid());
        sCourse.setCourseid(course.getCourseid());
        sCourse.setLanguageid(language.getLanguageid());
        sCourse.setAssessmentdate(new Timestamp(stCC.getAttemptEndDate().getTime()));
        
        String dSuccess = stCC.getCompletionStatus();
        if(StringUtils.isNotEmpty(dSuccess)) {
            if(dSuccess.contains(SUMTOTAL_COMPLETED))
                dSuccess = StudentCourse.ResultTypes.Pass.name();
            else
                dSuccess = StudentCourse.ResultTypes.Fail.name();
        }else {
            //throw new RuntimeException("No value for Sumtotal CourseCompletion Status for email = "+student.getEmail()+" : courseName = "+course.getCoursename());
            logger.warn("No value for Sumtotal CourseCompletion Status for email = "+student.getEmail()+" : courseName = "+course.getCoursename());
            dSuccess = StudentCourse.ResultTypes.Pass.name();
        }
        sCourse.setAssessmentresult(dSuccess);
        
        // 4)  Transform into canonical CourseCompletion
        CourseCompletion dStudentCourse = new CourseCompletion(student, course, language, sCourse);
        return dStudentCourse;
    }
    
    public CourseCompletion convertDokeosCourseCompletionToStudentCourse(@Body DokeosCourseCompletion dokeosCourseCompletion) {
        if(StringUtils.isEmpty(dokeosCourseCompletion.getEmail()))
            throw new RuntimeException(ExceptionCodes.GPTE_CC1001+dokeosCourseCompletion.toString());
        
        logger.info(dokeosCourseCompletion.getEmail()+" : converting from dokeos course completion to canonical StudentCourse");

        // If student not found, throws: org.springframework.dao.EmptyResultDataAccessException
        Student student = canonicalDAO.getStudentByEmail(dokeosCourseCompletion.getEmail());
        
        // https://github.com/redhat-gpe/OPEN_Reporting/issues/37
        dokeosCourseCompletion.pruneQuizName();
        
        Course course = null;
        try {
            course = canonicalDAO.getCourseByCourseName(dokeosCourseCompletion.getQuizName(), DokeosCourseCompletion.COURSE_COMPLETION_MAPPING_NAME);
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            StringBuilder sBuilder = new StringBuilder(ExceptionCodes.GPTE_CC1001+"Unable to locate a course with the following course name: \""+dokeosCourseCompletion.getQuizName()+"\"");
            sBuilder.append("\nCourse completion info as follows:");
            sBuilder.append("\n\temail: "+dokeosCourseCompletion.getEmail());
            sBuilder.append("\n\tassessmentDate: "+dokeosCourseCompletion.getAssessmentDate());
            throw new RuntimeException(sBuilder.toString());
        }
        Language language = new Language();
        language.setLanguageid(ENGLISH);
        StudentCourse sCourse = new StudentCourse();
        sCourse.setStudentid(student.getStudentid());
        sCourse.setCourseid(course.getCourseid());
        sCourse.setLanguageid(language.getLanguageid());
        
        
        try {
            sCourse.setAssessmentdate(convertDokeosAssessmentDateToCanonical(dokeosCourseCompletion.getAssessmentDate()));
        } catch(ParseException x) {
            throw new RuntimeException("Not able to parse the following dokeos date for: email = "+student.getEmail()+" : courseName = "+course.getCoursename()+" : date = "+dokeosCourseCompletion.getAssessmentDate());
        }
        
        byte canonicalScore = convertDokeosAssessmentScoreToCanonical(dokeosCourseCompletion.getScore());
        sCourse.setAssessmentscore(canonicalScore);
        
        String dSuccess = null;
        if(canonicalScore > coursePassingValue)
            dSuccess = StudentCourse.ResultTypes.Pass.name();
        else
            dSuccess = StudentCourse.ResultTypes.Fail.name();
        sCourse.setAssessmentresult(dSuccess);
        
        CourseCompletion dStudentCourse = new CourseCompletion(student, course, language, sCourse);
        return dStudentCourse;
    }
    
    private Timestamp convertDokeosAssessmentDateToCanonical(String dateString) throws ParseException {
        Date dokeosDate = DokeosCourseCompletion.dokeosSDF.parse(dateString);
        return new Timestamp(dokeosDate.getTime());
    }
    
    private byte convertDokeosAssessmentScoreToCanonical(String dScore) {
        String tempScore = dScore.substring(0, dScore.indexOf("%"));
        byte tempByte = Byte.valueOf(tempScore);
        return tempByte;
    }
    
    public void addStudentCourseToDB(@Body CourseCompletion sCourseWrapper) {
        StudentCourse studentCourse = sCourseWrapper.getStudentCourse();
        canonicalDAO.addStudentCourse(studentCourse);
    }
    
    public boolean isNewStudentCourseForStudent(@Body StudentCourse sCourse) {
        boolean result = canonicalDAO.isNewStudentCourseForStudent(sCourse);
        return result;
    }
/* ****************************************************************************** */

    

    
    
 
}
