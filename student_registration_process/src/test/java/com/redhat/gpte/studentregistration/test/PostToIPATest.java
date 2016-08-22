package com.redhat.gpte.studentregistration.test;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.camel.Endpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.gpte.studentregistration.service.IPAHTTPServiceBean;
import com.redhat.gpte.util.PropertiesSupport;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/* Purpose : test exception handling of various problematic input secenarios
 *
 */
public class PostToIPATest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(PostToIPATest.class);
    public static final String INPUT_URI = "sr_post-new-students-to-ipa-uri";
    public static final String LDAP_HTTP_URL = "ipa_ldap_http.url";
    public static final String LDAP_HTTP_USER_NAME = "ipa_ldap_http.username";
    public static final String LDAP_HTTP_PASSWORD = "ipa_ldap_http.password";
    public static final String LDAP_GROUPNAME = "ipa_ldap.groupName";
    public static final String LDAP_SEND_MAIL = "ipa_ldap.sendMail";
    private static final String IPA_UPLOAD_ABSOLUTE_PATH = null;

    private String routeURI = null;
    private Endpoint endpoint = null;
    
    public PostToIPATest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/student-registration-camel-context.xml");
    }

    /* curl -v -X POST -HAccept:text/plain -HgroupName:newuser -HsendMail:true --user jbride-redhat.com:xxx  \ 
            --insecure -F upload=@src/test/resources/sample-spreadsheets/ipa_upload_20160818_1015.txt \
            "https://www.opentlc.com/sso-admin/upload_file.php"
    */
    @Ignore
    @Test
    public void testPostToIPAViaUnirest() throws UnirestException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        
        String ldapHTTPUrl = System.getProperty(LDAP_HTTP_URL);
        if(StringUtils.isEmpty(ldapHTTPUrl))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_HTTP_URL);
        String ldapHTTPUserName = System.getProperty(LDAP_HTTP_USER_NAME);
        if(StringUtils.isEmpty(ldapHTTPUserName))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_HTTP_USER_NAME);
        String ldapHTTPPassword = System.getProperty(LDAP_HTTP_PASSWORD);
        if(StringUtils.isEmpty(ldapHTTPPassword))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_HTTP_PASSWORD);
        String groupName = System.getProperty(LDAP_GROUPNAME);
        if(StringUtils.isEmpty(groupName))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_GROUPNAME);
        String sendMail = System.getProperty(LDAP_SEND_MAIL);
        if(StringUtils.isEmpty(sendMail))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_SEND_MAIL);
        
        File uploadFile = new File("target/test-classes/sample-spreadsheets/", "ipa_upload_20160818_1015.csv");
        if(!uploadFile.exists())
            throw new RuntimeException("uploadToLdapServer() uploadFile not found: "+ uploadFile.getAbsolutePath());
        
        logger.info("Sending data to LDAP server: [" + ldapHTTPUrl + "] ...");
        
        // Skip validation of SSL certificate with host
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        Unirest.setHttpClient(httpclient);
        
        HttpResponse<String> result = Unirest.post(ldapHTTPUrl)                
                                            .basicAuth(ldapHTTPUserName, ldapHTTPPassword)
                                            .header("accept", "text/plain")
                                            .field("file", uploadFile, "multipart/form-data")
                                            .field("groupName", groupName)
                                            .field("sendMail", sendMail)
                                            .asString();
                 
        logger.info("Result status code: " + result.getStatus());
        logger.info("Result status text: " + result.getStatusText());
        logger.info("Result body: "+ result.getBody());

        if (result.getStatus() != 200) {
            logger.error("Error message from LDAP server. Status code: " + result.getStatus() + ", Status text: " + result.getStatusText());
        }
    }
    
    @Ignore
    @Test
    public void testPostToIPAViaCamel() {
        routeURI = System.getProperty(INPUT_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+INPUT_URI);
        endpoint = context.getEndpoint(routeURI);

        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new Object());

    }
    
    @Ignore
    @Test
    public void testLDAPResponseProcessing() throws IOException {
        File uploadFile = new File("target/test-classes/sample-response/", "good-ipa-response.txt");
        if(!uploadFile.exists())
            throw new RuntimeException("testLDAPResponseProcessing() response file not found: "+ uploadFile.getAbsolutePath());
        
        IPAHTTPServiceBean.logLdapServerResponse(FileUtils.readFileToString(uploadFile));

    }
    

}
