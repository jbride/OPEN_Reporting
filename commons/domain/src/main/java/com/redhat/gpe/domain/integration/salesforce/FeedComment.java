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
 * Salesforce DTO for SObject FeedComment
 */
@XStreamAlias("FeedComment")
public class FeedComment extends AbstractSObjectBase {

    // FeedItemId
    private String FeedItemId;

    @JsonProperty("FeedItemId")
    public String getFeedItemId() {
        return this.FeedItemId;
    }

    @JsonProperty("FeedItemId")
    public void setFeedItemId(String FeedItemId) {
        this.FeedItemId = FeedItemId;
    }

    // ParentId
    private String ParentId;

    @JsonProperty("ParentId")
    public String getParentId() {
        return this.ParentId;
    }

    @JsonProperty("ParentId")
    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }

    // CommentBody
    private String CommentBody;

    @JsonProperty("CommentBody")
    public String getCommentBody() {
        return this.CommentBody;
    }

    @JsonProperty("CommentBody")
    public void setCommentBody(String CommentBody) {
        this.CommentBody = CommentBody;
    }

    // InsertedById
    private String InsertedById;

    @JsonProperty("InsertedById")
    public String getInsertedById() {
        return this.InsertedById;
    }

    @JsonProperty("InsertedById")
    public void setInsertedById(String InsertedById) {
        this.InsertedById = InsertedById;
    }

    // CommentType
    @XStreamConverter(PicklistEnumConverter.class)
    private CommentTypeEnum CommentType;

    @JsonProperty("CommentType")
    public CommentTypeEnum getCommentType() {
        return this.CommentType;
    }

    @JsonProperty("CommentType")
    public void setCommentType(CommentTypeEnum CommentType) {
        this.CommentType = CommentType;
    }

    // RelatedRecordId
    private String RelatedRecordId;

    @JsonProperty("RelatedRecordId")
    public String getRelatedRecordId() {
        return this.RelatedRecordId;
    }

    @JsonProperty("RelatedRecordId")
    public void setRelatedRecordId(String RelatedRecordId) {
        this.RelatedRecordId = RelatedRecordId;
    }

}
