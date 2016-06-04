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
import java.util.Map;

public class CourseCompletionServiceBean extends GPTEBaseServiceBean {

    
    private static final String ENGLISH = "EN_US";
    private static final String SUMTOTAL_COMPLETED = "COMPLETED";
    private static final byte coursePassingValue = 70;
    public static final String GET_STUDENT_ATTRIBUTES_FROM_IPA_URI = "vm:get-student-attributes-from-ipa";
    private static final String CC_APPEND_COURSE_ISSUES_TO_FILE = "cc_append_course_issues_to_file";
    private static final String COURSE_ISSUES_OUTPUT = "/tmp/gpte_course_issues.txt";

    private Logger logger = Logger.getLogger(getClass());
    private boolean cc_append_course_issues_to_file = false;
    private File courseIssuesFile = null;
    private Map<String, Course> courseMapTempCache = new HashMap<String, Course>();  // <mappedCourseId, Course obj>
    
    public CourseCompletionServiceBean() throws IOException {
        String x = System.getProperty(CC_APPEND_COURSE_ISSUES_TO_FILE);
        if(StringUtils.isNotEmpty(x)){
            cc_append_course_issues_to_file = Boolean.parseBoolean(x);
            courseIssuesFile = new File(COURSE_ISSUES_OUTPUT);
            courseIssuesFile.createNewFile();
            FileOutputStream fStream = null;
            try {
                fStream = new FileOutputStream(courseIssuesFile);
                String output = "CourseId,CourseName";
                fStream.write(output.getBytes());
                fStream.flush();
            } finally {
                if(fStream != null)
                    fStream.close();
            }
            logger.info("CourseCompletionServiceBean:  cc_append_course_issues_to_file = "+cc_append_course_issues_to_file +" : "+courseIssuesFile.getAbsolutePath());
        }
    }
    

/* ***********      Student    ************************ */
    public void insertNewStudentGivenDokeosCourseCompletion(Exchange exchange) throws Exception {
        DokeosCourseCompletion dokeosCourseCompletion = (DokeosCourseCompletion) exchange.getIn().getBody();
        String studentEmail = dokeosCourseCompletion.getEmail();
        int companyId = 0;
        if(studentEmail.indexOf(RED_HAT_SUFFIX) > 0)
            companyId = canonicalDAO.getCompanyID(Company.RED_HAT_COMPANY_NAME);
        else {
            // TO-DO:  https://github.com/redhat-gpe/OPEN_Reporting/issues/40
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
                logger.info(studentEmail+" : insertNewStudentGivenDokeosCourseCompletion() about to identify companyId using companyname = "+studentOut.getCompanyName());
                if(StringUtils.isNotEmpty(studentOut.getCompanyName())){
                    companyId = this.getCompanyID(studentOut.getCompanyName());
                }else {
                    StringBuilder sBuilder = new StringBuilder(ExceptionCodes.GPTE_CC1001+studentEmail+" : insertNewStudentGivenDokeosCourseCompletion() not able to identify company information for this student");
                    sBuilder.append("\nCourse completion info as follows:");
                    sBuilder.append("\n\temail: "+dokeosCourseCompletion.getEmail());
                    sBuilder.append("\n\tassessmentDate: "+dokeosCourseCompletion.getAssessmentDate());
                    throw new RuntimeException(sBuilder.toString());
                }
            } catch(org.springframework.dao.EmptyResultDataAccessException x) {
                StringBuilder sBuilder = new StringBuilder(ExceptionCodes.GPTE_CC1001+"insertNewStudentGivenDokeosCourseCompletion() no company name found with name = "+studentOut.getCompanyName());
                sBuilder.append("\nCourse completion info as follows:");
                sBuilder.append("\n\temail: "+dokeosCourseCompletion.getEmail());
                sBuilder.append("\n\tassessmentDate: "+dokeosCourseCompletion.getAssessmentDate());
                throw new RuntimeException(sBuilder.toString());
            } finally {
                try {
                    if(producer != null)
                        producer.stop();
                } catch(Exception y) {
                    y.printStackTrace();
                }
            }
        }
        
        Student studentObj = new Student();
        studentObj.setEmail(studentEmail);
        studentObj.setFirstname(dokeosCourseCompletion.calculateFirstName());
        studentObj.setLastname(dokeosCourseCompletion.calculateLastName());
        studentObj.setCompanyid(companyId);
        canonicalDAO.updateStudent(studentObj);
    } 
/**
 * @throws IOException ******************************************************************/
    
    
    

/* **************       Student Courses        ********************** */
    
    public CourseCompletion convertSumtotalCourseCompletionToStudentCourse(@Body SumtotalCourseCompletion stCC) throws IOException {
        
        logger.info(stCC.getEmail()+" : converting from sumtotal course completion to canonical StudentCourse");
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
        
        Student student = null;
        try {
            student = canonicalDAO.getStudentByEmail(stCC.getEmail());
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            throw new RuntimeException("Unable to locate a student with the following email: "+stCC.getEmail());
        }
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
            throw new RuntimeException("No value for Sumtotal Completion Status for email = "+student.getEmail()+" : courseName = "+course.getCoursename());
        }
        sCourse.setAssessmentresult(dSuccess);
        
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
