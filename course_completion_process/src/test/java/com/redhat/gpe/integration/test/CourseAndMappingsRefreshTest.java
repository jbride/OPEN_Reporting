package com.redhat.gpe.integration.test;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.gpte.services.AttachmentValidationException;
import com.redhat.gpte.util.PropertiesSupport;

public class CourseAndMappingsRefreshTest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseAndMappingsRefreshTest.class);
    public static final String ROUTE_URI = "vm:cc_process-new-courses-and-mappings-uri";
    public static final String INBOX_PATH = "target/test-classes/sample-spreadsheets/courses";
    public static final String ADMIN_EMAIL = "admin_email";
    public static final String RETURN_PATH = "Return-Path";
    public static final String GOOD_TEST_FILE = "GPTE Accreditation Rules with Validation - Courses & Mappings.tsv";
    public static final String CAMEL_FILE_NAME = "CamelFileName";
    public static final String SUBJECT = "subject";

    private String adminEmail = null;
    private Map<String,Object> headers = null;
    private Endpoint endpoint = null;
    
    public CourseAndMappingsRefreshTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {
        headers = new HashMap<String, Object>();
        endpoint = context.getEndpoint(ROUTE_URI);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/course-completion-camel-context.xml");
    }
    
    @Ignore
    @Test
    public void test00ValidAttachment() throws InterruptedException, IOException {
        File inbox_file = new File(INBOX_PATH, GOOD_TEST_FILE);
        if(!inbox_file.exists())
            throw new RuntimeException("The following file does not exist: "+inbox_file.getPath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        headers.put(SUBJECT, "Course and CourseMappings Refresh");
        in.setHeaders(headers);
        Map<String,String> attachments = new HashMap<String,String>();
        FileInputStream fStream = fStream = new FileInputStream(inbox_file);
        String attachment = IOUtils.toString(fStream);
        fStream.close();
        attachments.put(GOOD_TEST_FILE, attachment);
        in.setBody(attachments);
        exchange = template.send(ROUTE_URI, exchange);
    }

}
