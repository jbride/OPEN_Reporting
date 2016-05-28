package com.redhat.gpe.coursecompletion.service;

import com.redhat.gpe.coursecompletion.dokeos.DokeosCourseCompletion;
import com.redhat.gpe.coursecompletion.sumtotal.SumtotalCourseCompletion;
import com.redhat.gpe.domain.canonical.*;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpte.services.ExceptionCodes;
import com.redhat.gpte.services.GPTEBaseServiceBean;
import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

public class CourseCompletionServiceBean extends GPTEBaseServiceBean {

    
    private static final String ENGLISH = "EN_US";
    private static final String SUMTOTAL_COMPLETED = "COMPLETED";
    private static final byte coursePassingValue = 70;

    private Logger logger = Logger.getLogger(getClass());
    

/* ***********      Student    ************************ */
    public void insertNewStudentGivenDokeosCourseCompletion(@Body DokeosCourseCompletion dokeosCourseCompletion) {
        String studentEmail = dokeosCourseCompletion.getEmail();
        int companyId = 0;
        if(studentEmail.indexOf(RED_HAT_SUFFIX) > 0)
            companyId = canonicalDAO.getCompanyID(Company.RED_HAT_COMPANY_NAME);
        else 
            throw new RuntimeException(ExceptionCodes.GPTE_CC1000+studentEmail);
        
        Student studentObj = new Student();
        studentObj.setEmail(studentEmail);
        studentObj.setFirstname(dokeosCourseCompletion.calculateFirstName());
        studentObj.setLastname(dokeosCourseCompletion.calculateLastName());
        studentObj.setCompanyid(companyId);
        canonicalDAO.updateStudent(studentObj);
    } 
/********************************************************************/
    
    
    

/* **************       Student Courses        ********************** */
    
    public CourseCompletion convertSumtotalCourseCompletionToStudentCourse(@Body SumtotalCourseCompletion stCC) {
        
        logger.info(stCC.getEmail()+" : converting from sumtotal course completion to canonical StudentCourse");
        Student student = null;
        try {
            student = canonicalDAO.getStudentByEmail(stCC.getEmail());
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            throw new RuntimeException("Unable to locate a student with the following email: "+stCC.getEmail());
        }
        Course course = null;
        try {
            course = canonicalDAO.getCourseByCourseName(stCC.getActivityName(), null);
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            throw new RuntimeException("Unable to locate a course with the following course name: \""+stCC.getActivityName()+"\"");
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

        // https://github.com/redhat-gpe/OPEN_Reporting/issues/37
        dokeosCourseCompletion.pruneQuizName();
        
        // If student not found, throws: org.springframework.dao.EmptyResultDataAccessException
        Student student = canonicalDAO.getStudentByEmail(dokeosCourseCompletion.getEmail());
        
        Course course = null;
        try {
            course = canonicalDAO.getCourseByCourseName(dokeosCourseCompletion.getQuizName(), DokeosCourseCompletion.COURSE_COMPLETION_MAPPING_NAME);
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            throw new RuntimeException("Unable to locate a course with the following course name: \""+dokeosCourseCompletion.getQuizName()+"\"");
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
