/**
 * This class is generated by jOOQ
 */
package com.redhat.gpe.domain.canonical;


import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Student implements Serializable {

    private static final long serialVersionUID = 289269489;

    public static final String STUDENT_ID = "StudentID";
    public static final String EMAIL = "Email";
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final String COMPANY_ID = "CompanyID";
    public static final String REGION = "Region";
    public static final String SUBREGION = "SubRegion";
    public static final String COUNTRY = "Country";
    public static final String ROLES = "Roles";
    public static final String SALES_FORCE_CONTACT_ID = "SalesForceContactID";
    public static final String SALES_FORCE_ACTIVE = "SalesForceActive";
    public static final String SUMTOTAL_ID = "SumTotalID";
    public static final String SUMTOTAL_ACTIVE = "SumTotalActive";
    public static final String SKILLSBASE_STATUS = "SkillsbaseStatus";
    public static final String SKILLSBASE_PERSON_ID = "skillsbasePersonId";
    public static final String IPA_STATUS = "IpaStatus";
    public static final String ACTIVATION_DATE = "ActivationDate";
    public static final String DEACTIVATION_DATE = "DeActivationDate";

    public static final String WHERE_CLAUSE = "s.StudentID,s.Email,s.FirstName,s.LastName,s.CompanyID,s.Region,s.SubRegion,s.Country,s.Roles,s.SalesForceContactID,s.SalesForceActive,s.SumTotalID,s.SumTotalActive,s.SkillsbaseStatus,s.IpaStatus,s.ActivationDate,s.DeActivationDate";

    public static final int SKILLSBASE_PERSON_UNVERIFIED = 0;
    public static final int SKILLSBASE_PERSON_NOTIFIED_NO_ACCOUNT=1;
    public static final int SKILLSBASE_PERSON_OK = 2;
    public static final int IPA_UNSYNCHED = 0;
    public static final int IPA_SYNCHED = 1;


    private Integer studentid = 0;
    private String  email;
    private String  firstname;
    private String  lastname;
    private Integer companyid = 0;
    private String  region;
    private String  subregion;
    private String  country;

    /* smangan: 22 Feb 2016
     * probably a good idea to maintain it in Students as well so that we can more easily feed it back to SalesForce. 
     * I'm fine with handling it either way. The important thing will be to keep it in sync across all systems. */
    private String  roles;
    private String  salesforcecontactid;
    private String  salesforceactive;
    private String  sumtotalid;
    private String  sumtotalactive;
    private int     skillsbaseStatus = SKILLSBASE_PERSON_UNVERIFIED;
    private String  skillsbasePersonId;  // Does not need to be persisted.  Value is refereshed with every invocation to SkillsBase
    private int        ipaStatus = IPA_UNSYNCHED;
    private Timestamp    activationDate;
    private Timestamp    deActivationDate;

    /* Patrick, 20 January 2016
     * the Title attribute in LDAP is being used for Role because it was available. It does not mean its their title
     * Cost center is computed by their role (aka:  title)  and geo */
    public enum Titles {
        sa,cons,other,sa_fed
    }
    public enum Geos {
        na,latam,emea,apac
    }

    public Student() {}

    public Integer getStudentid() {
        return this.studentid;
    }

    public void setStudentid(Integer studentid) {
        this.studentid = studentid;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getCompanyid() {
        return this.companyid;
    }

    public void setCompanyid(Integer companyid) {
        this.companyid = companyid;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubregion() {
        return this.subregion;
    }

    public void setSubregion(String subregion) {
        this.subregion = subregion;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRoles() {
        return this.roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getSalesforcecontactid() {
        return this.salesforcecontactid;
    }

    public void setSalesforcecontactid(String salesforcecontactid) {
        this.salesforcecontactid = salesforcecontactid;
    }

    public String getSalesforceactive() {
        return this.salesforceactive;
    }

    public void setSalesforceactive(String salesforceactive) {
        this.salesforceactive = salesforceactive;
    }

    public String getSumtotalid() {
        return this.sumtotalid;
    }

    public void setSumtotalid(String sumtotalid) {
        this.sumtotalid = sumtotalid;
    }

    public String getSumtotalactive() {
        return this.sumtotalactive;
    }

    public void setSumtotalactive(String sumtotalactive) {
        this.sumtotalactive = sumtotalactive;
    }

    public int getSkillsbaseStatus() {
        return skillsbaseStatus;
    }

    public void setSkillsbaseStatus(int skillsbaseStatus) {
        this.skillsbaseStatus = skillsbaseStatus;
    }

    public String getSkillsbasePersonId() {
        return skillsbasePersonId;
    }

    public void setSkillsbasePersonId(String skillsbasePersonId) {
        this.skillsbasePersonId = skillsbasePersonId;
    }
    
    public int getIpaStatus() {
        return this.ipaStatus;
    }
    public void setIpaStatus(int x){
        this.ipaStatus = x;
    }

    public Timestamp getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Timestamp activationDate) {
        this.activationDate = activationDate;
    }

    public Timestamp getDeActivationDate() {
        return deActivationDate;
    }

    public void setDeActivationDate(Timestamp deActivationDate) {
        this.deActivationDate = deActivationDate;
    }

    @Override
    public String toString() {
        return "Student [studentid=" + studentid + ", email=" + email
                + ", firstname=" + firstname + ", lastname=" + lastname
                + ", companyid=" + companyid + ", region=" + region
                + ", subregion=" + subregion + ", country=" + country
                + ", roles=" + roles + ", salesforcecontactid="
                + salesforcecontactid + ", salesforceactive="
                + salesforceactive + ", sumtotalid=" + sumtotalid
                + ", sumtotalactive=" + sumtotalactive + ", skillsbaseStatus="
                + skillsbaseStatus + ", skillsbasePersonId="
                + skillsbasePersonId + ", ipaStatus=" + ipaStatus
                + ", activationDate=" + activationDate + ", deActivationDate="
                + deActivationDate + "]";
    }
    
}
