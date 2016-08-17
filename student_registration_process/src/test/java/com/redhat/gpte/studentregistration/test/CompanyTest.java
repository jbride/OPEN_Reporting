package com.redhat.gpte.studentregistration.test;

import java.io.IOException;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.helper.DomainMockObjectHelper;
import com.redhat.gpte.util.PropertiesSupport;

public class CompanyTest extends CamelSpringTestSupport {

    private static final String PERSIST_COMPANY_URI = "direct:persist-company";
    private static final int RED_HAT_COMPANY_ID = 16518;
    private Endpoint endpoint = null;

    public CompanyTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/student-registration-camel-context.xml");
    }

    @Before
    public void init() {
        endpoint = context.getEndpoint(PERSIST_COMPANY_URI);
    }
    
    @Ignore
    @Test
    public void testPersistRHTCompany() throws InterruptedException {
        
        Company companyObj = DomainMockObjectHelper.getMockRHTCompany();
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        in.setBody(companyObj);
        exchange = template.send(PERSIST_COMPANY_URI, exchange);
        
        // throws:  org.springframework.dao.DuplicateKeyException: PreparedStatementCallback; SQL [insert into Companies values (null,?,?,?,?) ]; Duplicate entry 'Red Hat' for key 'IDX_CompanyName'
        //assertTrue(exchange.getException() instanceof AttachmentValidationException);
    }
    
    @Ignore
    @Test
    public void testUpdateRHTCompany() throws InterruptedException {
        
        Company companyObj = DomainMockObjectHelper.getMockRHTCompany();
        companyObj.setCompanyid(RED_HAT_COMPANY_ID);
        template.setDefaultEndpointUri(PERSIST_COMPANY_URI);
        template.sendBody(companyObj);
    }
    
    @Ignore
    @Test
    public void testPersistNonRHTCompany() throws InterruptedException {
        Company companyObj = DomainMockObjectHelper.getMockNonRHTCompany();
        template.setDefaultEndpointUri(PERSIST_COMPANY_URI);
        template.sendBody(companyObj);
    }

}
