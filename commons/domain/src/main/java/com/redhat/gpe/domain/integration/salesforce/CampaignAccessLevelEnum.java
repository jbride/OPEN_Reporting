/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist CampaignAccessLevel
 */
public enum CampaignAccessLevelEnum {

    // All
    ALL("All"),
    // Edit
    EDIT("Edit"),
    // Read
    READ("Read");

    final String value;

    private CampaignAccessLevelEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static CampaignAccessLevelEnum fromValue(String value) {
        for (CampaignAccessLevelEnum e : CampaignAccessLevelEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
