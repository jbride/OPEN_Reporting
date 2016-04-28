package com.redhat.gpe.integration.test;


import com.redhat.gpte.util.PropertiesSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RulesSpreadsheetValidationTest extends CamelSpringTestSupport {
    
    private static final String VALIDATE_RULES_SPREADSHEET_URI = "direct:validate-rules-spreadsheet";
    public static final String INBOX_PATH = "target/test-classes/rules-spreadsheet";
    private static final String RULES_SPREADSHEET_NAME = "accred_rules_spreadsheet_name";
    private String spreadsheetName = null;
    
    public RulesSpreadsheetValidationTest() throws IOException {
        PropertiesSupport.setupProps();
        spreadsheetName = System.getProperty(RULES_SPREADSHEET_NAME);
        if(spreadsheetName == null)
            throw new RuntimeException("Must system property with the following name: "+RULES_SPREADSHEET_NAME);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    @Before
    public void init() {}
    
    @Ignore
    @Test
    public void testValidateRulesSpreadsheet() throws InterruptedException, IOException {
        File csvFile = new File(INBOX_PATH, spreadsheetName);
        if(!csvFile.exists())
            throw new RuntimeException("restValidationRulesSpreadsheet() no file found at: "+csvFile.getAbsolutePath());
        
        Endpoint endpoint = context.getEndpoint(VALIDATE_RULES_SPREADSHEET_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();

        in.setHeader("CamelFileName", spreadsheetName);
        FileInputStream fStream = new FileInputStream(csvFile);
        String csvString = IOUtils.toString(fStream);
        fStream.close();
        in.setBody(csvString);
        template.send(VALIDATE_RULES_SPREADSHEET_URI, exchange);
    }
}
