/**
 * This class is generated by jOOQ
 */
package com.redhat.gpe.domain.canonical;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StudentMapping implements Serializable {

    private static final long serialVersionUID = 764083863;

    private String  oldemail;
    private String  newemail;
    private Integer studentid;

    public StudentMapping() {}

    public StudentMapping(StudentMapping value) {
        this.oldemail = value.oldemail;
        this.newemail = value.newemail;
        this.studentid = value.studentid;
    }

    public StudentMapping(
        String  oldemail,
        String  newemail,
        Integer studentid
    ) {
        this.oldemail = oldemail;
        this.newemail = newemail;
        this.studentid = studentid;
    }

    public String getOldemail() {
        return this.oldemail;
    }

    public void setOldemail(String oldemail) {
        this.oldemail = oldemail;
    }

    public String getNewemail() {
        return this.newemail;
    }

    public void setNewemail(String newemail) {
        this.newemail = newemail;
    }

    public Integer getStudentid() {
        return this.studentid;
    }

    public void setStudentid(Integer studentid) {
        this.studentid = studentid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Studentmappings (");

        sb.append(oldemail);
        sb.append(", ").append(newemail);
        sb.append(", ").append(studentid);

        sb.append(")");
        return sb.toString();
    }
}
