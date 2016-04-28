/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Salesforce DTO for SObject DeclinedEventRelation
 */
@XStreamAlias("DeclinedEventRelation")
public class DeclinedEventRelation extends AbstractSObjectBase {

    // RelationId
    private String RelationId;

    @JsonProperty("RelationId")
    public String getRelationId() {
        return this.RelationId;
    }

    @JsonProperty("RelationId")
    public void setRelationId(String RelationId) {
        this.RelationId = RelationId;
    }

    // EventId
    private String EventId;

    @JsonProperty("EventId")
    public String getEventId() {
        return this.EventId;
    }

    @JsonProperty("EventId")
    public void setEventId(String EventId) {
        this.EventId = EventId;
    }

    // RespondedDate
    private org.joda.time.DateTime RespondedDate;

    @JsonProperty("RespondedDate")
    public org.joda.time.DateTime getRespondedDate() {
        return this.RespondedDate;
    }

    @JsonProperty("RespondedDate")
    public void setRespondedDate(org.joda.time.DateTime RespondedDate) {
        this.RespondedDate = RespondedDate;
    }

    // Response
    private String Response;

    @JsonProperty("Response")
    public String getResponse() {
        return this.Response;
    }

    @JsonProperty("Response")
    public void setResponse(String Response) {
        this.Response = Response;
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

}
