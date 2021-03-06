/**
 * This class is generated by jOOQ
 */
package com.redhat.gpe.domain.canonical;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StudentAccreditation implements Serializable {

    private static final long serialVersionUID = -2084285653;
    public static final String STUDENT_ID = "studentid";
    public static final String ACCRED_ID = "accreditationid";
    public static final String ACCRED_DATE = "accreditationdate";
    public static final String ACCRED_TYPE = "accreditationtype";
    public static final String COURSE_ID = "courseid";
    public static final String RULE_FIRED = "ruleFired";
    public static final String PROCESSED = "processed";
    public static final short UNPROCESSED = 0;
    public static final short PROCESSED_SKILLS_BASE_ONLY = 1;
    public static final short PROCESSED_SALESFORCE_ONLY = 2;
    public static final short PROCESSED_ALL = 10;
    public static final short NOT_UPLOADED = 0;
    public static final String SALESFORCE_UPLOADED = "salesforceuploaded";
    public static final String SELECT_CLAUSE = "sa.AccreditationID,sa.AccreditationDate,sa.AccreditationType,sa.CourseID,sa.Processed,sa.RuleFired,sa.SalesForceUploaded";

    private Integer   studentid;
    private Integer   accreditationid;
    private Timestamp accreditationdate;
    private String    accreditationtype;

    /*  courseId of the StudentCourse leading to an accreditation in a particular learning path.
     *  if new accreditation:  courseId of StudentCourse that triggered new accreditation
     *  else existing accreditation, example as follows:
     *      if the rule is [(A or B) and (C or D)] and a person takes A and then C and then D and then E,  courseId would be associated with D
     */
    private String    courseid;
    private short     processed = UNPROCESSED;
    
    private String ruleFired;
    
    private short salesforceuploaded = NOT_UPLOADED;
    
    public java.util.Date determineExperitionDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(accreditationdate.getTime());
        cal.add(Calendar.YEAR, 2);
        return cal.getTime();
    }
    
    public enum Types {
        Valid,Active,Inactive,Expired,Renewed
    }

    public StudentAccreditation() {}

    public StudentAccreditation(StudentAccreditation value) {
        this.studentid = value.studentid;
        this.accreditationid = value.accreditationid;
        this.accreditationdate = value.accreditationdate;
        this.accreditationtype = value.accreditationtype;
        this.courseid = value.courseid;
        this.processed = value.processed;
    }

    public StudentAccreditation(
        Integer   studentid,
        Integer   accreditationid,
        Timestamp accreditationdate,
        String    accreditationtype,
        String    courseid,
        short     processed
    ) {
        this.studentid = studentid;
        this.accreditationid = accreditationid;
        this.accreditationdate = accreditationdate;
        this.accreditationtype = accreditationtype;
        this.courseid = courseid;
        this.processed = processed;
    }

    public Integer getStudentid() {
        return this.studentid;
    }

    public void setStudentid(Integer studentid) {
        this.studentid = studentid;
    }

    public Integer getAccreditationid() {
        return this.accreditationid;
    }

    public void setAccreditationid(Integer accreditationid) {
        this.accreditationid = accreditationid;
    }

    public Timestamp getAccreditationdate() {
        return this.accreditationdate;
    }

    public void setAccreditationdate(Timestamp accreditationdate) {
        this.accreditationdate = accreditationdate;
    }

    public String getAccreditationtype() {
        return this.accreditationtype;
    }

    public void setAccreditationtype(String accreditationtype) {
        this.accreditationtype = accreditationtype;
    }

    public String getCourseid() {
        return this.courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public short getProcessed() {
        return processed;
    }

    public void setProcessed(short processed) {
        this.processed = processed;
    }

    public String getRuleFired() {
        return ruleFired;
    }

    public void setRuleFired(String ruleFired) {
        this.ruleFired = ruleFired;
    }

    public short getSalesforceuploaded() {
        return salesforceuploaded;
    }

    public void setSalesforceuploaded(short salesforceuploaded) {
        this.salesforceuploaded = salesforceuploaded;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Studentaccreditations (");

        sb.append(studentid);
        sb.append(", ").append(accreditationid);
        sb.append(", ").append(accreditationdate);
        sb.append(", ").append(accreditationtype);
        sb.append(", ").append(courseid);
        sb.append(", ").append(processed);
        sb.append(", ").append(ruleFired);
        sb.append(", ").append(salesforceuploaded);
        sb.append(")");
        return sb.toString();
    }
}
