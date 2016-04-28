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
 * Salesforce DTO for SObject CustomPermission
 */
@XStreamAlias("CustomPermission")
public class CustomPermission extends AbstractSObjectBase {

    // DeveloperName
    private String DeveloperName;

    @JsonProperty("DeveloperName")
    public String getDeveloperName() {
        return this.DeveloperName;
    }

    @JsonProperty("DeveloperName")
    public void setDeveloperName(String DeveloperName) {
        this.DeveloperName = DeveloperName;
    }

    // Language
    @XStreamConverter(PicklistEnumConverter.class)
    private LanguageEnum Language;

    @JsonProperty("Language")
    public LanguageEnum getLanguage() {
        return this.Language;
    }

    @JsonProperty("Language")
    public void setLanguage(LanguageEnum Language) {
        this.Language = Language;
    }

    // MasterLabel
    private String MasterLabel;

    @JsonProperty("MasterLabel")
    public String getMasterLabel() {
        return this.MasterLabel;
    }

    @JsonProperty("MasterLabel")
    public void setMasterLabel(String MasterLabel) {
        this.MasterLabel = MasterLabel;
    }

    // NamespacePrefix
    private String NamespacePrefix;

    @JsonProperty("NamespacePrefix")
    public String getNamespacePrefix() {
        return this.NamespacePrefix;
    }

    @JsonProperty("NamespacePrefix")
    public void setNamespacePrefix(String NamespacePrefix) {
        this.NamespacePrefix = NamespacePrefix;
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

}
