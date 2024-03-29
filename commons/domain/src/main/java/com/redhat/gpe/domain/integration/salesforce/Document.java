/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Salesforce DTO for SObject Document
 */
@XStreamAlias("Document")
public class Document extends AbstractSObjectBase {

    // FolderId
    private String FolderId;

    @JsonProperty("FolderId")
    public String getFolderId() {
        return this.FolderId;
    }

    @JsonProperty("FolderId")
    public void setFolderId(String FolderId) {
        this.FolderId = FolderId;
    }

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

    // ContentType
    private String ContentType;

    @JsonProperty("ContentType")
    public String getContentType() {
        return this.ContentType;
    }

    @JsonProperty("ContentType")
    public void setContentType(String ContentType) {
        this.ContentType = ContentType;
    }

    // Type
    private String Type;

    @JsonProperty("Type")
    public String getType() {
        return this.Type;
    }

    @JsonProperty("Type")
    public void setType(String Type) {
        this.Type = Type;
    }

    // IsPublic
    private Boolean IsPublic;

    @JsonProperty("IsPublic")
    public Boolean getIsPublic() {
        return this.IsPublic;
    }

    @JsonProperty("IsPublic")
    public void setIsPublic(Boolean IsPublic) {
        this.IsPublic = IsPublic;
    }

    // BodyLength
    private Integer BodyLength;

    @JsonProperty("BodyLength")
    public Integer getBodyLength() {
        return this.BodyLength;
    }

    @JsonProperty("BodyLength")
    public void setBodyLength(Integer BodyLength) {
        this.BodyLength = BodyLength;
    }

    // Body
    // blob field url, use getBlobField to get the content
    @XStreamAlias("Body")
    private String BodyUrl;

    @JsonProperty("Body")
    public String getBodyUrl() {
        return this.BodyUrl;
    }

    @JsonProperty("Body")
    public void setBodyUrl(String BodyUrl) {
        this.BodyUrl = BodyUrl;
    }

    // Url
    private String Url;

    @JsonProperty("Url")
    public String getUrl() {
        return this.Url;
    }

    @JsonProperty("Url")
    public void setUrl(String Url) {
        this.Url = Url;
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

    // Keywords
    private String Keywords;

    @JsonProperty("Keywords")
    public String getKeywords() {
        return this.Keywords;
    }

    @JsonProperty("Keywords")
    public void setKeywords(String Keywords) {
        this.Keywords = Keywords;
    }

    // IsInternalUseOnly
    private Boolean IsInternalUseOnly;

    @JsonProperty("IsInternalUseOnly")
    public Boolean getIsInternalUseOnly() {
        return this.IsInternalUseOnly;
    }

    @JsonProperty("IsInternalUseOnly")
    public void setIsInternalUseOnly(Boolean IsInternalUseOnly) {
        this.IsInternalUseOnly = IsInternalUseOnly;
    }

    // AuthorId
    private String AuthorId;

    @JsonProperty("AuthorId")
    public String getAuthorId() {
        return this.AuthorId;
    }

    @JsonProperty("AuthorId")
    public void setAuthorId(String AuthorId) {
        this.AuthorId = AuthorId;
    }

    // IsBodySearchable
    private Boolean IsBodySearchable;

    @JsonProperty("IsBodySearchable")
    public Boolean getIsBodySearchable() {
        return this.IsBodySearchable;
    }

    @JsonProperty("IsBodySearchable")
    public void setIsBodySearchable(Boolean IsBodySearchable) {
        this.IsBodySearchable = IsBodySearchable;
    }

}
