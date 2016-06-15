package com.redhat.gpe.coursecompletion.dokeos;

import java.text.SimpleDateFormat;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import com.redhat.gpte.services.ExceptionCodes;

@CsvRecord(separator=";", skipFirstLine=true)
public class DokeosCourseCompletion implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    public static final SimpleDateFormat dokeosSDF = new SimpleDateFormat("MM-dd-yy");
    public static final String DOKEOS_SUFFIX = " Final Assessment";

    // https://github.com/redhat-gpe/OPEN_Reporting/issues/37
    // Dokes exam names are now identical to canonical course names;  no need to use mapping table
    //public static final String COURSE_COMPLETION_MAPPING_NAME = "dokeos";
    public static final String COURSE_COMPLETION_MAPPING_NAME = null;

    private static final String COMMA = ",";

    @DataField(pos=1)
    private String fullname;
    
    @DataField(pos=2)
    private String email;
    
    @DataField(pos=3)
    private String quizName;
    
    @DataField(pos=4)
    private String score;
    
    @DataField(pos=5)
    private String assessmentDate;
    
    @DataField(pos=6)
    private String time;
    
    @DataField(pos=7, required=false)
    private String empty;
    
    
    public DokeosCourseCompletion() {
    }
    
    public String getFullname() {
        return fullname;
    }


    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getQuizName() {
        return quizName;
    }


    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public void pruneQuizName() {
        if(quizName.indexOf(DOKEOS_SUFFIX) > 0) {
            String prunedQuizName = quizName.substring(0, quizName.indexOf(DOKEOS_SUFFIX));
            this.quizName = prunedQuizName;
        } else {
            StringBuilder sBuilder = new StringBuilder(ExceptionCodes.GPTE_CC1001+"The following course evaluation name does not follow convention: \""+quizName+"\" . Eval name must have a suffix of: "+DOKEOS_SUFFIX);
            sBuilder.append("\nCourse completion info as follows:");
            sBuilder.append("\n\temail: "+email);
            sBuilder.append("\n\tassessmentDate: "+assessmentDate);
            throw new RuntimeException(sBuilder.toString());
        }
    }


    public String getScore() {
        return score;
    }


    public void setScore(String score) {
        this.score = score;
    }


    public String getAssessmentDate() {
        return assessmentDate;
    }


    public void setAssessmentDate(String assessmentDate) {
        this.assessmentDate = assessmentDate;
    }


    public String getTime() {
        return time;
    }


    public void setTime(String time) {
        this.time = time;
    }


    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }

    @Override
    public String toString() {
        return "DokeosCourseCompletion [fullname=" + fullname + ", email="
                + email + ", quizName=" + quizName + ", score=" + score
                + ", assessmentDate=" + assessmentDate + ", time=" + time + "]";
    }
    
    public String calculateFirstName() {
        return this.fullname.substring(fullname.indexOf(COMMA)+1);
    }
    public String calculateLastName() {
        return this.fullname.substring(0, fullname.indexOf(COMMA));
    }
}

