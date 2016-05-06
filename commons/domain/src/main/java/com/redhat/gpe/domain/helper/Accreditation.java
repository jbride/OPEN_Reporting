package com.redhat.gpe.domain.helper;

import java.sql.Timestamp;
import java.util.Date;

import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentAccreditation;

/* 
 * Denormalized wrapper of a student accrediation
 */
public class Accreditation {
    private AccreditationDefinition accreditationDefinition;
    private Student student;
    private Course course;
    private StudentAccreditation studentAccred;
    
    private Integer accreditationId = 0;
    private String  accreditationName;
    private Integer studentId = 0;
    private String  email;
    private String courseId;
    private String courseName;
    
    private Date accreditationDate;
    private String accreditationType = StudentAccreditation.Types.Active.name();
    private String ruleFired;  // name of rule that instantiated this Accreditation object
    
    public Accreditation() {
        this.accreditationDefinition = new AccreditationDefinition();
        this.student = new Student();
        this.course = new Course();
        this.studentAccred = new StudentAccreditation();
        
        if(accreditationDate == null)
            this.setAccreditationDate(new Timestamp(new Date().getTime()));
    }
    public String getRuleFired() {
        return ruleFired;
    }
    public void setRuleFired(String x) {
        ruleFired = x;
    }
    public AccreditationDefinition getAccreditation() {
        return accreditationDefinition;
    }
    public void setAccreditation(AccreditationDefinition accredObj) {
        this.accreditationDefinition = accredObj;
        this.accreditationId = accredObj.getAccreditationid();
        this.accreditationName = accredObj.getAccreditationname();
    }
    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
        this.email = student.getEmail();
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
        this.courseId = course.getCourseid();
        this.courseName = course.getCoursename();
    }
    public StudentAccreditation getStudentAccred() {
        return studentAccred;
    }
    public void setStudentAccred(StudentAccreditation sAccredObj) {
        this.studentAccred = sAccredObj;
        this.accreditationDate = sAccredObj.getAccreditationdate();
        this.accreditationType = sAccredObj.getAccreditationtype();
    }
    
    public Date determineExperitionDate() {
        return studentAccred.determineExperitionDate();
    }
    public Integer getAccreditationId() {
        return accreditationId;
    }
    public void setAccreditationId(Integer accreditationId) {
        this.accreditationId = accreditationId;
        accreditationDefinition.setAccreditationid(accreditationId);
        studentAccred.setAccreditationid(accreditationId);
    }
    public String getAccreditationName() {
        return accreditationName;
    }
    public void setAccreditationName(String accreditationName) {
        this.accreditationName = accreditationName;
        this.accreditationDefinition.setAccreditationname(accreditationName);
    }
    public Integer getStudentId() {
        return studentId;
    }
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
        this.studentAccred.setStudentid(studentId);
        this.student.setStudentid(studentId);
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
        this.student.setEmail(email);
    }
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
        this.studentAccred.setCourseid(courseId);
        this.course.setCourseid(courseId);
    }
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
        this.course.setCoursename(courseName);
    }
    public Date getAccreditationDate() {
        return accreditationDate;
    }
    public void setAccreditationDate(Date accreditationDate) {
        this.accreditationDate = accreditationDate;
        this.studentAccred.setAccreditationdate(new Timestamp(accreditationDate.getTime()));
    }
    public String getAccreditationType() {
        return accreditationType;
    }
    public void setAccreditationType(String accreditationType) {
        this.accreditationType = accreditationType;
        this.studentAccred.setAccreditationtype(accreditationType);
    }
}
