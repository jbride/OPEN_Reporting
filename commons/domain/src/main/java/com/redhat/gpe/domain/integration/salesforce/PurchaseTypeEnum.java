/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist PurchaseType
 */
public enum PurchaseTypeEnum {

    // 0
    _0("0"),
    // 1
    _1("1"),
    // 2
    _2("2");

    final String value;

    private PurchaseTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static PurchaseTypeEnum fromValue(String value) {
        for (PurchaseTypeEnum e : PurchaseTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
