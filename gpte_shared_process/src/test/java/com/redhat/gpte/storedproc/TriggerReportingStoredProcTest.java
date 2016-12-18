package com.redhat.gpte.storedproc;

import java.io.IOException;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redhat.gpte.util.PropertiesSupport;

public class TriggerReportingStoredProcTest extends CamelSpringTestSupport {

    private static final String REPORTING_STORED_PROC = "direct:up-trigger-lms-reporting-stored-proc";
    
    private String routeURI = null;
    
    public TriggerReportingStoredProcTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/gpte-universal-camel-context.xml");
    }


    @Ignore
    @Test
    public void testStoredProc() throws InterruptedException {
        template.setDefaultEndpointUri(REPORTING_STORED_PROC);
        template.sendBody(new Object());
    }
}
