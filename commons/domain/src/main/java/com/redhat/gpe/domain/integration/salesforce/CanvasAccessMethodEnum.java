/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist CanvasAccessMethod
 */
public enum CanvasAccessMethodEnum {

    // Get
    GET("Get"),
    // Post
    POST("Post");

    final String value;

    private CanvasAccessMethodEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static CanvasAccessMethodEnum fromValue(String value) {
        for (CanvasAccessMethodEnum e : CanvasAccessMethodEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
