/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist ContactStatusDataDotCom
 */
public enum ContactStatusDataDotComEnum {

    // CompanyNeverExistedDataDotCom
    COMPANYNEVEREXISTEDDATADOTCOM("CompanyNeverExistedDataDotCom"),
    // CompanyOutOfBusinessDataDotCom
    COMPANYOUTOFBUSINESSDATADOTCOM("CompanyOutOfBusinessDataDotCom"),
    // DeadEmailDomainDataDotCom
    DEADEMAILDOMAINDATADOTCOM("DeadEmailDomainDataDotCom"),
    // InactiveDataDotCom
    INACTIVEDATADOTCOM("InactiveDataDotCom"),
    // IsActiveContactDataDotCom
    ISACTIVECONTACTDATADOTCOM("IsActiveContactDataDotCom"),
    // NotAtCompanyContactDataDotCom
    NOTATCOMPANYCONTACTDATADOTCOM("NotAtCompanyContactDataDotCom"),
    // WrongEmailDataDotCom
    WRONGEMAILDATADOTCOM("WrongEmailDataDotCom"),
    // WrongPhoneAndEmailDataDotCom
    WRONGPHONEANDEMAILDATADOTCOM("WrongPhoneAndEmailDataDotCom"),
    // WrongPhoneDataDotCom
    WRONGPHONEDATADOTCOM("WrongPhoneDataDotCom");

    final String value;

    private ContactStatusDataDotComEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static ContactStatusDataDotComEnum fromValue(String value) {
        for (ContactStatusDataDotComEnum e : ContactStatusDataDotComEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
