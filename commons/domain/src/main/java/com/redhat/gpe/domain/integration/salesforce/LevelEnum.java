/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist Level__c
 */
public enum LevelEnum {

    // Primary
    PRIMARY("Primary"),
    // Secondary
    SECONDARY("Secondary"),
    // Tertiary
    TERTIARY("Tertiary");

    final String value;

    private LevelEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static LevelEnum fromValue(String value) {
        for (LevelEnum e : LevelEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
