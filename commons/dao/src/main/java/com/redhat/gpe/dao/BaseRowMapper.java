package com.redhat.gpe.dao;

import com.redhat.gpe.domain.canonical.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseRowMapper {
    
    public Company getCompany(ResultSet rs) throws SQLException {
        Company cObj = new Company();
        cObj.setCompanyid(rs.getInt(Company.COMPANY_ID));
        cObj.setCompanyname(rs.getString(Company.COMPANY_NAME));
        cObj.setPartnertype(rs.getString(Company.PARTNER_TYPE));
        cObj.setPartnertier(rs.getString(Company.PARTNER_TIER));
        cObj.setLdapId(rs.getString(Company.LDAP_ID));
        cObj.setSfdcId(rs.getString(Company.SFDC_ID));
        return cObj;
    }
    
    protected Student getStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentid(rs.getInt(Student.STUDENT_ID));
        student.setEmail(rs.getString(Student.EMAIL));
        student.setFirstname(rs.getString(Student.FIRST_NAME));
        student.setLastname(rs.getString(Student.LAST_NAME));
        student.setCompanyid(rs.getInt(Student.COMPANY_ID));
        student.setRegion(rs.getString(Student.REGION));
        student.setSubregion(rs.getString(Student.SUBREGION));
        student.setCountry(rs.getString(Student.COUNTRY));
        student.setRoles(rs.getString(Student.ROLES));
        student.setSalesforcecontactid(rs.getString(Student.SALES_FORCE_CONTACT_ID));
        student.setSalesforceactive(rs.getString(Student.SALES_FORCE_ACTIVE));
        student.setSumtotalid(rs.getString(Student.SUMTOTAL_ID));
        student.setSumtotalactive(rs.getString(Student.SUMTOTAL_ACTIVE));
        student.setSkillsbaseStatus(rs.getShort(Student.SKILLSBASE_STATUS));
        student.setIpaStatus(rs.getShort(Student.IPA_STATUS));
        student.setActivationDate(rs.getTimestamp(Student.ACTIVATION_DATE));
        student.setDeActivationDate(rs.getTimestamp(Student.DEACTIVATION_DATE));
        student.setSkillsbasePartner(rs.getInt(Student.SKILLSBASEPARTNER));
        return student;
    }
    
    protected Course getCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseid(rs.getString(Course.COURSE_ID));
        course.setCoursename(rs.getString(Course.COURSE_NAME));
        return course;
    }
   
    protected Language getLanguage(ResultSet rs) throws SQLException {
        Language language = new Language();
        language.setLanguageid(rs.getString(Language.LANGUAGE_ID));
        language.setLanguagename(rs.getString(Language.LANGUAGE_NAME));
        return language;
    }
    
    protected StudentCourse getStudentCourse(ResultSet rs) throws SQLException {
        StudentCourse sCourse = new StudentCourse();
        sCourse.setStudentcourseid(rs.getInt(StudentCourse.STUDENT_COURSE_ID));
        sCourse.setStudentid(rs.getInt(StudentCourse.STUDENT_ID));
        sCourse.setCourseid(rs.getString(StudentCourse.COURSE_ID));
        sCourse.setLanguageid(rs.getString(StudentCourse.LANGUAGE_ID));
        sCourse.setAssessmentdate(rs.getTimestamp(StudentCourse.ASSESSMENT_DATE));
        sCourse.setAssessmentresult(rs.getString(StudentCourse.ASSESSMENT_RESULT));
        sCourse.setAssessmentscore(rs .getByte(StudentCourse.ASSESSMENT_SCORE));
        sCourse.setProcessed(rs.getShort(StudentCourse.PROCESSED));
        return sCourse;
    }
    
    public AccreditationDefinition getAccreditation(ResultSet rs) throws SQLException {
        AccreditationDefinition accredObj = new AccreditationDefinition();
        accredObj.setAccreditationid(rs.getInt(AccreditationDefinition.ACCREDITATION_ID));
        accredObj.setAccreditationname(rs.getString(AccreditationDefinition.ACCREDITATION_NAME));
        accredObj.setRole(rs.getString(AccreditationDefinition.ROLE));
        accredObj.setSpecialization(rs.getString(AccreditationDefinition.SPECIALIZATION));
        accredObj.setTrack(rs.getString(AccreditationDefinition.TRACK));
        accredObj.setProficiency(rs.getString(AccreditationDefinition.PROFICIENCY));
        accredObj.setAccreditationexportid(rs.getString(AccreditationDefinition.ACCREDITATION_EXPORT_ID));
        return accredObj;
    }
    
    protected StudentAccreditation getStudentAccreditation(ResultSet rs) throws SQLException {
        StudentAccreditation sAccredObj = new StudentAccreditation();
        sAccredObj.setStudentid(rs.getInt(StudentAccreditation.STUDENT_ID));
        sAccredObj.setAccreditationid(rs.getInt(StudentAccreditation.ACCRED_ID));
        sAccredObj.setAccreditationdate(rs.getTimestamp(StudentAccreditation.ACCRED_DATE));
        sAccredObj.setAccreditationtype(rs.getString(StudentAccreditation.ACCRED_TYPE));
        sAccredObj.setCourseid(rs.getString(StudentAccreditation.COURSE_ID));
        sAccredObj.setRuleFired(rs.getString(StudentAccreditation.RULE_FIRED));
        sAccredObj.setProcessed(rs.getShort(StudentAccreditation.PROCESSED));
        return sAccredObj;
    }

}
