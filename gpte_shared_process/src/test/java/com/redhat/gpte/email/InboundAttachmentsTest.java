package com.redhat.gpte.email;

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
import org.junit.Test;
import org.junit.Ignore;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.gpte.util.PropertiesSupport;

/* Purpose : test exception handling of various problematic input secenarios
 */
public class InboundAttachmentsTest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(InboundAttachmentsTest.class);
    public static final String RECEIVE_VALIDATE_INPUT_URI = "gpte_receive_ops_files_uri";
    public static final String INBOX_PATH = "target/test-classes/sample-spreadsheets/";
    public static final String ADMIN_EMAIL = "admin_email";
    public static final String RETURN_PATH = "Return-Path";
    public static final String CAMEL_FILE_NAME = "CamelFileName";
    public static final String SUBJECT = "subject";
    public static final String STUDENT_REG_MINI_FILE = "ELAB_Registration_Report_Mini.csv";
    public static final String DOKEOS_CC_MINI_FILE = "Dokeos_CC_Mini.csv";
    public static final String SUMTOTAL_CC_MINI_FILE = "Sumtotal_CC_Mini.csv";

    private String routeURI = null;
    private String adminEmail = null;
    private Map<String,Object> headers = null;
    private Endpoint endpoint = null;
    
    public InboundAttachmentsTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {

        routeURI = System.getProperty(RECEIVE_VALIDATE_INPUT_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+RECEIVE_VALIDATE_INPUT_URI);
        endpoint = context.getEndpoint(routeURI);
        adminEmail = System.getProperty(ADMIN_EMAIL);
 
        headers = new HashMap<String, Object>();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/gpte-shared-camel-context.xml");
    }
    
    @Ignore
    @Test
    public void test00StudentRegistration() throws IOException {
        File inbox_file = new File(INBOX_PATH, STUDENT_REG_MINI_FILE);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getAbsolutePath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        headers.put(SUBJECT, "New Student Registered in Sumtotal");
        in.setHeaders(headers);
        in.setBody("test");
        in.addAttachment(STUDENT_REG_MINI_FILE, new DataHandler(new FileDataSource(inbox_file)));
        exchange = template.send(routeURI, exchange);
    }
    
    @Ignore
    @Test
    public void test01DokeosCC() throws IOException {
        File inbox_file = new File(INBOX_PATH, DOKEOS_CC_MINI_FILE);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getAbsolutePath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        headers.put(SUBJECT, "Dokeos course completions");
        in.setHeaders(headers);
        in.setBody("test");
        in.addAttachment(STUDENT_REG_MINI_FILE, new DataHandler(new FileDataSource(inbox_file)));
        exchange = template.send(routeURI, exchange);
    }
    
    @Ignore
    @Test
    public void test02SumtotalCC() throws IOException {
        File inbox_file = new File(INBOX_PATH, SUMTOTAL_CC_MINI_FILE);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getAbsolutePath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        headers.put(SUBJECT, "Sumtotal course completions");
        in.setHeaders(headers);
        in.setBody("test");
        in.addAttachment(STUDENT_REG_MINI_FILE, new DataHandler(new FileDataSource(inbox_file)));
        exchange = template.send(routeURI, exchange);
    }

}
