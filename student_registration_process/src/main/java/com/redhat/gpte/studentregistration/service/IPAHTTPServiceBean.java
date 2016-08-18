package com.redhat.gpte.studentregistration.service;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.helper.DenormalizedStudent;

public class IPAHTTPServiceBean {

    public static final String LDAP_HTTP_URL = "ipa_ldap_http.url";
    public static final String LDAP_HTTP_USER_NAME = "ipa_ldap_http.username";
    public static final String LDAP_HTTP_PASSWORD = "ipa_ldap_http.password";
    public static final String LDAP_GROUPNAME = "ipa_ldap.groupName";
    public static final String LDAP_END_DATE = "ipa_ldap.endDate";
    public static final String LDAP_SEND_MAIL = "ipa_ldap.sendMail";
    private static final String IPA_UPLOAD_ABSOLUTE_PATH = null;
    
    private Logger logger = Logger.getLogger(getClass());
    
    private String ldapHTTPUrl;
    private String ldapHTTPUserName;
    private String ldapHTTPPassword;
    private String groupName;
    private String endDate;
    private String sendMail;
    
    public IPAHTTPServiceBean() {
        ldapHTTPUrl = System.getProperty(LDAP_HTTP_URL);
        if(StringUtils.isEmpty(ldapHTTPUrl))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_HTTP_URL);
        ldapHTTPUserName = System.getProperty(LDAP_HTTP_USER_NAME);
        if(StringUtils.isEmpty(ldapHTTPUserName))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_HTTP_USER_NAME);
        ldapHTTPPassword = System.getProperty(LDAP_HTTP_PASSWORD);
        if(StringUtils.isEmpty(ldapHTTPPassword))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_HTTP_PASSWORD);
        groupName = System.getProperty(LDAP_GROUPNAME);
        if(StringUtils.isEmpty(groupName))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_GROUPNAME);
        endDate = System.getProperty(LDAP_END_DATE);
        if(StringUtils.isEmpty(endDate))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_END_DATE);
        sendMail = System.getProperty(LDAP_SEND_MAIL);
        if(StringUtils.isEmpty(sendMail))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_SEND_MAIL);
    }
    
    
    public void createLdapTemplateFile(Exchange exchange) throws Exception {
        List<DenormalizedStudent> students = (List<DenormalizedStudent>) exchange.getIn().getBody();
        
        // leave these values blank: subregion, dokeos and sso
        String subregion = "";
        String dokeosId = "";
        String sso = "";
        
        StringBuilder data = new StringBuilder();
        
        
        data.append("Name;Email;Company;Region | Subregion;Dokeos;USERID;SSO;Role");
        data.append("\n");
        
        for (DenormalizedStudent dStudent : students) {
            Student student = dStudent.getStudentObj();
            Company company = dStudent.getCompanyObj();
            
            String email = student.getEmail();
            String userId = email.replace('@', '-');
            String region = student.getRegion();
            String role = student.getRoles();
            
            data.append(student.getFirstname() + " " + student.getLastname() + ";");
            data.append(email + ";");
            data.append(company.getCompanyname() + ";");
            data.append(region + " | " + subregion + ";");
            data.append(dokeosId + ";");
            data.append(userId + ";");
            data.append(sso + ";");
            data.append(role);
            
            data.append("\n");
        }
        exchange.getIn().setBody(data.toString());        
    }
    
    public void uploadToLdapServer(Exchange exchange) throws Exception {
        
        String bodyString = (String)exchange.getIn().getBody();
        File uploadFile = FileUtils.getFile(bodyString);
        if(uploadFile == null)
            throw new RuntimeException("uploadToLdapServer() uploadFile not on message body");
        
        logger.info("Sending data to LDAP server: [" + ldapHTTPUrl + "] ...");
        
        HttpResponse<String> result = Unirest.post(ldapHTTPUrl)                
                                            .basicAuth(ldapHTTPUserName, ldapHTTPPassword)
                                            .header("accept", "text/plain")
                                            .field("file", uploadFile, "multipart/form-data")
                                            .field("groupName", groupName)
                                            .field("endDate", endDate)
                                            .field("sendMail", sendMail)
                                            .asString();
                 
        logger.info("Result status code: " + result.getStatus());
        logger.info("Result status text: " + result.getStatusText());

        if (result.getStatus() != 200) {
            logger.fatal("Error message from LDAP server. Status code: " + result.getStatus() + ", Status text: " + result.getStatusText());
        }
    }
}
