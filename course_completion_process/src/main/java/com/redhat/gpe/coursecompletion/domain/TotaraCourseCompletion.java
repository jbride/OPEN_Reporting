package com.redhat.gpe.coursecompletion.domain;

public class TotaraCourseCompletion {

    private int totaraCCId;
    private String email;
    private String totaraCourseId;
    private String courseFullName;
    private String courseShortName;
    private String studentFirstName;
    private String studentLastName;

    public TotaraCourseCompletion(int totaraCCId, String email, String studentFirstName, String studentLastName, String totaraCourseId, String courseFullName, String courseShortName) {
        this.totaraCCId = totaraCCId;
        this.email = email;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.totaraCourseId = totaraCourseId;
        this.courseFullName = courseFullName;
        this.courseShortName = courseShortName;
    }

    public int getTotaraCCId() { return totaraCCId; }
    public void setTotaraCCId(int x) { totaraCCId = x; }
    public String getEmail() { return email; }
    public void setEmail(String x) { email = x; }
    public String getTotaraCourseId() { return totaraCourseId; }
    public void setTotaraCourseId(String x) { totaraCourseId = x; }
    public String getCourseFullName() { return courseFullName; }
    public void setCourseFullName(String x) { courseFullName = x; }
    public String getCourseShortName() { return courseShortName; }
    public void setCourseShortName(String x) { courseShortName = x; }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentFirstName() {
        return this.studentFirstName;
    }
}


