/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Salesforce DTO for SObject CampaignMemberStatus
 */
@XStreamAlias("CampaignMemberStatus")
public class CampaignMemberStatus extends AbstractSObjectBase {

    // CampaignId
    private String CampaignId;

    @JsonProperty("CampaignId")
    public String getCampaignId() {
        return this.CampaignId;
    }

    @JsonProperty("CampaignId")
    public void setCampaignId(String CampaignId) {
        this.CampaignId = CampaignId;
    }

    // Label
    private String Label;

    @JsonProperty("Label")
    public String getLabel() {
        return this.Label;
    }

    @JsonProperty("Label")
    public void setLabel(String Label) {
        this.Label = Label;
    }

    // SortOrder
    private Integer SortOrder;

    @JsonProperty("SortOrder")
    public Integer getSortOrder() {
        return this.SortOrder;
    }

    @JsonProperty("SortOrder")
    public void setSortOrder(Integer SortOrder) {
        this.SortOrder = SortOrder;
    }

    // IsDefault
    private Boolean IsDefault;

    @JsonProperty("IsDefault")
    public Boolean getIsDefault() {
        return this.IsDefault;
    }

    @JsonProperty("IsDefault")
    public void setIsDefault(Boolean IsDefault) {
        this.IsDefault = IsDefault;
    }

    // HasResponded
    private Boolean HasResponded;

    @JsonProperty("HasResponded")
    public Boolean getHasResponded() {
        return this.HasResponded;
    }

    @JsonProperty("HasResponded")
    public void setHasResponded(Boolean HasResponded) {
        this.HasResponded = HasResponded;
    }

}
