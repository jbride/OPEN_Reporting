package com.redhat.gpte.email;

import java.io.IOException;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;

import com.redhat.gpte.util.PropertiesSupport;

/* Tests SMTP server connections
 * TO-DO: This test is written generically and as such should be refactored such that it is re-used across all GPTE integration projects as per:
 *     http://blog.anorakgirl.co.uk/2013/04/sharing-junit-tests-with-maven/
 */
public class SMTPTest {
    
    private static final String EMAIL_USERNAME = "gpte_email_username";
    private static final String EMAIL_PASSOWRD = "gpte_email_password";
    private static final String RECIPIENT_EMAIL = "recipient_email";
    private static final String REPLY_TO_EMAIL = "replyTo_email";
    private static final String SMTP_SERVER = "gpte_smtp_server";
    private static final String SMTP_PORT = "gpte_smtp_port";
    private static final String RETURN_PATH = "Return-Path";
    
    private String userId = null;
    private String password = null;
    private String recipientEmail = null;
    private String replyTo = null;
    private String smtpServer = null;
    private int smtpPort = 0;
    private String testFilePath = "src/test/resources/";

    @Before
    public void init() throws IOException {

        userId = System.getProperty(EMAIL_USERNAME);
        password = System.getProperty(EMAIL_PASSOWRD);
        recipientEmail = System.getProperty(RECIPIENT_EMAIL);
        replyTo = System.getProperty(REPLY_TO_EMAIL);
        smtpServer = System.getProperty(SMTP_SERVER);
        smtpPort = Integer.parseInt(System.getProperty(SMTP_PORT));

        System.out.println("\n\ninit() "+ smtpServer+" : "+smtpPort+" : "+userId+" : "+password+" : "+recipientEmail+"\n\n");
    }


    /* 
     *  Example Usage:
     *      mvn test -Dgpte_smtp_server=smtp.mail.yahoo.com -Dgpte_smtp_port=465 -Dgpte_email_username=rhtgptetest@yahoo.com -Dgpte_email_password=3_aY1wHZaU0qQp-1vZBNGZty -DreplyTo_email=rhtgptetest@yahoo.com -Drecipient_email=rhtgptetest@yahoo.com
     *      mvn test -Dgpte_smtp_server=smtp.mail.yahoo.com -Dgpte_smtp_port=465 -Dgpte_email_username=rhtgptetest@yahoo.com -Dgpte_email_password=3_aY1wHZaU0qQp-1vZBNGZty -DreplyTo_email=no-reply@cloudapps.5b3d.3scale.opentlc.com -Drecipient_email=rhtgptetest@yahoo.com
    */
    //@Ignore
    @Test
    public void testSMTPServerConnectionAndSend() throws Exception {

        String fileName = "sample-spreadsheets/Dokeos_CC_Mini.csv";
        
        // create simple email and send it out
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(smtpServer);
        email.setSmtpPort(smtpPort);
        email.setAuthenticator(new DefaultAuthenticator(userId, password));
        email.setSSLOnConnect(true);
        
        //email.setFrom(userId); // Defines value of "X-Google-Original-From" field in header
        email.setFrom(replyTo); // Defines value of "X-Google-Original-From" field in header
        email.addReplyTo(replyTo);
        email.addTo(recipientEmail);
        email.setSubject("TestMail");
        email.setMsg("This is a test mail ... :-)");

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

}
