/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist QuarterPrefix
 */
public enum QuarterPrefixEnum {

    // FQ
    FQ("FQ"),
    // Q
    Q("Q"),
    // Quarter
    QUARTER("Quarter"),
    // Trimester
    TRIMESTER("Trimester");

    final String value;

    private QuarterPrefixEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static QuarterPrefixEnum fromValue(String value) {
        for (QuarterPrefixEnum e : QuarterPrefixEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
