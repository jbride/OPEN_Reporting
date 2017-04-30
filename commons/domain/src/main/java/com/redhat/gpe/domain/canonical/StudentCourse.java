/**
 * This class is generated by jOOQ
 */
package com.redhat.gpe.domain.canonical;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StudentCourse implements Serializable {

    private static final long serialVersionUID = 215771531;
    public static final String STUDENT_COURSE_ID = "studentcourseid";
    public static final String STUDENT_ID = "studentid";
    public static final String COURSE_ID = "courseid";
    public static final String LANGUAGE_ID = "languageid";
    public static final String ASSESSMENT_DATE = "assessmentdate";
    public static final String ASSESSMENT_RESULT = "assessmentresult";
    public static final String ASSESSMENT_SCORE = "assessmentscore";
    public static final String PROCESSED = "processed";
    public static final int UNPROCESSED = 0;
    public static final int PROCESSED_ALL = 1;
    public static final String SELECT_CLAUSE = "sc.StudentCourseID, sc.StudentID, sc.CourseID, sc.LanguageID, sc.AssessmentDate, sc.AssessmentResult, sc.AssessmentScore, sc.Processed, sc.totaraCourseCompletionId, sc.totaraCourseCompletionDate";

    private Integer    studentcourseid = 0;

    private Integer    studentid;
    private String     courseid;
    private String     languageid;
    private Timestamp  assessmentdate;
    private String     assessmentresult;
    private byte       assessmentscore = 100;  //passing score is 70% or greater
    private short      processed = UNPROCESSED;
    private Integer    totaraCourseCompletionId;
    private Timestamp  totaraCourseCompletionDate;
    
    public enum ResultTypes {
        Pass,Fail
    }

    public short isProcessed() {
        return processed;
    }

    public void setProcessed(short processed) {
        this.processed = processed;
    }

    public StudentCourse() {}

    public StudentCourse(StudentCourse value) {
        this.studentcourseid = value.studentcourseid;
        this.studentid = value.studentid;
        this.courseid = value.courseid;
        this.languageid = value.languageid;
        this.assessmentdate = value.assessmentdate;
        this.assessmentresult = value.assessmentresult;
        this.assessmentscore = value.assessmentscore;
        this.processed = value.processed;
        this.totaraCourseCompletionId = value.totaraCourseCompletionId;
        this.totaraCourseCompletionDate = value.totaraCourseCompletionDate;
    }

    public StudentCourse(
        Integer    studentcourseid,
        Integer    studentid,
        String     courseid,
        String     languageid,
        Timestamp  assessmentdate,
        String     assessmentresult,
        byte assessmentscore,
        short      processed,
        Integer    totaraCourseCompletionId,
        Timestamp  totaraCourseCompletionDate
    ) {
        this.studentcourseid = studentcourseid;
        this.studentid = studentid;
        this.courseid = courseid;
        this.languageid = languageid;
        this.assessmentdate = assessmentdate;
        this.assessmentresult = assessmentresult;
        this.assessmentscore = assessmentscore;
        this.processed = processed;
        this.totaraCourseCompletionId = totaraCourseCompletionId;
        this.totaraCourseCompletionDate = totaraCourseCompletionDate;
    }

    public Integer getStudentcourseid() {
        return this.studentcourseid;
    }

    public void setStudentcourseid(Integer studentcourseid) {
        this.studentcourseid = studentcourseid;
    }

    public Integer getStudentid() {
        return this.studentid;
    }

    public void setStudentid(Integer studentid) {
        this.studentid = studentid;
    }

    public String getCourseid() {
        return this.courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getLanguageid() {
        return this.languageid;
    }

    public void setLanguageid(String languageid) {
        this.languageid = languageid;
    }

    public Timestamp getAssessmentdate() {
        return this.assessmentdate;
    }

    public void setAssessmentdate(Timestamp assessmentdate) {
        this.assessmentdate = assessmentdate;
    }

    public String getAssessmentresult() {
        return this.assessmentresult;
    }

    public void setAssessmentresult(String assessmentresult) {
        this.assessmentresult = assessmentresult;
    }

    public byte getAssessmentscore() {
        return this.assessmentscore;
    }

    public void setAssessmentscore(byte assessmentscore) {
        this.assessmentscore = assessmentscore;
    }

    public Integer getTotaraCourseCompletionId() {
        return totaraCourseCompletionId;
    }
    public void setTotaraCourseCompletionId(Integer x) {
        this.totaraCourseCompletionId = x;
    }

    public Timestamp getTotaraCourseCompletionDate() {
        return this.totaraCourseCompletionDate;
    }

    public void setTotaraCourseCompletionDate(Timestamp x) {
        this.totaraCourseCompletionDate = x;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Studentcourses (");

        sb.append(studentcourseid);
        sb.append(", ").append(studentid);
        sb.append(", ").append(courseid);
        sb.append(", ").append(languageid);
        sb.append(", ").append(assessmentdate);
        sb.append(", ").append(assessmentresult);
        sb.append(", ").append(assessmentscore);
        sb.append(", ").append(processed);
        sb.append(", ").append(totaraCourseCompletionId);
        sb.append(", ").append(totaraCourseCompletionDate);

        sb.append(")");
        return sb.toString();
    }
}
