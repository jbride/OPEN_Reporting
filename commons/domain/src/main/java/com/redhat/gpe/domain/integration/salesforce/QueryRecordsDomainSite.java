/*
 * Salesforce Query DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.camel.component.salesforce.api.dto.AbstractQueryRecordsBase;

import java.util.List;

/**
 * Salesforce QueryRecords DTO for type DomainSite
 */
public class QueryRecordsDomainSite extends AbstractQueryRecordsBase {

    @XStreamImplicit
    private List<DomainSite> records;

    public List<DomainSite> getRecords() {
        return records;
    }

    public void setRecords(List<DomainSite> records) {
        this.records = records;
    }
}
