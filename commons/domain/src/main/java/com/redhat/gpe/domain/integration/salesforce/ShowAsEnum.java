/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist ShowAs
 */
public enum ShowAsEnum {

    // Busy
    BUSY("Busy"),
    // Free
    FREE("Free"),
    // OutOfOffice
    OUTOFOFFICE("OutOfOffice");

    final String value;

    private ShowAsEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static ShowAsEnum fromValue(String value) {
        for (ShowAsEnum e : ShowAsEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
