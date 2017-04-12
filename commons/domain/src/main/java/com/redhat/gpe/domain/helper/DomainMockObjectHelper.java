package com.redhat.gpe.domain.helper;

import java.sql.Timestamp;
import java.util.Date;

import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Language;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentAccreditation;
import com.redhat.gpe.domain.canonical.StudentCourse;

public class DomainMockObjectHelper {
    
    public static final String CLOUDFORMS_FASTRAX = "CloudForms FASTRAX";
    public static final String CLOUDFORMS_IMPLEMENTATION = "CloudForms Implementation";
    
    //static int partnerStudentId = 10145;                   // Abhishek Singh
    //static String partnerEmail = "abhishek139@tcs.com";
    
    public static int partnerStudentId = 10161;
    public static String partnerEmail = "abhishek5.chatterjee5@cognizant.com";
    
    static String rhtEmail = "jbride@redhat.com";
    static int accredId = 19;   // Red Hat Delivery Specialist - Business Process Automation
    static String accredName = "Red Hat Delivery Specialist – Business Process Automation";
    static String courseId = "MWS-SE-BPA-ASM-BRMS";
    static String coursename = "Authoring Rules in BRMS Guided Rule Editor Lab";
    static String languageId = "EN_US";
    static String accredType = StudentAccreditation.Types.Active.name();
    
    static String bpmsImplementationCourseId = "MWS-DEL-BPA-2074-AST";
    static String bpmsImplementationCourseName = "JBoss BPM Suite 6 Implementation";
    
    static String bpmsProjectHoursCourseId = "MWS-DEL-ADBA-6538-AST";
    static String bpmsProjectHoursCourseName = "Advanced Business Automation Project Hours";
    
    static String bpmsAdvancedProcessDevCourseId = "MWS-DEL-ADBA-4238-AST-EN_US";
    static String bpmsAdvancedProcessDevCourseName = "Advanced Process Development";

    static Integer totaraCourseCompletionId = 109436;
    
    public static Company getMockRHTCompany() {
        Company company = new Company();
        company.setCompanyname(Company.RED_HAT_COMPANY_NAME);
        return company;
    }
    
    public static Company getMockNonRHTCompany() {
        Company company = new Company();
        company.setCompanyname("mytestcompany");
        return company;
    }
    
    public static Student getMockStudent() {
        Student studentObj = new Student();
        studentObj.setStudentid(partnerStudentId);
        studentObj.setEmail(partnerEmail);
        return studentObj;
    }

    public static Student getMockRHTStudent() {
        Student studentObj = new Student();
        studentObj.setEmail(rhtEmail);
        return studentObj;
    }
    
    public static Language getMockLanguage() {
        Language langObj = new Language();
        langObj.setLanguageid(languageId);
        return langObj;
    }

    public static Course getMockCourse() {
        Course courseObj = new Course();
        courseObj.setCourseid(courseId);
        courseObj.setCoursename(coursename);
        return courseObj;
    }
    
    public static Course getBPMSImplementationCourse() {
        Course courseObj = new Course();
        courseObj.setCourseid(bpmsImplementationCourseId);
        courseObj.setCoursename(bpmsImplementationCourseName);
        return courseObj;
    }
    
    public static Course getBPMSProjectHoursCourse() {
        Course courseObj = new Course();
        courseObj.setCourseid(bpmsProjectHoursCourseId);
        courseObj.setCoursename(bpmsProjectHoursCourseName);
        return courseObj;
    }
    
    public static Course getBPMSAdvancedCourse() {
        Course courseObj = new Course();
        courseObj.setCourseid(bpmsAdvancedProcessDevCourseId);
        courseObj.setCoursename(bpmsAdvancedProcessDevCourseName);
        return courseObj;
    }

    public static StudentCourse getMockStudentCourse(Integer studentId, String courseId) {
        StudentCourse sCourse = new StudentCourse();
        sCourse.setStudentid(studentId);
        sCourse.setLanguageid(languageId);
        sCourse.setCourseid(courseId);
        sCourse.setAssessmentdate(new Timestamp(new Date().getTime()));
        sCourse.setAssessmentresult(StudentCourse.ResultTypes.Pass.name());
        sCourse.setTotaraCourseCompletionId(totaraCourseCompletionId);
        return sCourse;
    }
    
    public static AccreditationDefinition getMockAccreditation() {
        AccreditationDefinition accred = new AccreditationDefinition();
        accred.setAccreditationid(accredId);
        accred.setAccreditationname(accredName);
        return accred;
    }
    
    public static StudentAccreditation getMockStudentAccreditation(){
        StudentAccreditation sAccredObj = new StudentAccreditation();
        sAccredObj.setStudentid(partnerStudentId);
        sAccredObj.setAccreditationid(accredId);
        sAccredObj.setAccreditationdate(new Timestamp(new Date().getTime()));
        sAccredObj.setCourseid(courseId);
        sAccredObj.setAccreditationtype(accredType);
        return sAccredObj;
    }

}
