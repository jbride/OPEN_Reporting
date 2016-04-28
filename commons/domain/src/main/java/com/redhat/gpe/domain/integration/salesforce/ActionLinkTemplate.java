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
 * Salesforce DTO for SObject ActionLinkTemplate
 */
@XStreamAlias("ActionLinkTemplate")
public class ActionLinkTemplate extends AbstractSObjectBase {

    // ActionLinkGroupTemplateId
    private String ActionLinkGroupTemplateId;

    @JsonProperty("ActionLinkGroupTemplateId")
    public String getActionLinkGroupTemplateId() {
        return this.ActionLinkGroupTemplateId;
    }

    @JsonProperty("ActionLinkGroupTemplateId")
    public void setActionLinkGroupTemplateId(String ActionLinkGroupTemplateId) {
        this.ActionLinkGroupTemplateId = ActionLinkGroupTemplateId;
    }

    // LabelKey
    private String LabelKey;

    @JsonProperty("LabelKey")
    public String getLabelKey() {
        return this.LabelKey;
    }

    @JsonProperty("LabelKey")
    public void setLabelKey(String LabelKey) {
        this.LabelKey = LabelKey;
    }

    // Method
    @XStreamConverter(PicklistEnumConverter.class)
    private MethodEnum Method;

    @JsonProperty("Method")
    public MethodEnum getMethod() {
        return this.Method;
    }

    @JsonProperty("Method")
    public void setMethod(MethodEnum Method) {
        this.Method = Method;
    }

    // LinkType
    @XStreamConverter(PicklistEnumConverter.class)
    private LinkTypeEnum LinkType;

    @JsonProperty("LinkType")
    public LinkTypeEnum getLinkType() {
        return this.LinkType;
    }

    @JsonProperty("LinkType")
    public void setLinkType(LinkTypeEnum LinkType) {
        this.LinkType = LinkType;
    }

    // Position
    private Integer Position;

    @JsonProperty("Position")
    public Integer getPosition() {
        return this.Position;
    }

    @JsonProperty("Position")
    public void setPosition(Integer Position) {
        this.Position = Position;
    }

    // IsConfirmationRequired
    private Boolean IsConfirmationRequired;

    @JsonProperty("IsConfirmationRequired")
    public Boolean getIsConfirmationRequired() {
        return this.IsConfirmationRequired;
    }

    @JsonProperty("IsConfirmationRequired")
    public void setIsConfirmationRequired(Boolean IsConfirmationRequired) {
        this.IsConfirmationRequired = IsConfirmationRequired;
    }

    // IsGroupDefault
    private Boolean IsGroupDefault;

    @JsonProperty("IsGroupDefault")
    public Boolean getIsGroupDefault() {
        return this.IsGroupDefault;
    }

    @JsonProperty("IsGroupDefault")
    public void setIsGroupDefault(Boolean IsGroupDefault) {
        this.IsGroupDefault = IsGroupDefault;
    }

    // UserVisibility
    @XStreamConverter(PicklistEnumConverter.class)
    private UserVisibilityEnum UserVisibility;

    @JsonProperty("UserVisibility")
    public UserVisibilityEnum getUserVisibility() {
        return this.UserVisibility;
    }

    @JsonProperty("UserVisibility")
    public void setUserVisibility(UserVisibilityEnum UserVisibility) {
        this.UserVisibility = UserVisibility;
    }

    // UserAlias
    private String UserAlias;

    @JsonProperty("UserAlias")
    public String getUserAlias() {
        return this.UserAlias;
    }

    @JsonProperty("UserAlias")
    public void setUserAlias(String UserAlias) {
        this.UserAlias = UserAlias;
    }

    // ActionUrl
    private String ActionUrl;

    @JsonProperty("ActionUrl")
    public String getActionUrl() {
        return this.ActionUrl;
    }

    @JsonProperty("ActionUrl")
    public void setActionUrl(String ActionUrl) {
        this.ActionUrl = ActionUrl;
    }

    // RequestBody
    private String RequestBody;

    @JsonProperty("RequestBody")
    public String getRequestBody() {
        return this.RequestBody;
    }

    @JsonProperty("RequestBody")
    public void setRequestBody(String RequestBody) {
        this.RequestBody = RequestBody;
    }

    // Headers
    private String Headers;

    @JsonProperty("Headers")
    public String getHeaders() {
        return this.Headers;
    }

    @JsonProperty("Headers")
    public void setHeaders(String Headers) {
        this.Headers = Headers;
    }

}
