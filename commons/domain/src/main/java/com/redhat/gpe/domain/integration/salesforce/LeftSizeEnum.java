/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist LeftSize
 */
public enum LeftSizeEnum {

    // Medium
    MEDIUM("Medium"),
    // Narrow
    NARROW("Narrow"),
    // Wide
    WIDE("Wide");

    final String value;

    private LeftSizeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static LeftSizeEnum fromValue(String value) {
        for (LeftSizeEnum e : LeftSizeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
