package com.redhat.gpe.integration.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.gpte.util.PropertiesSupport;

// Purpose:  test processing of valid CSV input file
public class SumtotalAttachmentProcessTest extends CamelSpringTestSupport {
    
    public static final String INBOX_PATH = "target/test-classes/sample-spreadsheets/sumtotal";
    public static final String ADMIN_EMAIL = "admin_email";
    public static final String RETURN_PATH = "Return-Path";
    public static final String SUBJECT = "subject";
    public static final String SUMTOTAL_SUBJECT = "New Student Accreditations from Sumtotal";
    public static final String GOOD_TEST_FILE = "ST_completion_aug_2016_short.csv";
    public static final String RECEIVE_VALIDATE_INPUT_URI = "cc_receive_sumtotal_input_uri";
    public static final String CAMEL_FILE_NAME = "CamelFileName";
    private static final Logger logger = LoggerFactory.getLogger(SumtotalAttachmentProcessTest.class);
    
    private String routeURI = null;
    private String adminEmail = null;
    private Map<String,Object> headers = null;
    private Endpoint endpoint = null;
    
    public SumtotalAttachmentProcessTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() throws IOException {
        
        routeURI = System.getProperty(RECEIVE_VALIDATE_INPUT_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+RECEIVE_VALIDATE_INPUT_URI);
        endpoint = context.getEndpoint(routeURI);
        adminEmail = System.getProperty(ADMIN_EMAIL);
        
        headers = new HashMap<String, Object>();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/course-completion-camel-context.xml");
    }

    //@Ignore
    @Test
    public void testAttachmentProcessing() throws InterruptedException, IOException {
        
        File inbox_file = new File(INBOX_PATH, GOOD_TEST_FILE);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getPath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        in.setBody("Test");
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        headers.put(SUBJECT, SUMTOTAL_SUBJECT);
        in.setHeaders(headers);
        FileInputStream fStream = fStream = new FileInputStream(inbox_file);
        String attachment = IOUtils.toString(fStream);
        fStream.close();
        in.setBody(attachment);
        exchange = template.send(routeURI, exchange);
        
        //Student object is currently composed of all String fields.  Subsequently, all types passed in csv are valid and don't throw a Parse Exception
        assertTrue(exchange.getException() == null);
    }
}
