package com.redhat.gpte.services;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

import javax.activation.DataHandler;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/* 
 * TO-DO: Much of this class is generic and could be re-used across all integration projects
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
    private static final String STUDENT_REG_FIRST_LINE = "Name,Email,Company,Region | Subregion,USERID,Region.Partner Tier.Partner Type,SFDC User ID: Partner Company ID";
    private static final String STUDENT_UPDATE_FIRST_LINE = "Email,FirstName,LastName,Company Name,Region,Country,Role";
    private static final String RULES_SPREADSHEET_FIRST_LINE = "Condition    Condition";
    private static final String DOKEOS = "dokeos_cc";
    private static final String SUMTOTAL = "sumtotal_cc";
    private static final String PARTNER = "partner_cc";
    private static final String STUDENT_REG = "student_registration";
    private static final Object STUDENT_UPDATE = "student_update";
    private static final Object RULES_SPREADSHEET = "rules_spreadsheet";
    private static final String ROUTE_SPECIFIC_EMAILS="ROUTE_SPECIFIC_EMAILS";

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
        
        String fromEmail = (String)in.getHeader(RETURN_PATH, String.class);
        fromEmail = cleanEmailAddress(in.getHeader(RETURN_PATH, String.class));
        if(StringUtils.isNotEmpty(fromEmail)) {
            // make sure return email exists, it is from redhat.com and email has csv attachment(s)
            // Do not throw AttachmentValidationException as this would be expensive to handle if originating from DDoS attack

 
            logger.debug("fromEmail = "+fromEmail);
            String fromEmailSuffix = null;
            try {
                fromEmailSuffix = fromEmail.substring(fromEmail.indexOf("@")+1, fromEmail.indexOf(".com")+4);
                fromEmailSuffix = fromEmailSuffix.replace(">", "");
                if (!validEmailSuffixes.contains(fromEmailSuffix))  {
                    logger.error("isValidCamelMessage() email address is invalid: "+fromEmailSuffix);
                    return false;
                }
            } catch(Exception x) {
                logger.error("isValidCamelMessage() Exception thrown attempting to process from email: "+fromEmail+" : "+x.getMessage());
                return false;
            }
        } else {
            logger.error("isValidCamelMessage() email address path header not found");
            return false;
        }
        
        // Ensure email has attachments
        Map<String, DataHandler> attachments = exchange.getIn().getAttachments();
        if(attachments.size() == 0) {
            // loggering error only
            // Not throwing exception because too expensive 
            // Our gmail account could simply find itself involved in an email thread  between Red Hat associates
            logger.error(AttachmentValidationException.NO_ATTACHMENTS_PROVIDED+" Input provided by "+fromEmail);
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
    public void moveAttachmentsToBodyAndSendToGPTEProcessingRoute(Exchange exchange) throws Exception {
        Map<String, DataHandler> attachments = exchange.getIn().getAttachments();
        logger.info("moveAttachmentsToBodyAndSendToGPTEProcessingRoute() received the following # of attachments: "+attachments.size());
        for(Entry<String, DataHandler> attachEntry : attachments.entrySet()){
            InputStream iStream = null;
            Producer producer = null;
            try {
                iStream = attachEntry.getValue().getInputStream();
                String attachString = IOUtils.toString(iStream);

                CamelContext cContext = exchange.getContext();
                Endpoint endpoint = cContext.getEndpoint("direct:process-gpte-operation-files");
                producer = endpoint.createProducer();
                producer.start();

                Exchange newExchange = producer.createExchange();
                newExchange.setPattern(ExchangePattern.InOnly);
                Message in = newExchange.getIn();
                in.setBody(attachString);
                in.setHeader(CAMEL_FILE_NAME, attachEntry.getKey());
                in.setHeader(RETURN_PATH, exchange.getIn().getHeader(RETURN_PATH));
                in.setHeader(SUBJECT, exchange.getIn().getHeader(SUBJECT));

                producer.process(newExchange);

            }finally {
                if(producer != null)
                    producer.stop();
                if(iStream != null)
                    iStream.close();
            }
        }
    }

    public void determineAttachmentType(Exchange exchange) {
        String body =  (String)exchange.getIn().getBody();
        if(body.startsWith(DOKEOS_FIRST_LINE)) {
            exchange.getIn().setHeader(ATTACHMENT_TYPE, DOKEOS);
        }else if(body.startsWith(DOKEOS_ASSIGNMENT_FIRST_LINE)) {
            exchange.getIn().setHeader(ATTACHMENT_TYPE, DOKEOS);
        }else if(body.startsWith(SUMTOTAL_FIRST_LINE)) {
            exchange.getIn().setHeader(ATTACHMENT_TYPE, SUMTOTAL);
        }else if(body.startsWith(PARTNER_FIRST_LINE)) {
            exchange.getIn().setHeader(ATTACHMENT_TYPE, PARTNER);
        }else if(body.startsWith(STUDENT_REG_FIRST_LINE)){
            exchange.getIn().setHeader(ATTACHMENT_TYPE, STUDENT_REG);
        }else if(body.startsWith(STUDENT_UPDATE_FIRST_LINE)){
            int headerLength = STUDENT_UPDATE_FIRST_LINE.length();
            body = body.substring(headerLength + 1); // includes first line break
            exchange.getIn().setHeader(ATTACHMENT_TYPE, STUDENT_UPDATE);
            exchange.getIn().setBody(body);
        }else if(body.startsWith(RULES_SPREADSHEET_FIRST_LINE)){
            exchange.getIn().setHeader(ATTACHMENT_TYPE, RULES_SPREADSHEET);
        }else {
            String theReturnEmail = null;
            Object inObj = exchange.getIn().getHeader(RETURN_PATH);
            if(inObj != null) {
                if(inObj instanceof List) {
                    List<String> inList = (List<String>)inObj;
                    int x=0;
                    for(String email : inList){
                        if(x > 0) {
                            theReturnEmail = theReturnEmail + DELIMITER;
                        }
                        theReturnEmail = theReturnEmail+email;
                    }
                }else {
                    theReturnEmail = (String)inObj;
                }
            }
            StringBuilder sBuilder  = new StringBuilder();
            sBuilder.append(ExceptionCodes.GPTE_E_1000);
            sBuilder.append("\n\tfirstLine of attachment = "+body);
            sBuilder.append("\n\treturnAddress = "+theReturnEmail);
            sBuilder.append("\n\tdate = "+(String)exchange.getIn().getHeader(DATE));
            sBuilder.append("\n\tsubject = "+(String)exchange.getIn().getHeader(SUBJECT));
            throw new RuntimeException(sBuilder.toString());
        }
        logger.info("determineAttachmentType() attachment type = " + exchange.getIn().getHeader(ATTACHMENT_TYPE));
    }

    public void setHeaderToWithProperEmails(Exchange exchange) {
        Message in = exchange.getIn();
        Set<String> uniqueEmails = new HashSet<String>();

        // 1)  Always send email to admin
        String[] adminEmails = StringUtils.split(adminEmail, DELIMITER);
        for(String aEmail : adminEmails) {
            uniqueEmails.add(aEmail);
        }

        // 2)  Send response back to originator, if exists
        Object inObj = in.getHeader(RETURN_PATH);
        if(inObj != null){
            if(inObj instanceof List){
                List<String> inList = (List<String>)inObj;
                for(String email : inList){
                    if(email.startsWith(REDHAT)) {
                        uniqueEmails.add(email);
                    }
                }
                
            }else {
                if(((String)inObj).startsWith(REDHAT)) {
                    uniqueEmails.add((String)inObj);
                }
            }
        }

        // 3)  Send response email to ROUTE_SPECIFIC_EMAILS, if set
        String routeSpecificEmails = (String)in.getHeader(ROUTE_SPECIFIC_EMAILS);
        if(StringUtils.isNotEmpty(routeSpecificEmails)) {
            String[] routeEmails = StringUtils.split(routeSpecificEmails, DELIMITER);
            for(String email : routeEmails) {
                uniqueEmails.add(routeSpecificEmails);
            }
        }
        Iterator uIterator = uniqueEmails.iterator();
        int x = 0;
        StringBuilder sBuilder  = new StringBuilder();
        while(uIterator.hasNext()) {
            if(x != 0) {
                sBuilder.append(DELIMITER);
            }
            sBuilder.append(uIterator.next());
            x++;
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

    public void changeFileBodyToMapBody(Exchange exchange) throws Exception {
        Map<String,String> attachmentMap = new HashMap<String,String>();
        org.apache.camel.component.file.GenericFile gFile = (org.apache.camel.component.file.GenericFile)exchange.getIn().getBody();
        File fileBody = (File)gFile.getBody();
        String stringBody = FileUtils.readFileToString(fileBody);

        attachmentMap.put(gFile.getFileName(), stringBody);
        exchange.getIn().setBody(attachmentMap);
    }
    
}
