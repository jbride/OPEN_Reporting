package com.redhat.gpte.email;

import java.io.IOException;

import org.apache.camel.test.spring.CamelSpringTestSupport;import org.apache.camel.test.spring.CamelSpringTestSupport;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redhat.gpte.util.PropertiesSupport;

/* Tests SMTP server connection as well as Camel route responsible for sending emails
 * TO-DO: This test is written generically and as such should be refactored such that it is re-used across all GPTE integration projects as per:
 *     http://blog.anorakgirl.co.uk/2013/04/sharing-junit-tests-with-maven/
 */
public class CamelSMTPTest extends CamelSpringTestSupport {
    
    private static final String EMAIL_USERNAME = "gpte_email_username";
    private static final String EMAIL_PASSOWRD = "gpte_email_password";
    private static final String RECIPIENT_EMAIL = "admin_email";
    private static final String SMTP_SERVER = "gpte_smtp_server";
    private static final String SMTP_PORT = "gpte_smtp_port";
    private static final String RETURN_PATH = "Return-Path";
    
    private String userId = null;
    private String password = null;
    private String recipientEmail = null;
    private String smtpServer = null;
    private int smtpPort = 0;
    private String testFilePath = "src/test/resources/";

    public CamelSMTPTest() throws IOException {
        PropertiesSupport.setupProps();
    }
    
    @Before
    public void init() throws IOException {

        userId = System.getProperty(EMAIL_USERNAME);
        password = System.getProperty(EMAIL_PASSOWRD);
        recipientEmail = System.getProperty(RECIPIENT_EMAIL);
        smtpServer = System.getProperty(SMTP_SERVER);
        smtpPort = Integer.parseInt(System.getProperty(SMTP_PORT));

        System.out.println("init() "+ smtpServer+" : "+smtpPort+" : "+userId);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/gpte-shared-camel-context.xml");
    }

    @Ignore
    @Test
    public void testCamelSendEmail() throws InterruptedException {
        template.setDefaultEndpointUri("vm:send-email");
        template.sendBody(new Object());
    }

}
