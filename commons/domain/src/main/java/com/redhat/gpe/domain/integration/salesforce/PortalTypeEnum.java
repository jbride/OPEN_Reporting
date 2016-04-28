/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist PortalType
 */
public enum PortalTypeEnum {

    // CustomerPortal
    CUSTOMERPORTAL("CustomerPortal"),
    // None
    NONE("None"),
    // Partner
    PARTNER("Partner");

    final String value;

    private PortalTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static PortalTypeEnum fromValue(String value) {
        for (PortalTypeEnum e : PortalTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
