/**
 * This class is generated by jOOQ
 */
package com.redhat.gpe.domain.canonical;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Company implements Serializable {

    private static final long serialVersionUID = 1249015856;
    public static final String RED_HAT_COMPANY_NAME = "Red Hat";
    public static final String COMPANY_ID = "companyid";
    public static final String ACCOUNT_ID = "accountid";
    public static final String COMPANY_NAME = "companyname";
    public static final String PARTNER_TYPE = "partnertype";
    public static final String PARTNER_TIER = "partnertier";
    public static final String LDAP_ID = "ldapId";
    public static final String FROM_CLAUSE = "c.CompanyID, c.AccountID, c.CompanyName, c.PartnerType, c.PartnerTier, c.LdapID ";

    private Integer companyid = 0;
    private String  accountid;
    private String  companyname;
    private String  partnertype;
    private String  partnertier;
    private String  ldapId;

    public Company() {}

    public Company(Company value) {
        this.companyid = value.companyid;
        this.accountid = value.accountid;
        this.companyname = value.companyname;
        this.partnertype = value.partnertype;
        this.partnertier = value.partnertier;
        this.ldapId = value.ldapId;
    }

    public Company(
        Integer companyid,
        String  accountid,
        String  companyname,
        String  partnertype,
        String  partnertier,
        String  ldapId
    ) {
        this.companyid = companyid;
        this.accountid = accountid;
        this.companyname = companyname;
        this.partnertype = partnertype;
        this.partnertier = partnertier;
        this.ldapId = ldapId;
    }

    public Integer getCompanyid() {
        return this.companyid;
    }

    public void setCompanyid(Integer companyid) {
        this.companyid = companyid;
    }

    public String getAccountid() {
        return this.accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getCompanyname() {
        return this.companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getPartnertype() {
        return this.partnertype;
    }

    public void setPartnertype(String partnertype) {
        this.partnertype = partnertype;
    }

    public String getPartnertier() {
        return this.partnertier;
    }

    public void setPartnertier(String partnertier) {
        this.partnertier = partnertier;
    }

    public String getLdapId() {
        return this.ldapId;
    }
    public void setLdapId(String x) {
        ldapId = x;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Companies (");

        sb.append(companyid);
        sb.append(", ").append(accountid);
        sb.append(", ").append(companyname);
        sb.append(", ").append(partnertype);
        sb.append(", ").append(partnertier);

        sb.append(")");
        return sb.toString();
    }
}
