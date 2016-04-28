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
 * Salesforce DTO for SObject ContentDocument
 */
@XStreamAlias("ContentDocument")
public class ContentDocument extends AbstractSObjectBase {

    // IsArchived
    private Boolean IsArchived;

    @JsonProperty("IsArchived")
    public Boolean getIsArchived() {
        return this.IsArchived;
    }

    @JsonProperty("IsArchived")
    public void setIsArchived(Boolean IsArchived) {
        this.IsArchived = IsArchived;
    }

    // ArchivedById
    private String ArchivedById;

    @JsonProperty("ArchivedById")
    public String getArchivedById() {
        return this.ArchivedById;
    }

    @JsonProperty("ArchivedById")
    public void setArchivedById(String ArchivedById) {
        this.ArchivedById = ArchivedById;
    }

    // ArchivedDate
    private org.joda.time.DateTime ArchivedDate;

    @JsonProperty("ArchivedDate")
    public org.joda.time.DateTime getArchivedDate() {
        return this.ArchivedDate;
    }

    @JsonProperty("ArchivedDate")
    public void setArchivedDate(org.joda.time.DateTime ArchivedDate) {
        this.ArchivedDate = ArchivedDate;
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

    // PublishStatus
    @XStreamConverter(PicklistEnumConverter.class)
    private PublishStatusEnum PublishStatus;

    @JsonProperty("PublishStatus")
    public PublishStatusEnum getPublishStatus() {
        return this.PublishStatus;
    }

    @JsonProperty("PublishStatus")
    public void setPublishStatus(PublishStatusEnum PublishStatus) {
        this.PublishStatus = PublishStatus;
    }

    // LatestPublishedVersionId
    private String LatestPublishedVersionId;

    @JsonProperty("LatestPublishedVersionId")
    public String getLatestPublishedVersionId() {
        return this.LatestPublishedVersionId;
    }

    @JsonProperty("LatestPublishedVersionId")
    public void setLatestPublishedVersionId(String LatestPublishedVersionId) {
        this.LatestPublishedVersionId = LatestPublishedVersionId;
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

    // ContentSize
    private Integer ContentSize;

    @JsonProperty("ContentSize")
    public Integer getContentSize() {
        return this.ContentSize;
    }

    @JsonProperty("ContentSize")
    public void setContentSize(Integer ContentSize) {
        this.ContentSize = ContentSize;
    }

    // FileType
    private String FileType;

    @JsonProperty("FileType")
    public String getFileType() {
        return this.FileType;
    }

    @JsonProperty("FileType")
    public void setFileType(String FileType) {
        this.FileType = FileType;
    }

    // FileExtension
    private String FileExtension;

    @JsonProperty("FileExtension")
    public String getFileExtension() {
        return this.FileExtension;
    }

    @JsonProperty("FileExtension")
    public void setFileExtension(String FileExtension) {
        this.FileExtension = FileExtension;
    }

    // ContentModifiedDate
    private org.joda.time.DateTime ContentModifiedDate;

    @JsonProperty("ContentModifiedDate")
    public org.joda.time.DateTime getContentModifiedDate() {
        return this.ContentModifiedDate;
    }

    @JsonProperty("ContentModifiedDate")
    public void setContentModifiedDate(org.joda.time.DateTime ContentModifiedDate) {
        this.ContentModifiedDate = ContentModifiedDate;
    }

}
