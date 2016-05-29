package com.redhat.gpte.studentregistration.test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import javax.activation.FileDataSource;
import javax.activation.DataHandler;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.gpte.services.AttachmentValidationException;
import com.redhat.gpte.util.PropertiesSupport;

/* Purpose : test exception handling of various problematic input secenarios
 *
 */
public class PostToIPATest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(PostToIPATest.class);
    public static final String INPUT_URI = "sr_post-new-students-to-ipa-uri";

    private String routeURI = null;
    private Endpoint endpoint = null;
    
    public PostToIPATest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {

        routeURI = System.getProperty(INPUT_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+INPUT_URI);
        endpoint = context.getEndpoint(routeURI);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/student-registration-camel-context.xml");
    }
    
    @Ignore
    @Test
    public void testPostToIPA() {
        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new Object());

    }

}
