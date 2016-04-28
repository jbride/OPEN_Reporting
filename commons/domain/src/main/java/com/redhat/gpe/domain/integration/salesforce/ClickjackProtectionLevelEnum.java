/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist ClickjackProtectionLevel
 */
public enum ClickjackProtectionLevelEnum {

    // AllowAllFraming
    ALLOWALLFRAMING("AllowAllFraming"),
    // NoFraming
    NOFRAMING("NoFraming"),
    // SameOriginOnly
    SAMEORIGINONLY("SameOriginOnly");

    final String value;

    private ClickjackProtectionLevelEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static ClickjackProtectionLevelEnum fromValue(String value) {
        for (ClickjackProtectionLevelEnum e : ClickjackProtectionLevelEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
