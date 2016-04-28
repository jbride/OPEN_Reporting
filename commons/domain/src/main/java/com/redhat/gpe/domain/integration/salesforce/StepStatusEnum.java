/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist StepStatus
 */
public enum StepStatusEnum {

    // Approved
    APPROVED("Approved"),
    // Fault
    FAULT("Fault"),
    // Held
    HELD("Held"),
    // NoResponse
    NORESPONSE("NoResponse"),
    // Pending
    PENDING("Pending"),
    // Reassigned
    REASSIGNED("Reassigned"),
    // Rejected
    REJECTED("Rejected"),
    // Removed
    REMOVED("Removed"),
    // Started
    STARTED("Started");

    final String value;

    private StepStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static StepStatusEnum fromValue(String value) {
        for (StepStatusEnum e : StepStatusEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
