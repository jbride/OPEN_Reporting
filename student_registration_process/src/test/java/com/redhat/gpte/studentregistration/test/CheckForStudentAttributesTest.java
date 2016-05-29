package com.redhat.gpte.studentregistration.test;

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

// Purpose:  test processing of valid CSV input file
public class CheckForStudentAttributesTest extends CamelSpringTestSupport {
    
    public static final String CHECK_STUDENT_ATTRIBUTES_URI = "sr_check-for-student_attributes_uri";
    private static final Logger logger = LoggerFactory.getLogger(CheckForStudentAttributesTest.class);
    private String routeURI = null;
    
    public CheckForStudentAttributesTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {
        routeURI = System.getProperty(CHECK_STUDENT_ATTRIBUTES_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+CHECK_STUDENT_ATTRIBUTES_URI);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/student-registration-camel-context.xml");
    }

    @Ignore
    @Test
    public void testAttachmentProcessing() throws InterruptedException {
        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new Object());
    }
}
