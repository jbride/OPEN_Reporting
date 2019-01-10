package com.redhat.gpe.studentregistration.studentreg;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.Company;

@CsvRecord(separator=",", skipFirstLine=true)
public class StudentRegistrationBindy {
    
    private static final String REGION_DELIMITER = "|";
    private static final String NAME_DELIMITER = " ";
    private static final String PARTNER_DELIMITER = ".";
    private static final String SFDC_DELIMITER = ":";
    private static final String EMAIL_AT = "@";

    @DataField(pos=1, required=true)
    private String name;

    @DataField(pos=2, required=true)
    private String email;

    @DataField(pos=3, required=true)
    private String company;

    @DataField(pos=4, required=true)
    private String regionCode;
    
    @DataField(pos=5)
    private String userId;

    @DataField(pos=6, required=true)
    private String partnerTierType;
    
    @DataField(pos=7, required=true)
    private String sfdcUserIdCompanyId;
    
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPartnerTierType() {
        return partnerTierType;
    }

    public void setPartnerTierType(String x) {
        this.partnerTierType = x;
    }

    public String getSfdcUserIdCompanyId() {
        return this.sfdcUserIdCompanyId;
    }

    public void setRole(String x) {
        this.sfdcUserIdCompanyId = x;
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
    public String getPartnerTier() {
        String tString = partnerTierType.substring(partnerTierType.indexOf(PARTNER_DELIMITER)+1);
        return tString.substring(0, tString.indexOf(PARTNER_DELIMITER));
    }
    public String getPartnerType() {
        String tString = partnerTierType.substring(partnerTierType.indexOf(PARTNER_DELIMITER)+1);
        return tString.substring(tString.indexOf(PARTNER_DELIMITER)+1);
    }
    public String getSfdcUserId() {
        String uNumber = sfdcUserIdCompanyId.substring(0, this.sfdcUserIdCompanyId.indexOf(this.SFDC_DELIMITER));
        // https://github.com/redhat-gpe/OPEN_Reporting/issues/128
        //return uNumber.substring(0,uNumber.length() - 3); 
        return uNumber;
    }
    public String getSfdcCompanyId() {
        return this.sfdcUserIdCompanyId.substring(this.sfdcUserIdCompanyId.indexOf(this.SFDC_DELIMITER)+1);
    }

    public Student convertToCanonicalStudent() {
        Student sObj = new Student();        
        sObj.setEmail(this.getEmail());
        //------ Not to pass multiple byte character to LDAP. To fix https://github.com/redhat-gpe/OPEN_Reporting/issues/312 --------
        if(this.getFirstName().length()!=this.getFirstName().getBytes().length
                || this.getLastName().length()!=this.getLastName().getBytes().length) {
            sObj.setFirstname(this.getEmail().substring(0, this.getEmail().indexOf(EMAIL_AT)));
            sObj.setLastname(this.getEmail().substring(0, this.getEmail().indexOf(EMAIL_AT)));
        } else {
            sObj.setFirstname(this.getFirstName());
            sObj.setLastname(this.getLastName());
        }
        //--------------------------------------------------------------
        sObj.setCompanyName(this.getCompany());
        sObj.setRegion(this.getRegion());
        sObj.setSubregion(this.getSubRegion());
        sObj.setSalesforcecontactid(this.getSfdcUserId());
        return sObj;
    }
    
    public Company convertToCanonicalCompany() {
        Company cObj = new Company();
        cObj.setPartnertier(this.getPartnerTier());
        cObj.setPartnertype(this.getPartnerType());
        cObj.setSfdcId(this.getSfdcCompanyId());
        cObj.setCompanyname(this.getCompany());
        return cObj;
    }
    
}
