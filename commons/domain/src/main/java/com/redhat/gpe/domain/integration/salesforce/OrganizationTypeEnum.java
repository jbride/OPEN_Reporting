/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist OrganizationType
 */
public enum OrganizationTypeEnum {

    // Base Edition
    BASE_EDITION("Base Edition"),
    // Contact Manager Edition
    CONTACT_MANAGER_EDITION("Contact Manager Edition"),
    // Developer Edition
    DEVELOPER_EDITION("Developer Edition"),
    // Enterprise Edition
    ENTERPRISE_EDITION("Enterprise Edition"),
    // Personal Edition
    PERSONAL_EDITION("Personal Edition"),
    // Professional Edition
    PROFESSIONAL_EDITION("Professional Edition"),
    // Team Edition
    TEAM_EDITION("Team Edition"),
    // Unlimited Edition
    UNLIMITED_EDITION("Unlimited Edition");

    final String value;

    private OrganizationTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static OrganizationTypeEnum fromValue(String value) {
        for (OrganizationTypeEnum e : OrganizationTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
