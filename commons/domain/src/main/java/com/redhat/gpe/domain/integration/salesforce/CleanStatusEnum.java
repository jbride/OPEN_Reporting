/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist CleanStatus
 */
public enum CleanStatusEnum {

    // Acknowledged
    ACKNOWLEDGED("Acknowledged"),
    // Different
    DIFFERENT("Different"),
    // Inactive
    INACTIVE("Inactive"),
    // Matched
    MATCHED("Matched"),
    // NotFound
    NOTFOUND("NotFound"),
    // Pending
    PENDING("Pending"),
    // SelectMatch
    SELECTMATCH("SelectMatch"),
    // Skipped
    SKIPPED("Skipped");

    final String value;

    private CleanStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static CleanStatusEnum fromValue(String value) {
        for (CleanStatusEnum e : CleanStatusEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
