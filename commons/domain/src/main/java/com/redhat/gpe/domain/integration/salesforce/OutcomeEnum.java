/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist Outcome
 */
public enum OutcomeEnum {

    // CompileFail
    COMPILEFAIL("CompileFail"),
    // Fail
    FAIL("Fail"),
    // Pass
    PASS("Pass"),
    // Skip
    SKIP("Skip");

    final String value;

    private OutcomeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static OutcomeEnum fromValue(String value) {
        for (OutcomeEnum e : OutcomeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
