package com.redhat.gpe.dao;

import java.util.List;

import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Language;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentCourse;
import com.redhat.gpe.domain.canonical.StudentAccreditation;
import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpe.domain.helper.DenormalizedStudent;

public interface CanonicalDomainDAO {
    
    /* *******   Company   *********/
    public int updateCompany(Company companyObj);
    public int getCompanyID(String companyName);
    public Company getCompanyGivenLdapId(String ldapId);

    /* *******  Students  ********/
    public Student getStudentByEmail(String email);
    public List<DenormalizedStudent> selectStudentsByIpaStatus(int status);
    public List<Student> selectRHTStudentsWithMissingAttributes();
    public void updateStudent(Student emp) throws Exception;
    public boolean doesStudentExist(String email);
    public boolean hasThisStudentBeenEmailedBefore(String email);
    public void updateStudentStatusForEmailedAlready(String email);
    public void updateStudentStatusForOk(String email);
    public int updateStudentStatus(String email, int statusCode, String field);
    
    
    /********  Course  ********/
    public Course getCourseByCourseName(String courseName, String sourceName);
    public Course getCourseByCourseId(String courseId);
    public List<Course> listCanonicalCourses();
    public int[] deleteAllFromCoursesAndCourseMappings();
    
    
    /* *******  Student Courses  ********/
    public void addStudentCourse(StudentCourse sCourse);
    public int updateStudentCourseProcessedByStudent(Student studentObj, int processedValue);
    public int updateStudentCourseProcessedByStudent(Integer studentId, int processedValue);
    public List<Integer> selectStudentIdsWithStudentCoursesByStatus(int processedStatus);
    public List<Integer> selectStudentIdsWithStudentCoursesByStatus(int processedStatus, int lowStudentId, int highStudentId);
    public List<Integer> selectStudentCourseIdsByStatus(int processedStatus);
    public List<CourseCompletion> selectPassedStudentCoursesByStudent(int studentId);
    public boolean isNewStudentCourseForStudent(StudentCourse sCourse);
    public int getUniqueStudentCourseCount(StudentCourse scObj);
    
    
    /* *******  Accreditations  ********/
    public int getAccreditationIdGivenName(String accredName);
    public List<Accreditation> selectUnprocessedStudentAccreditationsByProcessStatus(int processed, String studentEmailSuffix);
    public int changeStatusOnExpiredStudentAccreditations();
    public void addStudentAccreditation(StudentAccreditation sAccredObj);
    
    /* ******* Languages  ********* */
    public List<Language> getLanguages();
    
    /* ****** Stored Procedures  *******/
    public void triggerStoredProcedure(String storedProcCall);

}
