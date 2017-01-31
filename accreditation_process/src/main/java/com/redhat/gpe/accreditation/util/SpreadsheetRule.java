package com.redhat.gpe.accreditation.util;

import java.text.SimpleDateFormat;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator="\t", skipFirstLine=false)
public class SpreadsheetRule implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    public static final SimpleDateFormat rulesSDF = new SimpleDateFormat("dd-MMM-yyyy");
    public static final String BEGIN_DATE = "beginDate";
    public static final String END_DATE = "endDate";
    public static final String ACCRED_CONDITION = "accredCondition";
    public static final String COURSE1 = "course1";
    public static final String COURSE2 = "course2";
    public static final String COURSE3 = "course3";
    public static final String COURSE4 = "course4";
    public static final String COURSE5 = "course5";
    public static final String COURSE6 = "course6";
    public static final String COURSE7 = "course7";
    public static final String COURSE8 = "course8";
    public static final String ACCRED_NAME = "accredName";
    public static final String RULE_NAME = "ruleName";
    
    @DataField(pos=1)
    private String beginDate;
    
    @DataField(pos=2)
    private String endDate;
    
    @DataField(pos=3)
    private String accredCondition;
    
    @DataField(pos=4)
    private String course1;
    
    @DataField(pos=5)
    private String course2;
    
    @DataField(pos=6)
    private String course3;
    
    @DataField(pos=7)
    private String course4;
    
    @DataField(pos=8)
    private String course5;
    
    @DataField(pos=9)
    private String course6;
    
    @DataField(pos=10)
    private String course7;
    
    @DataField(pos=11)
    private String course8;
    
    @DataField(pos=12)
    private String accredName;

    @DataField(pos=13)
    private String notes;
    
    private String ruleName;
    private int spreadsheetRowNumber;
    

    public SpreadsheetRule() {
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    
    /* Generate a unique rule name.
     *   The accreditation name is not unique in a spreadsheet
     */
    public void generateRuleName(int rowNumber) {
        ruleName = rowNumber+"_"+accredName;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAccredCondition() {
        return accredCondition;
    }

    public void setAccredCondition(String accredCondition) {
        this.accredCondition = accredCondition;
    }

    public String getCourse1() {
        return course1;
    }

    public void setCourse1(String course1) {
        this.course1 = course1;
    }

    public String getCourse2() {
        return course2;
    }

    public void setCourse2(String course2) {
        this.course2 = course2;
    }

    public String getCourse3() {
        return course3;
    }

    public void setCourse3(String course3) {
        this.course3 = course3;
    }

    public String getCourse4() {
        return course4;
    }

    public void setCourse4(String course4) {
        this.course4 = course4;
    }

    public String getCourse5() {
        return course5;
    }

    public void setCourse5(String course5) {
        this.course5 = course5;
    }

    public String getCourse6() {
        return course6;
    }

    public void setCourse6(String course6) {
        this.course6 = course6;
    }

    public String getCourse7() {
        return course7;
    }

    public void setCourse7(String course7) {
        this.course7 = course7;
    }

    public String getCourse8() {
        return course8;
    }

    public void setCourse8(String course8) {
        this.course8 = course8;
    }

    public String getAccredName() {
        return accredName;
    }

    public void setAccredName(String accredName) {
        this.accredName = accredName;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String x) {
        this.notes = x;
    }

    @Override
    public String toString() {
        return "[ruleName="+ruleName+", beginDate=" + beginDate + ", endDate="
                + endDate + ", accredCondition="+ accredCondition 
                +", course1=" + course1 + ", course2=" + course2
                + ", course3=" + course3 + ", course4=" + course4
                + ", course5=" + course5 + ", course6=" + course6
                + ", course7=" + course7 + ", course8=" + course8
                + ", accredName=" + accredName + "]";
    }

    public int getSpreadsheetRowNumber() {
        return spreadsheetRowNumber;
    }

    public void setSpreadsheetRowNumber(int spreadsheetRowNumber) {
        this.spreadsheetRowNumber = spreadsheetRowNumber;
    }
    
}

