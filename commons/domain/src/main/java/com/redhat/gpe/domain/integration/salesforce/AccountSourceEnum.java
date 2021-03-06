/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist AccountSource
 */
public enum AccountSourceEnum {

    // Other
    OTHER("Other"),
    // Partner Referral
    PARTNER_REFERRAL("Partner Referral"),
    // Phone Inquiry
    PHONE_INQUIRY("Phone Inquiry"),
    // Purchased List
    PURCHASED_LIST("Purchased List"),
    // Web
    WEB("Web");

    final String value;

    private AccountSourceEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static AccountSourceEnum fromValue(String value) {
        for (AccountSourceEnum e : AccountSourceEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
