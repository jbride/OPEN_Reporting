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

/* Purpose : test exception handling of various problematic input data input scenarious
 */
public class DokeosAttachmentValidationTest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(DokeosAttachmentValidationTest.class);
    public static final String RECEIVE_VALIDATE_INPUT_URI = "cc_receive_dokeos_input_uri";
    public static final String INBOX_PATH = "target/test-classes/sample-spreadsheets/dokeos";
    public static final String ADMIN_EMAIL = "admin_email";
    public static final String RETURN_PATH = "Return-Path";
    public static final String CAMEL_FILE_NAME = "CamelFileName";
    public static final String GOOD_TEST_FILE = "app_dev_eap_new.csv";
    public static final String SUBJECT = "subject";
    public static final String DOKEOS_SUBJECT = "New Student Accreditations from rh.dokeos.com";
    public static final String TEST_FILE_02 = "test02InvalidAttachmentType.xls";
    public static final String TEST_FILE_05 = "test05IncorrectNumberOfColumnsInSpreadsheet.csv";
    public static final String TEST_FILE_06 = "test06BindyUnMarshallingError.csv";
    public static final String TEST_FILE_07 = "test07AmbiguousPassFail.csv";
    public static final String TEST_FILE_08 = "test08UnknownAccreditation.csv";
    public static final String TEST_FILE_09 = "test09UnknownQualification.csv";
    public static final String TEST_FILE_10 = "test10UnknownUser.csv";
    public static final String TEST_FILE_11 = "incorrectCourseName.csv";

    private String routeURI = null;
    private String adminEmail = null;
    private Map<String,Object> headers = null;
    private Endpoint endpoint = null;
    
    public DokeosAttachmentValidationTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {

        routeURI = System.getProperty(RECEIVE_VALIDATE_INPUT_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+RECEIVE_VALIDATE_INPUT_URI);
        endpoint = context.getEndpoint(routeURI);

        adminEmail = System.getProperty(ADMIN_EMAIL);
        
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("routeURI = "+routeURI);
        System.out.println("init() AttachmentValidationTest props as follows\n"+sBuilder.toString());

        headers = new HashMap<String, Object>();

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
            throw new RuntimeException("the following file does not exist: "+inbox_file.getPath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        headers.put(SUBJECT, DOKEOS_SUBJECT);
        in.setHeaders(headers);
        FileInputStream fStream = fStream = new FileInputStream(inbox_file);
        String attachment = IOUtils.toString(fStream);
        in.setBody(attachment);
        exchange = template.send(routeURI, exchange);
        fStream.close();
        
        //Student object is currently composed of all String fields.
        //Subsequently, all types passed in csv are valid and don't throw a Parse Exception
        //assertTrue(exchange.getException() == null);
    }

    @Ignore
    @Test
    public void test01InvalidSender() throws InterruptedException {
        headers.put(RETURN_PATH, "jbride2001@yahoo.com");
        template.sendBodyAndHeaders(routeURI, new Object(), headers);
    }

    @Ignore
    @Test
    public void test02InvalidAttachmentType() throws InterruptedException, IOException {
        File inbox_file = new File(INBOX_PATH, TEST_FILE_02);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getPath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        in.setHeaders(headers);
        FileInputStream fStream = fStream = new FileInputStream(inbox_file);
        String attachment = IOUtils.toString(fStream);
        fStream.close();
        in.setBody(attachment);
        in.addAttachment(TEST_FILE_02, new DataHandler(new FileDataSource(inbox_file)));
        exchange = template.send(routeURI, exchange);
        assertTrue(exchange.getException() instanceof AttachmentValidationException);
        assertTrue(exchange.getException().getMessage().indexOf(AttachmentValidationException.ATTACHMENT_MUST_HAVE_CSV_SUFFIX) > 0);
    }

    @Ignore
    @Test
    public void test03NoAttachments() throws InterruptedException {
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        in.setBody("Test");
        headers.put(RETURN_PATH, adminEmail);
        in.setHeaders(headers);
        exchange = template.send(routeURI, exchange);
        assertTrue(exchange.getException() instanceof AttachmentValidationException);
        assertTrue(exchange.getException().getMessage().indexOf(AttachmentValidationException.NO_ATTACHMENTS_PROVIDED) > 0);
    }

    @Ignore
    @Test
    public void test04NoHeader() throws InterruptedException {
        //template.sendBodyAndHeader(routeURI, inbox_file, "CamelFileName", inbox_file.getPath());
    }

    @Ignore
    @Test
    public void test05IncorrectNumberOfColumnsInSpreadsheet() throws InterruptedException, IOException {
        File inbox_file = new File(INBOX_PATH, TEST_FILE_05);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getPath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        in.setBody("Test");
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        in.setHeaders(headers);
        FileInputStream fStream = fStream = new FileInputStream(inbox_file);
        String attachment = IOUtils.toString(fStream);
        fStream.close();
        in.setBody(attachment);
        exchange = template.send(routeURI, exchange);
        assertTrue(exchange.getException() instanceof AttachmentValidationException);
        assertTrue(exchange.getException().getMessage().indexOf(AttachmentValidationException.INVALID_NUM_ELEMENTS) > 0);
    }

    @Ignore
    @Test
    public void test06BindyUnMarshallingError() throws InterruptedException, IOException {
        File inbox_file = new File(INBOX_PATH, GOOD_TEST_FILE);
        if(!inbox_file.exists())
            throw new RuntimeException("the following file does not exist: "+inbox_file.getPath());
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        in.setBody("Test");
        headers.put(CAMEL_FILE_NAME, inbox_file.getPath());
        headers.put(RETURN_PATH, adminEmail);
        in.setHeaders(headers);
        FileInputStream fStream = fStream = new FileInputStream(inbox_file);
        String attachment = IOUtils.toString(fStream);
        fStream.close();
        in.setBody(attachment);
        exchange = template.send(routeURI, exchange);
        
        //Student object is currently composed of all String fields.  Subsequently, all types passed in csv are valid and don't throw a Parse Exception
        assertTrue(exchange.getException() == null);
    }

    @Ignore
    @Test
    public void test07AmbiguousPassFail() throws InterruptedException {
        //template.sendBodyAndHeader(routeURI, inbox_file, "CamelFileName", inbox_file.getPath());
    }

    @Ignore
    @Test
    public void test08UnknownAccreditation() throws InterruptedException {
        //template.sendBodyAndHeader(routeURI, inbox_file, "CamelFileName", inbox_file.getPath());
    }
    
    @Ignore
    @Test
    public void test09UnknownQualification() throws InterruptedException {
        //template.sendBodyAndHeader(routeURI, inbox_file, "CamelFileName", inbox_file.getPath());
    }

    @Ignore
    @Test
    public void test10UnknownUser() throws InterruptedException {
        //template.sendBodyAndHeader(routeURI, inbox_file, "CamelFileName", inbox_file.getPath());
    }
    
    @Ignore
    @Test
    public void test11IncorrectCourseName() throws InterruptedException {
        
    }
}
