package com.redhat.gpe.domain.helper;

import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Language;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentCourse;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CourseCompletion {
    
    private Course course;
    private Student student;
    private Language language;
    private StudentCourse studentCourse;
    
    private String courseId;
    private String courseName;
    
    private String languageId;
    private String languageName;
    
    private int studentCourseId = 0;
    private Date assessmentDate;
    private String formattedAssessmentDate;
    private String assessmentResult;
    private byte assessmentScore;
    private short processed = 0;
    private boolean mostRecentCourseCompletion = false;
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

    public CourseCompletion(){}

    public CourseCompletion(Student sObj, Course courseObj, Language language, StudentCourse studentCourse) {
        student = sObj;
        setCourse(courseObj);
        setLanguage(language);
        setStudentCourse(studentCourse);
    }
    
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
        this.courseId = course.getCourseid();
        this.courseName = course.getCoursename();
    }
    public Student getStudent() {
        return student;
    }
    
    public StudentCourse getStudentCourse() {
        return studentCourse;
    }
    public void setStudentCourse(StudentCourse studentCourse) {
        this.studentCourse = studentCourse;
        this.studentCourseId = studentCourse.getStudentcourseid();
        this.assessmentDate = studentCourse.getAssessmentdate();
        this.assessmentResult = studentCourse.getAssessmentresult();
        this.assessmentScore = studentCourse.getAssessmentscore();
        this.processed  = studentCourse.isProcessed();
    }
    public Language getLanguage() {
        return language;
    }
    public void setLanguage(Language language) {
        this.language = language;
        this.languageId = language.getLanguageid();
        this.languageName = language.getLanguagename();
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public int getStudentCourseId() {
        return studentCourseId;
    }

    public void setStudentCourseId(int studentCourseId) {
        this.studentCourseId = studentCourseId;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getAssessmentResult() {
        return assessmentResult;
    }

    public void setAssessmentResult(String assessmentResult) {
        this.assessmentResult = assessmentResult;
    }

    public byte getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(byte assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public short getProcessed() {
        return processed;
    }

    public void setProcessed(short isProcessed) {
        this.processed = isProcessed;
    }

    public void setMostRecentCourseCompletion(boolean b) {
        this.mostRecentCourseCompletion = b;
    }
    public boolean getMostRecentCourseCompletion() {
        return this.mostRecentCourseCompletion;
    }

    @Override
    public String toString() {
        return "CourseCompletion [course=" + course + ", student=" + student
                + ", language=" + language + ", studentCourse=" + studentCourse
                + ", courseName=" + courseName + ", languageId=" + languageId
                + ", languageName=" + languageName + ", studentCourseId="
                + studentCourseId + ", assessmentDate=" + assessmentDate
                + ", assessmentResult=" + assessmentResult
                + ", assessmentScore=" + assessmentScore + ", processed="
                + processed + ", mostRecentCourseCompletion="
                + mostRecentCourseCompletion + "]";
    }

    public String getFormattedAssessmentDate() {
        return sdf.format(assessmentDate);
    }

    public void setFormattedAssessmentDate(String formattedAssessmentDate) {
        this.formattedAssessmentDate = formattedAssessmentDate;
    }
}
