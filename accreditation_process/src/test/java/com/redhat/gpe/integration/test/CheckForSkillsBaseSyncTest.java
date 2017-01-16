package com.redhat.gpe.integration.test;

import java.io.IOException;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.gpte.util.PropertiesSupport;

public class CheckForSkillsBaseSyncTest extends CamelSpringTestSupport {
    
    public static final String CHECK_FOR_SB_SYNC_URI = "accred_process-update-accred-to-skillsbase-batch";
    private static final Logger logger = LoggerFactory.getLogger(CheckForSkillsBaseSyncTest.class);
    private String routeURI = null;
    
    public CheckForSkillsBaseSyncTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {

        routeURI = System.getProperty(CHECK_FOR_SB_SYNC_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+CHECK_FOR_SB_SYNC_URI);

    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    @Ignore
    @Test
    public void testAttachmentProcessing() throws InterruptedException {
        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new Object());
    }
}
