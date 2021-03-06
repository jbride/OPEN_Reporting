/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Salesforce DTO for SObject UserPackageLicense
 */
@XStreamAlias("UserPackageLicense")
public class UserPackageLicense extends AbstractSObjectBase {

    // PackageLicenseId
    private String PackageLicenseId;

    @JsonProperty("PackageLicenseId")
    public String getPackageLicenseId() {
        return this.PackageLicenseId;
    }

    @JsonProperty("PackageLicenseId")
    public void setPackageLicenseId(String PackageLicenseId) {
        this.PackageLicenseId = PackageLicenseId;
    }

    // UserId
    private String UserId;

    @JsonProperty("UserId")
    public String getUserId() {
        return this.UserId;
    }

    @JsonProperty("UserId")
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

}
