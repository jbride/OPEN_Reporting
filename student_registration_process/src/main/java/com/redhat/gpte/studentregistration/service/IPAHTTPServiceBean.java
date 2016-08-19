package com.redhat.gpte.studentregistration.service;

import java.io.File;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.helper.DenormalizedStudent;
import com.redhat.gpte.services.GPTEBaseServiceBean;

public class IPAHTTPServiceBean extends GPTEBaseServiceBean {

    public static final String LDAP_HTTP_URL = "ipa_ldap_http.url";
    public static final String LDAP_HTTP_USER_NAME = "ipa_ldap_http.username";
    public static final String LDAP_HTTP_PASSWORD = "ipa_ldap_http.password";
    public static final String LDAP_GROUPNAME = "ipa_ldap.groupName";
    public static final String LDAP_SEND_MAIL = "ipa_ldap.sendMail";
    private static final String IPA_UPLOAD_ABSOLUTE_PATH = null;
    private static final String SPACE=" ";
    private static final String DELIMITER=";";
    private static final String PIPE=" | ";
    private static final String NEW_LINE="\n";
    private static final String ALL_GOOD="Import exited cleanly";
    private static final String WARNING="WARNING";
    private static final String BREAK="<br />";
    private static final String ADDING="Adding";
    private static final String REMOVING="Removing";
    private static final String PLEASE_WAIT="Please wait processing....";
    private static final String DIV="</div>";
    private static final String EXAMINING="Examining";
    private static final String LOOKING="Looking";
    private static final String USER="User ";
    private static final String ERROR="ERROR:";
    private static final String NEW_STUDENT="newstudent_";
    private static final String CSV=".csv";
    SimpleDateFormat dfObj = new SimpleDateFormat("yyyyMMddHHmmss");
    
    private static Logger logger = Logger.getLogger("IPAHTTPServiceBean");
    
    private String ldapHTTPUrl;
    private String ldapHTTPUserName;
    private String ldapHTTPPassword;
    private String groupName;
    private String sendMail;
    
    public IPAHTTPServiceBean() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
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
        sendMail = System.getProperty(LDAP_SEND_MAIL);
        if(StringUtils.isEmpty(sendMail))
            throw new RuntimeException("init() must pass sys property of: "+LDAP_SEND_MAIL);
        
        // Skip validation of SSL certificate with host
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        Unirest.setHttpClient(httpclient);
    }
    
    
    public void createLdapTemplateFile(Exchange exchange) throws Exception {
        List<DenormalizedStudent> students = (List<DenormalizedStudent>) exchange.getIn().getBody();
        
        // leave these values blank: subregion, dokeos and sso
        String dokeosId = "";
        String sso = "";
        
        StringBuilder data = new StringBuilder();
        
        
        data.append("Name;Email;Company;Region | Subregion;Dokeos;USERID;SSO;Role");
        data.append("\n");
        
        for (DenormalizedStudent dStudent : students) {
            Student student = dStudent.getStudentObj();
            Company company = dStudent.getCompanyObj();
            String companyName = company.getCompanyname();
            String firstName = student.getFirstname();
            String lastName = student.getLastname();
            String email = student.getEmail();
            String userId = email.replace('@', '-');
            String region = student.getRegion();
            String role = student.getRoles();
            String subregion = student.getSubregion();
            
            if(StringUtils.isNotEmpty(firstName))
                data.append(firstName);
            data.append(SPACE);
            if(StringUtils.isNotEmpty(lastName))
                data.append(lastName);
            data.append(DELIMITER);
            if(StringUtils.isNotEmpty(email))
                data.append(email);
            data.append(DELIMITER);
            if(StringUtils.isNotEmpty(companyName))
                data.append(companyName);
            data.append(DELIMITER);
           
            if(StringUtils.isNotEmpty(region))
                data.append(region);
            data.append(PIPE);
            if(StringUtils.isNotEmpty(subregion))
                data.append(subregion);
            data.append(DELIMITER);
            
            data.append(dokeosId + ";");
            if(StringUtils.isNotEmpty(userId))
                data.append(userId);
            data.append(DELIMITER);
            data.append(sso + ";");
            
            if(StringUtils.isNotEmpty(role))
                data.append(role);
            
            data.append("\n");
        }
        exchange.getIn().setBody(data.toString());        
    }
    
    public void uploadToLdapServer(Exchange exchange) throws Exception {
        
        String bodyString = (String)exchange.getIn().getBody();
        Set<String> exceptionSet = new HashSet<String>();
        
        /*
         *  IPA has a bug where upload of each new student takes about 2-3 minutes.
         *  Subsequently, can not upload an entire batch file because processing will take longer than network timeouts.
        */
        String[] studentLines = bodyString.split("\\r?\\n");
        
        
        String header = null;
        int count = 0;
        for(String studentLine : studentLines ) {

            if(count == 0) {
                header = studentLine;
                count++;
                continue;
            }
            
            String email = StringUtils.substringBetween(studentLine, DELIMITER, DELIMITER);
            String dateS = dfObj.format(new Date());
            String fileName = NEW_STUDENT+dateS+CSV;
            File uploadFile = new File("/tmp", fileName);
            FileUtils.writeStringToFile(uploadFile, header + NEW_LINE + studentLine);

            logger.info(email+" : Sending data to LDAP server: [" + ldapHTTPUrl + "] ..."+fileName);

            String responseBody = null;
            boolean mockUpload = false;
            if(!mockUpload) {
                HttpResponse<String> result = Unirest.post(ldapHTTPUrl)                
                        .basicAuth(ldapHTTPUserName, ldapHTTPPassword)
                        .header("accept", "text/plain")
                        .field("file", uploadFile, "multipart/form-data")
                        .field("groupName", groupName)
                        .field("sendMail", sendMail)
                        .asString();

                responseBody = result.getBody();
            }else {
                responseBody = PLEASE_WAIT+ERROR+DIV;
            }
            int start = responseBody.indexOf(PLEASE_WAIT);
            responseBody = responseBody.substring(start, responseBody.indexOf(DIV, start));
            if(!responseBody.contains(ALL_GOOD) || responseBody.contains(ERROR)){
                logger.error(email+" : uploadToLdapServer() Result body: "+ responseBody);
                exceptionSet.add(email);
            } else {
                logLdapServerResponse(responseBody);
            }
            uploadFile.delete();

        }
        exchange.getIn().setHeader(UPLOAD_EXCEPTION_SET, exceptionSet);
    }
    
    public static void logLdapServerResponse(String rBody) {
        //logger.info(rBody);
        StringBuilder sBuilder = new StringBuilder("logLdapServerResponse() \n");
        String[] logLines = rBody.split("\\r?\\n");
        for(String lLine : logLines){
            if(lLine.contains(EXAMINING))
                sBuilder.append(lLine.substring(lLine.indexOf(EXAMINING))+NEW_LINE);
            if(lLine.contains(WARNING))
                sBuilder.append(lLine.substring(lLine.indexOf(WARNING))+NEW_LINE);
            if(lLine.contains(ADDING))
                sBuilder.append(lLine.substring(lLine.indexOf(ADDING))+NEW_LINE);
            if(lLine.contains(ALL_GOOD))
                sBuilder.append(lLine.substring(lLine.indexOf(ALL_GOOD))+NEW_LINE);
            if(lLine.contains(LOOKING))
                sBuilder.append(lLine.substring(lLine.indexOf(LOOKING))+NEW_LINE);
            if(lLine.contains(USER))
                sBuilder.append(lLine.substring(lLine.indexOf(USER))+NEW_LINE);
            if(lLine.contains(ERROR))
                sBuilder.append(lLine.substring(lLine.indexOf(ERROR))+NEW_LINE);
        }
        sBuilder.append(NEW_LINE);
        logger.info(sBuilder.toString());
    }
}
