package com.redhat.gpte.studentregistration.util;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import com.redhat.gpe.domain.canonical.Student;

@CsvRecord(separator=",", skipFirstLine=true)
public class StudentRegistrationBindy {
    
    private static final String REGION_DELIMITER = "|";
    private static final String NAME_DELIMITER = " ";

    @DataField(pos=1)
    private String name;

    @DataField(pos=2)
    private String email;

    @DataField(pos=3)
    private String company;

    @DataField(pos=4)
    private String regionCode;
    
    @DataField(pos=5)
    private String dokeos;
    
    @DataField(pos=6)
    private String userId;

    @DataField(pos=7)
    private String sso;
    
    @DataField(pos=8)
    private String role;
    
    public StudentRegistrationBindy() {  
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getDokeos() {
        return dokeos;
    }

    public void setDokeos(String dokeos) {
        this.dokeos = dokeos;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getRegion(){
        return regionCode.substring(0, regionCode.indexOf(REGION_DELIMITER));
    }
    public String getSubRegion() {
        return regionCode.substring(regionCode.indexOf(REGION_DELIMITER)+1);
    }
    public String getFirstName() {
        return name.substring(0, name.indexOf(NAME_DELIMITER));
    }
    public String getLastName() {
        return name.substring(name.indexOf(NAME_DELIMITER)+1);
    }

    public Student convertToCanonicalStudent() {
        Student sObj = new Student();
        sObj.setCompanyName(this.getCompany());
        sObj.setEmail(this.getEmail());
        sObj.setFirstname(this.getFirstName());
        sObj.setLastname(this.getLastName());
        sObj.setRegion(this.getRegion());
        sObj.setSubregion(this.getSubRegion());
        sObj.setRoles(this.getRole());
        return sObj;
    }
    
}
