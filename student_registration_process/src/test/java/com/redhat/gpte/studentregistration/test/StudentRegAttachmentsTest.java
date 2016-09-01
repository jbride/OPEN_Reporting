package com.redhat.gpte.studentregistration.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.activation.FileDataSource;
import javax.activation.DataHandler;

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

/* Purpose : test exception handling of various problematic input secenarios
 */
public class StudentRegAttachmentsTest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentRegAttachmentsTest.class);
    public static final String RECEIVE_VALIDATE_INPUT_URI = "direct:receive-sumtotal-student-reg-input";
    public static final String INBOX_PATH = "target/test-classes/sample-spreadsheets/";
    public static final String ADMIN_EMAIL = "admin_email";
    public static final String RETURN_PATH = "Return-Path";
    public static final String CAMEL_FILE_NAME = "CamelFileName";
    public static final String GOOD_STUDENT_REG_FILE = "ELAB_Registration_Report.csv";
    public static final String MINI_STUDENT_REG_FILE = "ELAB_Registration_Report_Mini.csv";
    public static final String SUBJECT = "subject";
    public static final String NEW_STUDENT_SUBJECT = "New Student Registered in Sumtotal";

    private String adminEmail = null;
    private Map<String,Object> headers = null;
    private Endpoint endpoint = null;
    
    public StudentRegAttachmentsTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {

        endpoint = context.getEndpoint(RECEIVE_VALIDATE_INPUT_URI);
        adminEmail = System.getProperty(ADMIN_EMAIL);
 
        headers = new HashMap<String, Object>();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/student-registration-camel-context.xml");
    }
    
    @Ignore
    @Test
    public void test00StudentRegistration() throws IOException {
        File inbox_file = new File(INBOX_PATH, GOOD_STUDENT_REG_FILE);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getAbsolutePath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        headers.put(SUBJECT, NEW_STUDENT_SUBJECT);
        in.setHeaders(headers);
        FileInputStream fStream = fStream = new FileInputStream(inbox_file);
        String attachment = IOUtils.toString(fStream);
        fStream.close();
        in.setBody(attachment);
        exchange = template.send(RECEIVE_VALIDATE_INPUT_URI, exchange);
    }

    //@Ignore
    @Test
    public void test01OneStudentRegistration() throws IOException {
        File inbox_file = new File(INBOX_PATH, MINI_STUDENT_REG_FILE);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getAbsolutePath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        headers.put(SUBJECT, NEW_STUDENT_SUBJECT);
        in.setHeaders(headers);
        FileInputStream fStream = fStream = new FileInputStream(inbox_file);
        String attachment = IOUtils.toString(fStream);
        fStream.close();
        in.setBody(attachment);
        exchange = template.send(RECEIVE_VALIDATE_INPUT_URI, exchange);
    }

}
