package com.redhat.gpte.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

import javax.activation.DataHandler;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/* TO-DO: Much of this class is generic and could be re-used across all integration projects
 */
public class EmailServiceBean extends GPTEBaseServiceBean {

    public static final String ATTACHMENTS_ARE_VALID = "Attachments are valid.";
    private static final String RETURN_PATH = "Return-Path";
    private static final String DATE = "Date";
    private static final String SUBJECT = "Subject";
    private static final String GPTE_VALID_EMAIL_SUFFIXES = "gpte_valid_email_suffixes";
    private static final String ADMIN_EMAIL = "admin_email";
    private static final String DELIMITER = ",";
    private static final String CSV_SUFFIX = ".csv";
    private static final String TSV_SUFFIX = ".tsv";
    private static final String ATTACHMENT_TYPE = "ATTACHMENT_TYPE";
    private static final String DOKEOS_FIRST_LINE = "Fullname;Email;Exam name;Score;Date;Time";
    private static final String DOKEOS_ASSIGNMENT_FIRST_LINE = "Fullname;Email;Assignment;Score;Date;Time";
    private static final String SUMTOTAL_FIRST_LINE = "Full Name,Email,Activity Label,Activity Name,Activity Code,Attempt End Date";
    private static final String PARTNER_FIRST_LINE = "undefined";
    private static final String STUDENT_REG_FIRST_LINE = "Name,Email,Company,Region | Subregion,Dokeos,USERID,SSO,Role";
    private static final String RULES_SPREADSHEET_FIRST_LINE = "Condition    Condition";
    private static final String DOKEOS = "dokeos_cc";
    private static final String SUMTOTAL = "sumtotal_cc";
    private static final String PARTNER = "partner_cc";
    private static final String STUDENT_REG = "student_registration";
    private static final Object RULES_SPREADSHEET = "rules_spreadsheet";
    private static final String ACCRED_RULES_SPREADSHEET_INBOX_PATH = "accred_rules_spreadsheet_inbox_path";

    private Logger logger = Logger.getLogger(getClass());

    private String adminEmail;
    private Set<String> validEmailSuffixes;

    public EmailServiceBean() {
        
        adminEmail = System.getProperty(ADMIN_EMAIL);
        if(StringUtils.isEmpty(adminEmail))
            throw new RuntimeException("Must define a system property of: "+ADMIN_EMAIL);

        String vSuffixes = System.getProperty(GPTE_VALID_EMAIL_SUFFIXES);
        if(StringUtils.isEmpty(vSuffixes)) {
            throw new RuntimeException("Must define a system property of: "+GPTE_VALID_EMAIL_SUFFIXES);
        }else {
            String[] vEmailsSuffixArray = vSuffixes.split(",");
            validEmailSuffixes = new HashSet<String>(Arrays.asList(vEmailsSuffixArray));
        }
    }

    public boolean isValidCamelMessage(Exchange exchange) throws AttachmentValidationException {
        Message in = exchange.getIn();
        
        // make sure return email exists, it is from redhat.com and email has csv attachment(s)
        // Do not throw AttachmentValidationException as this would be expensive to handle if originating from DDoS attack
        String fromEmail = cleanEmailAddress(in.getHeader(RETURN_PATH, String.class));
        if(fromEmail != null) {
            logger.debug("fromEmail = "+fromEmail);
            String fromEmailSuffix = fromEmail.substring(fromEmail.indexOf("@")+1, fromEmail.indexOf(".com")+4);
            fromEmailSuffix = fromEmailSuffix.replace(">", "");
            if(StringUtils.isEmpty(fromEmail)) {
                logger.error("isValidCamelMessage() no return email address provided");
                return false;
            } else if (!validEmailSuffixes.contains(fromEmailSuffix))  {
                logger.error("isValidCamelMessage() email address is invalid (must origin from *@redhat.com) : "+fromEmailSuffix);
                return false;
            }
        } else {
            logger.error("isValidCamelMessage() email address path header not found");
            return false;
        }
        
        // Ensure email has attachments
        Map<String, DataHandler> attachments = exchange.getIn().getAttachments();
        if(attachments.size() == 0) {
            throw new AttachmentValidationException(AttachmentValidationException.NO_ATTACHMENTS_PROVIDED+" Input provided by "+fromEmail);
        } else {
            logger.debug("isValidCamelMessage() # attachments = "+attachments.size());
        }
        
        // Ensure email has CSV suffix
        Set<String> attachmentNames = exchange.getIn().getAttachmentNames();
        for (String attachName : attachmentNames) {
            if (!attachName.endsWith(CSV_SUFFIX) && !attachName.endsWith(TSV_SUFFIX)) {
                throw new AttachmentValidationException(AttachmentValidationException.ATTACHMENT_MUST_HAVE_CSV_SUFFIX+attachName);
            }
        }
        return true;
    }
    
    
    /* Iterates through attachments and converts each one to a String 
     * Message body is added with a List<String> of these converted attachments.
     */
    public void moveAttachmentsToBody(Exchange exchange) throws Exception {
        Map<String, DataHandler> attachments = exchange.getIn().getAttachments();
        Map<String,String> attachmentMap = new HashMap<String,String>();
        for(Entry<String, DataHandler> attachEntry : attachments.entrySet()){
            InputStream iStream = null;
            try {
                iStream = attachEntry.getValue().getInputStream();
                String attachString = IOUtils.toString(iStream);
                attachmentMap.put(attachEntry.getKey(), attachString);
            }finally {
                if(iStream != null)
                    iStream.close();
            }
        }
        exchange.getIn().setBody(attachmentMap);
    }

    public void changeFileBodyToMapBody(Exchange exchange) throws Exception {
        Map<String,String> attachmentMap = new HashMap<String,String>();
        org.apache.camel.component.file.GenericFile gFile = (org.apache.camel.component.file.GenericFile)exchange.getIn().getBody();
        File fileBody = (File)gFile.getBody();
        String stringBody = FileUtils.readFileToString(fileBody);

        attachmentMap.put(gFile.getFileName(), stringBody);
        exchange.getIn().setBody(attachmentMap);
    }
    
    public void writeRulesSpreadsheetsToDisk(Exchange exchange) throws IOException {
        String ss_inbox_path = System.getProperty(ACCRED_RULES_SPREADSHEET_INBOX_PATH);
        if(StringUtils.isEmpty(ss_inbox_path))
            throw new RuntimeException("Need to define the following system property: "+ACCRED_RULES_SPREADSHEET_INBOX_PATH);
        
        Map<String,String> attachments = (Map<String,String>) exchange.getIn().getBody();
        if(attachments.size() == 0)
            throw new RuntimeException("No rule spreadsheet attachments");
        
        File outDir = new File(ss_inbox_path);
        if(!outDir.exists())
            outDir.mkdirs();
        
        for(Entry<String, String> attachment : attachments.entrySet()) {
            FileOutputStream foStream = null;
            try {
                File outFile = new File(ss_inbox_path, attachment.getKey());
                foStream = new FileOutputStream(outFile);
                foStream.write(attachment.getValue().getBytes());
                foStream.flush();
                logger.info("writeRulesSpreadsheetsToDisk() just wrote rule spreadsheet: "+outFile.getAbsolutePath());
            }finally {
                if(foStream != null)
                    foStream.close();
            }
        }
    }

    
    public void determineAttachmentType(Exchange exchange) {
        Map<String,String> attachmentMap =  (Map<String,String>)exchange.getIn().getBody();
        Collection<String> attachCollection = attachmentMap.values();
        List<String> attachList = new ArrayList<String>(attachCollection);
        String[] rows = attachList.get(0).split("\\r?\\n");
        String firstRow = rows[0];
        if(firstRow.contains(DOKEOS_FIRST_LINE)) {
            exchange.getIn().setHeader(ATTACHMENT_TYPE, DOKEOS);
        }else if(firstRow.contains(DOKEOS_ASSIGNMENT_FIRST_LINE)) {
            exchange.getIn().setHeader(ATTACHMENT_TYPE, DOKEOS);
        }else if(firstRow.contains(SUMTOTAL_FIRST_LINE)) {
            exchange.getIn().setHeader(ATTACHMENT_TYPE, SUMTOTAL);
        }else if(firstRow.contains(PARTNER_FIRST_LINE)) {
            exchange.getIn().setHeader(ATTACHMENT_TYPE, PARTNER);
        }else if(firstRow.contains(STUDENT_REG_FIRST_LINE)){
            exchange.getIn().setHeader(ATTACHMENT_TYPE, STUDENT_REG);
        }else if(firstRow.contains(RULES_SPREADSHEET_FIRST_LINE)){
            exchange.getIn().setHeader(ATTACHMENT_TYPE, RULES_SPREADSHEET);
        }else {
            String theReturnEmail = (String) exchange.getIn().getHeader(RETURN_PATH);
            StringBuilder sBuilder  = new StringBuilder();
            sBuilder.append(ExceptionCodes.GPTE_E_1000);
            sBuilder.append("\n\tfirstLine of attachment = "+firstRow);
            sBuilder.append("\n\treturnAddress = "+theReturnEmail);
            sBuilder.append("\n\tdate = "+(String)exchange.getIn().getHeader(DATE));
            sBuilder.append("\n\tsubject = "+(String)exchange.getIn().getHeader(SUBJECT));
            throw new RuntimeException(sBuilder.toString());
        }
        logger.info("determineAttachmentType() attachment type = " + exchange.getIn().getHeader(ATTACHMENT_TYPE));
    }

    // Get email address from the sender. 
    // Then set this as the "To" header for reply email
    // Also tack on admin email to recipientList
    public void setHeaderToWithSendersEmail(Exchange exchange) {
        Message in = exchange.getIn();
        StringBuilder sBuilder = new StringBuilder(adminEmail);
        Object inObj = in.getHeader(RETURN_PATH);
        if(inObj != null){
            if(inObj instanceof List){
                List<String> inList = (List<String>)inObj;
                for(String email : inList){
                    sBuilder.append(DELIMITER);
                    sBuilder.append(email);
                }
                
            }else {
                sBuilder.append(DELIMITER);
                sBuilder.append((String)inObj);
            }
        }
        in.setHeader("to", sBuilder.toString());
    }

    public void clearAttachments(Exchange exchange) {
        exchange.getIn().getAttachments().clear();
    }

    protected String cleanEmailAddress(String email) {
        email = StringUtils.strip(email, "<>");
        return email;
    }
    
    public void removeCommasAndDoubleQuotes(Exchange exchange) {

        StringBuilder result = new StringBuilder();
        
        // get input string
        Message in = exchange.getIn();    
        String body = in.getBody(String.class);
        
        // split into an array
        String textStr[] = body.split("\\r?\\n");
            
        // now skip the numberOfLines ... combine the rest as result
        for (String tempLine : textStr) {

            // skip any header lines
            if (tempLine.startsWith("First Name")) {
                continue;
            }
            
            // tokenize here
            String[] data = tempLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            StringBuilder lineResult = new StringBuilder();
            
            for (int i=0; i < data.length; i++) {
                
                String tempComponent = data[i];

                // strip double quotes
                tempComponent = tempComponent.replace("\"", "");

                // strip embedded commas
                tempComponent = tempComponent.replace(DELIMITER, "");
                
                // append delimiter unless we're at the end
                if (i < (data.length-1)) {
                    lineResult.append(tempComponent + DELIMITER);
                }
                else {
                    lineResult.append(tempComponent);
                }
            }
            result.append(lineResult + "\n");
        }

        // update the body
        in.setBody(result.toString());                
    }
}
