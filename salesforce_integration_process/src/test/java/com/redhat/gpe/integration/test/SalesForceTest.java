package com.redhat.gpe.integration.test;

import java.io.IOException;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redhat.gpte.test.PropertiesTestSupport;

// Purpose:  test updating of accreditation data in SalesForce
public class SalesForceTest extends CamelSpringTestSupport {

    private static final String SALESFORCE_ACCREDITATION_UPDATE_ROUTE_URI = "cc_salesforce_uri";
    
    private static final Logger logger = LoggerFactory.getLogger(SalesForceTest.class);
    private String routeURI = null;
    
    public SalesForceTest() throws IOException {
        PropertiesTestSupport.setupProps();
    }

    @Before
    public void init() {

        routeURI = System.getProperty(SALESFORCE_ACCREDITATION_UPDATE_ROUTE_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+SALESFORCE_ACCREDITATION_UPDATE_ROUTE_URI);

    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/environment-specific-camel-context.xml", "/spring/course-completion-camel-context.xml");
    }


    @Test
    @Ignore
    public void tesSalesForce() throws InterruptedException {
        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new Object());
    }
}
