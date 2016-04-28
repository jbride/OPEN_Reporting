/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Salesforce DTO for SObject CaseSolution
 */
@XStreamAlias("CaseSolution")
public class CaseSolution extends AbstractSObjectBase {

    // CaseId
    private String CaseId;

    @JsonProperty("CaseId")
    public String getCaseId() {
        return this.CaseId;
    }

    @JsonProperty("CaseId")
    public void setCaseId(String CaseId) {
        this.CaseId = CaseId;
    }

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

}
