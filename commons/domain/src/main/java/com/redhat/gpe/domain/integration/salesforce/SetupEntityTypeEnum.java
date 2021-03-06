/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist SetupEntityType
 */
public enum SetupEntityTypeEnum {

    // ApexClass
    APEXCLASS("ApexClass"),
    // ApexPage
    APEXPAGE("ApexPage"),
    // ConnectedApplication
    CONNECTEDAPPLICATION("ConnectedApplication"),
    // CustomPermission
    CUSTOMPERMISSION("CustomPermission"),
    // ExternalDataSource
    EXTERNALDATASOURCE("ExternalDataSource"),
    // NamedCredential
    NAMEDCREDENTIAL("NamedCredential"),
    // TabSet
    TABSET("TabSet");

    final String value;

    private SetupEntityTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static SetupEntityTypeEnum fromValue(String value) {
        for (SetupEntityTypeEnum e : SetupEntityTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
