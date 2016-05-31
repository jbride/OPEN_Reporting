package com.redhat.gpte.inbound_email.test;

import java.io.IOException;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.camel.test.spring.CamelSpringTestSupport;
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
public class SMTPTest extends CamelSpringTestSupport {
    
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
    
    public SMTPTest() throws IOException {
        PropertiesSupport.setupProps();
    }
    
    @Before
    public void init() {
        userId = System.getProperty(EMAIL_USERNAME);
        password = System.getProperty(EMAIL_PASSOWRD);
        recipientEmail = System.getProperty(RECIPIENT_EMAIL);
        smtpServer = System.getProperty(SMTP_SERVER);
        smtpPort = Integer.parseInt(System.getProperty(SMTP_PORT));
    }

    @Ignore
    @Test
    public void testSMTPServerConnectionAndSend() throws Exception {

        String fileName = "NEW_exercise_results_darby_samp_single_assessment.csv";
        
        // create simple email and send it out
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(smtpServer);
        email.setSmtpPort(smtpPort);
        email.setAuthenticator(new DefaultAuthenticator(userId, password));
        email.setSSLOnConnect(true);
        
        email.setFrom(userId); // Defines value of "X-Google-Original-From" field in header
        email.setSubject("TestMail");
        email.setMsg("This is a test mail ... :-)");
        email.addTo(recipientEmail);

        // Create the attachment
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(testFilePath + fileName);
        attachment.setName(fileName);
        attachment.setDisposition(EmailAttachment.ATTACHMENT);

        // now attach it
        email.attach(attachment);

        // send it out
        email.send();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/environment-specific-camel-context.xml", "/spring/course-completion-camel-context.xml");
    }

}
