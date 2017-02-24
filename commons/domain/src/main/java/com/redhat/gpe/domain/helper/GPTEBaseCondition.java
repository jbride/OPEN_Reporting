package com.redhat.gpe.domain.helper;

import java.util.Date;

import com.redhat.gpe.domain.canonical.Student;

public class GPTEBaseCondition {
    
    protected String name;
    protected Date completionDate;
    protected Student student;
    
    public String getName() {
        return name;
    }
    public void setName(String x) {
        this.name = x;
    }
    
    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date assessmentDate) {
        this.completionDate = assessmentDate;
    }
    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

}
