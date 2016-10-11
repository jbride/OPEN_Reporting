package com.redhat.gpe.coursecompletion.sumtotal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", skipFirstLine=true)
public class SumtotalCourseCompletion implements java.io.Serializable{
    
    private static final long serialVersionUID = 1L;
    
    public static final String FULL_NAME="fullName";
    public static final String USER_NUMBER="userNumber";
    public static final String EMAIL="email";
    public static final String TEXT3="text3";
    public static final String PRIMARY_JOB="primaryJob";
    public static final String ORGANIZATION="organization";
    public static final String DOMAIN="domain";
    public static final String COUNTRY="country";
    public static final String IS_ACTIVE="isActive";
    public static final String ACTIVITY_LABEL="activityLabel";
    public static final String ACTIVITY_NAME="activityName";
    public static final String ACTIVITY_CODE="activityCode";
    public static final String IS_CERTIFICATION="isCertification";
    public static final String ATTENDANCE_STATUS="attendanceStatus";
    public static final String COMPLETION_STATUS="completionStatus";
    public static final String ATTEMPT_START_DATE="attemptStartDate";
    public static final String ATTEMPT_END_DATE="attemptEndDate";
    public static final String TEXT4="text4";
    public static final String TEXT4_DELIMITER=".";
    public static final DateFormat dfObj = new SimpleDateFormat("MMM dd, yyyy");

    public static final String COURSE_COMPLETION_MAPPING_NAME = "sumtotal";

    private static final String COMMA = ",";

    @DataField(pos=1)
    private String fullName;
    

    @DataField(pos=2, required=true)
    private String email;
    
    private String text3;

    private String primaryJob;


    private String domain;
    
    
    private boolean isActive;
    
    @DataField(pos=3, required=true)
    private String activityLabel;

    @DataField(pos=4)
    private String activityName;
    
    @DataField(pos=5, required=true)
    private String activityCode;
    
    private boolean isCertification;
    
    private String attendanceStatus;
    
    private String completionStatus;

    private Date attemptStartDate = new Date();
    
    @DataField(pos=6, pattern="MMM dd, yyyy", required=true)
    private Date attemptEndDate = new Date();
    
    
    // Sumtotal course completion spreadsheet now provides company name from SalesForce
    // This companyName is included because the information often times is not available in GPTE's IPA LDAP
    // This is the case for students that are in a Sales track and not a SE / Delivery track (thus student doesn't need a lab environment nor OPENTLC SSO
    @DataField(pos=7, required=true)
    private String organization;
    
    // Region / Tier and type (not currently used):  example:   EMEA.ADVANCED.ISV
    @DataField(pos=8, required=true)
    private String text4;

    @DataField(pos=9, required=true)
    private String userNumber;

    @DataField(pos=10, required=true, length=2)
    private String country;
    
    public SumtotalCourseCompletion() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getPrimaryJob() {
        return primaryJob;
    }

    public void setPrimaryJob(String primaryJob) {
        this.primaryJob = primaryJob;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        isActive = isActive;
    }

    public String getActivityLabel() {
        return activityLabel;
    }

    public void setActivityLabel(String activityLabel) {
        this.activityLabel = activityLabel;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public boolean isCertification() {
        return isCertification;
    }

    public void setCertification(boolean isCertification) {
        this.isCertification = isCertification;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public Date getAttemptStartDate() {
        return attemptStartDate;
    }

    public void setAttemptStartDate(Date attemptStartDate) {
        this.attemptStartDate = attemptStartDate;
    }

    public Date getAttemptEndDate() {
        return attemptEndDate;
    }

    public void setAttemptEndDate(Date attemptEndDate) {
        this.attemptEndDate = attemptEndDate;
    }

    public String getText4() {
        return text4;
    }

    public void setText4(String text4) {
        this.text4 = text4;
    }

    public String getUserNumber() {
        return userNumber;
    }
    public void setUserNumber(String x) {
        userNumber = x;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    public static SumtotalCourseCompletion createSumtotalCourseCompletion(String[] line) {
        SumtotalCourseCompletion sAssessment = new SumtotalCourseCompletion();
        return sAssessment;
    }
    
    public String getFirstName(){
        if(fullName == null && fullName.indexOf(COMMA) > 0) {
            return fullName.substring(0, fullName.indexOf(COMMA));
        }else {
            return null;
        }
    }
    public String getLastName() {
        if(fullName == null && fullName.indexOf(COMMA) > 0){
            return fullName.substring(fullName.indexOf(COMMA));
        }else {
            return fullName;
        }
    }
    
    public String getAttemptEndDateString(){
        return dfObj.format(this.attemptEndDate);
    }

    public String getRegion() {
        String region = this.text4.substring(0, text4.indexOf(TEXT4_DELIMITER));
        return region;
    }
    
    @Override
    public String toString() {
        return String.format(
                "SumtotalCourseCompletion [fullName=%s, userNumber=%s, email=%s, text3=%s, primaryJob=%s, organization=%s, domain=%s, country=%s, IsActive=%s, activityLabel=%s, activityName=%s, activityCode=%s, isCertification=%s, attendanceStatus=%s, completionStatus=%s, attemptStartDate=%s, attemptEndDate=%s, text4=%s]",
                fullName, userNumber, email, text3, primaryJob, organization, domain, country, isActive, activityLabel,
                activityName, activityCode, isCertification, attendanceStatus, completionStatus, attemptStartDate,
                attemptEndDate, text4);
    }
    
}
