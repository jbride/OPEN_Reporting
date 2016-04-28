package com.redhat.gpte.studentregistration.test;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Store;
import javax.mail.Session;
import com.sun.mail.imap.IMAPStore;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.junit.Test;
import org.junit.Ignore;

public class IMAPTest {

    static String protocol = "imaps";
    static String host = "imap.gmail.com";
    static int port = 993;
    static String username = "lmsldap.rh.test@gmail.com";
    static String password = "Fusejboss99";
    static String folderName = "Inbox";

    @Ignore
    @Test
    public void testConnect() throws Exception {
        Properties mailProps =  new Properties();

        Session sessionObj  = Session.getInstance(mailProps , new DefaultAuthenticator(username, password));
        Store store  = sessionObj.getStore(protocol);
        store.connect(host, port, username, password);
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        System.out.println("inbox count = "+folder.getMessageCount());
    }


    static class DefaultAuthenticator extends Authenticator {

        public DefaultAuthenticator(String username, String password) {
            password = password;
            username = username;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }

    }
}
