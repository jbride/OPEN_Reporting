package com.redhat.gpe.integration.test;


import com.redhat.gpe.accreditation.util.SpreadsheetRule;
import com.redhat.gpte.util.PropertiesSupport;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateRulesFileTest extends CamelSpringTestSupport {

    private static final String CREATE_DRL_FROM_RULES_SPREADSHEET_URI = "direct:create-drl-from-rules-spreadsheet";
    private static final String HEADER_NAME_FILE = "CamelFileName";

    public CreateRulesFileTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    @Before
    public void init() {}
    
    @Ignore
    @Test
    public void testCreate1CourseDRLFile() throws IOException {
        String TEST_RULE_FILE_NAME = "TEST_1Course_CI_ACCREDITATION_RULES.drl";
        Endpoint endpoint = context.getEndpoint(CREATE_DRL_FROM_RULES_SPREADSHEET_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();
        in.setHeader(HEADER_NAME_FILE, TEST_RULE_FILE_NAME);

        List<SpreadsheetRule> sRules = new ArrayList<SpreadsheetRule>();
        addRule(sRules, 1);
        in.setBody(sRules);
        template.send(CREATE_DRL_FROM_RULES_SPREADSHEET_URI, exchange);
    }

    @Ignore
    @Test
    public void testCreate2CourseDRLFile() throws IOException {

        String TEST_RULE_FILE_NAME = "TEST_2Course_CI_ACCREDITATION_RULES.drl";
        Endpoint endpoint = context.getEndpoint(CREATE_DRL_FROM_RULES_SPREADSHEET_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();
        in.setHeader(HEADER_NAME_FILE, TEST_RULE_FILE_NAME);

        List<SpreadsheetRule> sRules = new ArrayList<SpreadsheetRule>();
        addRule(sRules, 2);
        in.setBody(sRules);
        template.send(CREATE_DRL_FROM_RULES_SPREADSHEET_URI, exchange);
    }
    
    @Ignore
    @Test
    public void testCreate3CourseDRLFile() throws IOException {

        String TEST_RULE_FILE_NAME = "TEST_3Course_CI_ACCREDITATION_RULES.drl";
        Endpoint endpoint = context.getEndpoint(CREATE_DRL_FROM_RULES_SPREADSHEET_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();
        in.setHeader(HEADER_NAME_FILE, TEST_RULE_FILE_NAME);

        List<SpreadsheetRule> sRules = new ArrayList<SpreadsheetRule>();
        addRule(sRules, 3);
        in.setBody(sRules);
        template.send(CREATE_DRL_FROM_RULES_SPREADSHEET_URI, exchange);
    }
    
    @Ignore
    @Test
    public void testCreate4CourseDRLFile() throws IOException {

        String TEST_RULE_FILE_NAME = "TEST_4Course_CI_ACCREDITATION_RULES.drl";
        Endpoint endpoint = context.getEndpoint(CREATE_DRL_FROM_RULES_SPREADSHEET_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();
        in.setHeader(HEADER_NAME_FILE, TEST_RULE_FILE_NAME);

        List<SpreadsheetRule> sRules = new ArrayList<SpreadsheetRule>();
        addRule(sRules, 4);
        in.setBody(sRules);
        template.send(CREATE_DRL_FROM_RULES_SPREADSHEET_URI, exchange);
    }
    
    
    
    private void addRule(List<SpreadsheetRule> sRules, int numOfRules){
        SpreadsheetRule rule = new SpreadsheetRule();
        rule.setAccredName(numOfRules+"Red Hat Delivery Specialist - Cloud Management");
        rule.setBeginDate("01-Feb-2014");
        for(int x=1; x<=numOfRules; x++ ){
            if(x == 1)
                rule.setCourse1(x+"CloudForms FASTRAX");
            else if (x == 2)
                rule.setCourse2(x+"CloudForms FASTRAX");
            else if (x == 3)
                rule.setCourse3(x+"CloudForms FASTRAX");
            else if (x == 4)
                rule.setCourse4(x+"CloudForms FASTRAX");
            else if (x == 5)
                rule.setCourse5(x+"CloudForms FASTRAX");
            else if (x == 6)
                rule.setCourse6(x+"CloudForms FASTRAX");
            else if (x == 7)
                rule.setCourse7(x+"CloudForms FASTRAX");            
        }
        sRules.add(rule);
        
    }
}
