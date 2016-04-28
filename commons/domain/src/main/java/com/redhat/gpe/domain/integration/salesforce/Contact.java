/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.apache.camel.component.salesforce.api.PicklistEnumConverter;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Salesforce DTO for SObject Contact
 */
@XStreamAlias("Contact")
public class Contact extends AbstractSObjectBase {

    // MasterRecordId
    private String MasterRecordId;

    @JsonProperty("MasterRecordId")
    public String getMasterRecordId() {
        return this.MasterRecordId;
    }

    @JsonProperty("MasterRecordId")
    public void setMasterRecordId(String MasterRecordId) {
        this.MasterRecordId = MasterRecordId;
    }

    // AccountId
    private String AccountId;

    @JsonProperty("AccountId")
    public String getAccountId() {
        return this.AccountId;
    }

    @JsonProperty("AccountId")
    public void setAccountId(String AccountId) {
        this.AccountId = AccountId;
    }

    // LastName
    private String LastName;

    @JsonProperty("LastName")
    public String getLastName() {
        return this.LastName;
    }

    @JsonProperty("LastName")
    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    // FirstName
    private String FirstName;

    @JsonProperty("FirstName")
    public String getFirstName() {
        return this.FirstName;
    }

    @JsonProperty("FirstName")
    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    // Salutation
    @XStreamConverter(PicklistEnumConverter.class)
    private SalutationEnum Salutation;

    @JsonProperty("Salutation")
    public SalutationEnum getSalutation() {
        return this.Salutation;
    }

    @JsonProperty("Salutation")
    public void setSalutation(SalutationEnum Salutation) {
        this.Salutation = Salutation;
    }

    // OtherStreet
    private String OtherStreet;

    @JsonProperty("OtherStreet")
    public String getOtherStreet() {
        return this.OtherStreet;
    }

    @JsonProperty("OtherStreet")
    public void setOtherStreet(String OtherStreet) {
        this.OtherStreet = OtherStreet;
    }

    // OtherCity
    private String OtherCity;

    @JsonProperty("OtherCity")
    public String getOtherCity() {
        return this.OtherCity;
    }

    @JsonProperty("OtherCity")
    public void setOtherCity(String OtherCity) {
        this.OtherCity = OtherCity;
    }

    // OtherState
    private String OtherState;

    @JsonProperty("OtherState")
    public String getOtherState() {
        return this.OtherState;
    }

    @JsonProperty("OtherState")
    public void setOtherState(String OtherState) {
        this.OtherState = OtherState;
    }

    // OtherPostalCode
    private String OtherPostalCode;

    @JsonProperty("OtherPostalCode")
    public String getOtherPostalCode() {
        return this.OtherPostalCode;
    }

    @JsonProperty("OtherPostalCode")
    public void setOtherPostalCode(String OtherPostalCode) {
        this.OtherPostalCode = OtherPostalCode;
    }

    // OtherCountry
    private String OtherCountry;

    @JsonProperty("OtherCountry")
    public String getOtherCountry() {
        return this.OtherCountry;
    }

    @JsonProperty("OtherCountry")
    public void setOtherCountry(String OtherCountry) {
        this.OtherCountry = OtherCountry;
    }

    // OtherLatitude
    private Double OtherLatitude;

    @JsonProperty("OtherLatitude")
    public Double getOtherLatitude() {
        return this.OtherLatitude;
    }

    @JsonProperty("OtherLatitude")
    public void setOtherLatitude(Double OtherLatitude) {
        this.OtherLatitude = OtherLatitude;
    }

    // OtherLongitude
    private Double OtherLongitude;

    @JsonProperty("OtherLongitude")
    public Double getOtherLongitude() {
        return this.OtherLongitude;
    }

    @JsonProperty("OtherLongitude")
    public void setOtherLongitude(Double OtherLongitude) {
        this.OtherLongitude = OtherLongitude;
    }

    // OtherAddress
    private org.apache.camel.component.salesforce.api.dto.Address OtherAddress;

    @JsonProperty("OtherAddress")
    public org.apache.camel.component.salesforce.api.dto.Address getOtherAddress() {
        return this.OtherAddress;
    }

    @JsonProperty("OtherAddress")
    public void setOtherAddress(org.apache.camel.component.salesforce.api.dto.Address OtherAddress) {
        this.OtherAddress = OtherAddress;
    }

    // MailingStreet
    private String MailingStreet;

    @JsonProperty("MailingStreet")
    public String getMailingStreet() {
        return this.MailingStreet;
    }

    @JsonProperty("MailingStreet")
    public void setMailingStreet(String MailingStreet) {
        this.MailingStreet = MailingStreet;
    }

    // MailingCity
    private String MailingCity;

    @JsonProperty("MailingCity")
    public String getMailingCity() {
        return this.MailingCity;
    }

    @JsonProperty("MailingCity")
    public void setMailingCity(String MailingCity) {
        this.MailingCity = MailingCity;
    }

    // MailingState
    private String MailingState;

    @JsonProperty("MailingState")
    public String getMailingState() {
        return this.MailingState;
    }

    @JsonProperty("MailingState")
    public void setMailingState(String MailingState) {
        this.MailingState = MailingState;
    }

    // MailingPostalCode
    private String MailingPostalCode;

    @JsonProperty("MailingPostalCode")
    public String getMailingPostalCode() {
        return this.MailingPostalCode;
    }

    @JsonProperty("MailingPostalCode")
    public void setMailingPostalCode(String MailingPostalCode) {
        this.MailingPostalCode = MailingPostalCode;
    }

    // MailingCountry
    private String MailingCountry;

    @JsonProperty("MailingCountry")
    public String getMailingCountry() {
        return this.MailingCountry;
    }

    @JsonProperty("MailingCountry")
    public void setMailingCountry(String MailingCountry) {
        this.MailingCountry = MailingCountry;
    }

    // MailingLatitude
    private Double MailingLatitude;

    @JsonProperty("MailingLatitude")
    public Double getMailingLatitude() {
        return this.MailingLatitude;
    }

    @JsonProperty("MailingLatitude")
    public void setMailingLatitude(Double MailingLatitude) {
        this.MailingLatitude = MailingLatitude;
    }

    // MailingLongitude
    private Double MailingLongitude;

    @JsonProperty("MailingLongitude")
    public Double getMailingLongitude() {
        return this.MailingLongitude;
    }

    @JsonProperty("MailingLongitude")
    public void setMailingLongitude(Double MailingLongitude) {
        this.MailingLongitude = MailingLongitude;
    }

    // MailingAddress
    private org.apache.camel.component.salesforce.api.dto.Address MailingAddress;

    @JsonProperty("MailingAddress")
    public org.apache.camel.component.salesforce.api.dto.Address getMailingAddress() {
        return this.MailingAddress;
    }

    @JsonProperty("MailingAddress")
    public void setMailingAddress(org.apache.camel.component.salesforce.api.dto.Address MailingAddress) {
        this.MailingAddress = MailingAddress;
    }

    // Phone
    private String Phone;

    @JsonProperty("Phone")
    public String getPhone() {
        return this.Phone;
    }

    @JsonProperty("Phone")
    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    // Fax
    private String Fax;

    @JsonProperty("Fax")
    public String getFax() {
        return this.Fax;
    }

    @JsonProperty("Fax")
    public void setFax(String Fax) {
        this.Fax = Fax;
    }

    // MobilePhone
    private String MobilePhone;

    @JsonProperty("MobilePhone")
    public String getMobilePhone() {
        return this.MobilePhone;
    }

    @JsonProperty("MobilePhone")
    public void setMobilePhone(String MobilePhone) {
        this.MobilePhone = MobilePhone;
    }

    // HomePhone
    private String HomePhone;

    @JsonProperty("HomePhone")
    public String getHomePhone() {
        return this.HomePhone;
    }

    @JsonProperty("HomePhone")
    public void setHomePhone(String HomePhone) {
        this.HomePhone = HomePhone;
    }

    // OtherPhone
    private String OtherPhone;

    @JsonProperty("OtherPhone")
    public String getOtherPhone() {
        return this.OtherPhone;
    }

    @JsonProperty("OtherPhone")
    public void setOtherPhone(String OtherPhone) {
        this.OtherPhone = OtherPhone;
    }

    // AssistantPhone
    private String AssistantPhone;

    @JsonProperty("AssistantPhone")
    public String getAssistantPhone() {
        return this.AssistantPhone;
    }

    @JsonProperty("AssistantPhone")
    public void setAssistantPhone(String AssistantPhone) {
        this.AssistantPhone = AssistantPhone;
    }

    // ReportsToId
    private String ReportsToId;

    @JsonProperty("ReportsToId")
    public String getReportsToId() {
        return this.ReportsToId;
    }

    @JsonProperty("ReportsToId")
    public void setReportsToId(String ReportsToId) {
        this.ReportsToId = ReportsToId;
    }

    // Email
    private String Email;

    @JsonProperty("Email")
    public String getEmail() {
        return this.Email;
    }

    @JsonProperty("Email")
    public void setEmail(String Email) {
        this.Email = Email;
    }

    // Title
    private String Title;

    @JsonProperty("Title")
    public String getTitle() {
        return this.Title;
    }

    @JsonProperty("Title")
    public void setTitle(String Title) {
        this.Title = Title;
    }

    // Department
    private String Department;

    @JsonProperty("Department")
    public String getDepartment() {
        return this.Department;
    }

    @JsonProperty("Department")
    public void setDepartment(String Department) {
        this.Department = Department;
    }

    // AssistantName
    private String AssistantName;

    @JsonProperty("AssistantName")
    public String getAssistantName() {
        return this.AssistantName;
    }

    @JsonProperty("AssistantName")
    public void setAssistantName(String AssistantName) {
        this.AssistantName = AssistantName;
    }

    // LeadSource
    @XStreamConverter(PicklistEnumConverter.class)
    private LeadSourceEnum LeadSource;

    @JsonProperty("LeadSource")
    public LeadSourceEnum getLeadSource() {
        return this.LeadSource;
    }

    @JsonProperty("LeadSource")
    public void setLeadSource(LeadSourceEnum LeadSource) {
        this.LeadSource = LeadSource;
    }

    // Birthdate
    private org.joda.time.DateTime Birthdate;

    @JsonProperty("Birthdate")
    public org.joda.time.DateTime getBirthdate() {
        return this.Birthdate;
    }

    @JsonProperty("Birthdate")
    public void setBirthdate(org.joda.time.DateTime Birthdate) {
        this.Birthdate = Birthdate;
    }

    // Description
    private String Description;

    @JsonProperty("Description")
    public String getDescription() {
        return this.Description;
    }

    @JsonProperty("Description")
    public void setDescription(String Description) {
        this.Description = Description;
    }

    // LastCURequestDate
    private org.joda.time.DateTime LastCURequestDate;

    @JsonProperty("LastCURequestDate")
    public org.joda.time.DateTime getLastCURequestDate() {
        return this.LastCURequestDate;
    }

    @JsonProperty("LastCURequestDate")
    public void setLastCURequestDate(org.joda.time.DateTime LastCURequestDate) {
        this.LastCURequestDate = LastCURequestDate;
    }

    // LastCUUpdateDate
    private org.joda.time.DateTime LastCUUpdateDate;

    @JsonProperty("LastCUUpdateDate")
    public org.joda.time.DateTime getLastCUUpdateDate() {
        return this.LastCUUpdateDate;
    }

    @JsonProperty("LastCUUpdateDate")
    public void setLastCUUpdateDate(org.joda.time.DateTime LastCUUpdateDate) {
        this.LastCUUpdateDate = LastCUUpdateDate;
    }

    // EmailBouncedReason
    private String EmailBouncedReason;

    @JsonProperty("EmailBouncedReason")
    public String getEmailBouncedReason() {
        return this.EmailBouncedReason;
    }

    @JsonProperty("EmailBouncedReason")
    public void setEmailBouncedReason(String EmailBouncedReason) {
        this.EmailBouncedReason = EmailBouncedReason;
    }

    // EmailBouncedDate
    private org.joda.time.DateTime EmailBouncedDate;

    @JsonProperty("EmailBouncedDate")
    public org.joda.time.DateTime getEmailBouncedDate() {
        return this.EmailBouncedDate;
    }

    @JsonProperty("EmailBouncedDate")
    public void setEmailBouncedDate(org.joda.time.DateTime EmailBouncedDate) {
        this.EmailBouncedDate = EmailBouncedDate;
    }

    // IsEmailBounced
    private Boolean IsEmailBounced;

    @JsonProperty("IsEmailBounced")
    public Boolean getIsEmailBounced() {
        return this.IsEmailBounced;
    }

    @JsonProperty("IsEmailBounced")
    public void setIsEmailBounced(Boolean IsEmailBounced) {
        this.IsEmailBounced = IsEmailBounced;
    }

    // PhotoUrl
    private String PhotoUrl;

    @JsonProperty("PhotoUrl")
    public String getPhotoUrl() {
        return this.PhotoUrl;
    }

    @JsonProperty("PhotoUrl")
    public void setPhotoUrl(String PhotoUrl) {
        this.PhotoUrl = PhotoUrl;
    }

    // Jigsaw
    private String Jigsaw;

    @JsonProperty("Jigsaw")
    public String getJigsaw() {
        return this.Jigsaw;
    }

    @JsonProperty("Jigsaw")
    public void setJigsaw(String Jigsaw) {
        this.Jigsaw = Jigsaw;
    }

    // JigsawContactId
    private String JigsawContactId;

    @JsonProperty("JigsawContactId")
    public String getJigsawContactId() {
        return this.JigsawContactId;
    }

    @JsonProperty("JigsawContactId")
    public void setJigsawContactId(String JigsawContactId) {
        this.JigsawContactId = JigsawContactId;
    }

    // CleanStatus
    @XStreamConverter(PicklistEnumConverter.class)
    private CleanStatusEnum CleanStatus;

    @JsonProperty("CleanStatus")
    public CleanStatusEnum getCleanStatus() {
        return this.CleanStatus;
    }

    @JsonProperty("CleanStatus")
    public void setCleanStatus(CleanStatusEnum CleanStatus) {
        this.CleanStatus = CleanStatus;
    }

    // Level__c
    @XStreamConverter(PicklistEnumConverter.class)
    private LevelEnum Level__c;

    @JsonProperty("Level__c")
    public LevelEnum getLevel__c() {
        return this.Level__c;
    }

    @JsonProperty("Level__c")
    public void setLevel__c(LevelEnum Level__c) {
        this.Level__c = Level__c;
    }

    // Languages__c
    private String Languages__c;

    @JsonProperty("Languages__c")
    public String getLanguages__c() {
        return this.Languages__c;
    }

    @JsonProperty("Languages__c")
    public void setLanguages__c(String Languages__c) {
        this.Languages__c = Languages__c;
    }

    // IPA__c
    private Boolean IPA__c;

    @JsonProperty("IPA__c")
    public Boolean getIPA__c() {
        return this.IPA__c;
    }

    @JsonProperty("IPA__c")
    public void setIPA__c(Boolean IPA__c) {
        this.IPA__c = IPA__c;
    }

}
