/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.apache.camel.component.salesforce.api.PicklistEnumConverter;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Salesforce DTO for SObject CustomObjectUserLicenseMetrics
 */
@XStreamAlias("CustomObjectUserLicenseMetrics")
public class CustomObjectUserLicenseMetrics extends AbstractSObjectBase {

    // MetricsDate
    private org.joda.time.DateTime MetricsDate;

    @JsonProperty("MetricsDate")
    public org.joda.time.DateTime getMetricsDate() {
        return this.MetricsDate;
    }

    @JsonProperty("MetricsDate")
    public void setMetricsDate(org.joda.time.DateTime MetricsDate) {
        this.MetricsDate = MetricsDate;
    }

    // UserLicenseId
    private String UserLicenseId;

    @JsonProperty("UserLicenseId")
    public String getUserLicenseId() {
        return this.UserLicenseId;
    }

    @JsonProperty("UserLicenseId")
    public void setUserLicenseId(String UserLicenseId) {
        this.UserLicenseId = UserLicenseId;
    }

    // CustomObjectId
    private String CustomObjectId;

    @JsonProperty("CustomObjectId")
    public String getCustomObjectId() {
        return this.CustomObjectId;
    }

    @JsonProperty("CustomObjectId")
    public void setCustomObjectId(String CustomObjectId) {
        this.CustomObjectId = CustomObjectId;
    }

    // CustomObjectType
    @XStreamConverter(PicklistEnumConverter.class)
    private CustomObjectTypeEnum CustomObjectType;

    @JsonProperty("CustomObjectType")
    public CustomObjectTypeEnum getCustomObjectType() {
        return this.CustomObjectType;
    }

    @JsonProperty("CustomObjectType")
    public void setCustomObjectType(CustomObjectTypeEnum CustomObjectType) {
        this.CustomObjectType = CustomObjectType;
    }

    // CustomObjectName
    private String CustomObjectName;

    @JsonProperty("CustomObjectName")
    public String getCustomObjectName() {
        return this.CustomObjectName;
    }

    @JsonProperty("CustomObjectName")
    public void setCustomObjectName(String CustomObjectName) {
        this.CustomObjectName = CustomObjectName;
    }

    // ObjectCount
    private Integer ObjectCount;

    @JsonProperty("ObjectCount")
    public Integer getObjectCount() {
        return this.ObjectCount;
    }

    @JsonProperty("ObjectCount")
    public void setObjectCount(Integer ObjectCount) {
        this.ObjectCount = ObjectCount;
    }

}
