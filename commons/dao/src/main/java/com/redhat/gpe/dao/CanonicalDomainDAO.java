package com.redhat.gpe.dao;

import java.util.List;

import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Course;
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

    /* *******  Students  ********/
    public Student getStudentByEmail(String email);
    public List<DenormalizedStudent> selectStudentsByIpaStatus(int status);
    public List<Student> selectRHTStudentsWithMissingAttributes();
    public void updateStudent(Student emp);
    public boolean doesStudentExist(String email);
    public boolean hasThisStudentBeenEmailedBefore(String email);
    public void updateStudentStatusForEmailedAlready(String email);
    public void updateStudentStatusForOk(String email);
    
    
    /********  Course  ********/
    public Course getCourseByCourseName(String courseName, String sourceName);
    public List<Course> listCanonicalCourses();
    
    
    /* *******  Student Courses  ********/
    public void addStudentCourse(StudentCourse sCourse);
    public int updateStudentCourseProcessedByStudent(Student studentObj, int processedValue);
    public int updateStudentCourseProcessedByStudent(Integer studentId, int processedValue);
    public List<Integer> selectStudentIdsWithStudentCoursesByStatus(int processedStatus);
    public List<Integer> selectStudentIdsWithStudentCoursesByStatus(int processedStatus, int lowStudentId, int highStudentId);
    public List<Integer> selectStudentCourseIdsByStatus(int processedStatus);
    public List<CourseCompletion> selectPassedStudentCoursesByStudent(int studentId);
    public boolean isNewStudentCourseForStudent(StudentCourse sCourse);
    
    
    /* *******  Accreditations  ********/
    public int getAccreditationIdGivenName(String accredName);
    public List<Accreditation> selectUnprocessedStudentAccreditationsByProcessStatus(int processed);
    public void addStudentAccreditation(StudentAccreditation sAccredObj);
    
    /* ****** Stored Procedures  *******/
    public void triggerStoredProcedure(String storedProcCall);

}
