package com.redhat.gpte.studentregistration.util;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.Company;

@CsvRecord(separator=",", skipFirstLine=false)
public class StudentBindy {
    
    @DataField(pos=1, required=true)
    private String email;

    @DataField(pos=2, required=true)
    private String firstName;

    @DataField(pos=3, required=true)
    private String lastName;

    @DataField(pos=4, required=true)
    private String company;

    @DataField(pos=5, required=true)
    private String region;
    
    @DataField(pos=6, required=true)
    private String country;

    @DataField(pos=7, required=true)
    private String role;
    
    public StudentBindy() {  
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static Student convertToCanonicalStudent(String csvLine) {
        String[] tokens = csvLine.split(",");
        StudentBindy sBindy = new StudentBindy();
        sBindy.setEmail(tokens[0]);
        sBindy.setFirstName(tokens[1]);
        sBindy.setLastName(tokens[2]);
        sBindy.setCompany(tokens[3]);
        sBindy.setRegion(tokens[4]);
        sBindy.setCountry(tokens[5]);
        sBindy.setRole(tokens[6]);

        return sBindy.convertToCanonicalStudent();
    }

    public Student convertToCanonicalStudent() {
        Student sObj = new Student();
        sObj.setEmail(this.getEmail());
        sObj.setFirstname(this.getFirstName());
        sObj.setLastname(this.getLastName());
        sObj.setCompanyName(this.getCompany());
        sObj.setRegion(this.getRegion());
        sObj.setCountry(this.getCountry());
        sObj.setRoles(this.getRole());
        return sObj;
    }
    
}
