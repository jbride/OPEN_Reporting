/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Salesforce DTO for SObject AdditionalNumber
 */
@XStreamAlias("AdditionalNumber")
public class AdditionalNumber extends AbstractSObjectBase {

    // CallCenterId
    private String CallCenterId;

    @JsonProperty("CallCenterId")
    public String getCallCenterId() {
        return this.CallCenterId;
    }

    @JsonProperty("CallCenterId")
    public void setCallCenterId(String CallCenterId) {
        this.CallCenterId = CallCenterId;
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

}
