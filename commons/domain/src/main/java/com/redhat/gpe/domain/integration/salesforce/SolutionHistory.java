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
 * Salesforce DTO for SObject SolutionHistory
 */
@XStreamAlias("SolutionHistory")
public class SolutionHistory extends AbstractSObjectBase {

    // SolutionId
    private String SolutionId;

    @JsonProperty("SolutionId")
    public String getSolutionId() {
        return this.SolutionId;
    }

    @JsonProperty("SolutionId")
    public void setSolutionId(String SolutionId) {
        this.SolutionId = SolutionId;
    }

    // Field
    @XStreamConverter(PicklistEnumConverter.class)
    private FieldEnum Field;

    @JsonProperty("Field")
    public FieldEnum getField() {
        return this.Field;
    }

    @JsonProperty("Field")
    public void setField(FieldEnum Field) {
        this.Field = Field;
    }

    // OldValue
    private String OldValue;

    @JsonProperty("OldValue")
    public String getOldValue() {
        return this.OldValue;
    }

    @JsonProperty("OldValue")
    public void setOldValue(String OldValue) {
        this.OldValue = OldValue;
    }

    // NewValue
    private String NewValue;

    @JsonProperty("NewValue")
    public String getNewValue() {
        return this.NewValue;
    }

    @JsonProperty("NewValue")
    public void setNewValue(String NewValue) {
        this.NewValue = NewValue;
    }

}
