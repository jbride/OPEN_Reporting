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

public class ReportOnCoursesTest extends CamelSpringTestSupport {
    
    private static final String REPORT_ON_COURSES_URI = "direct:report-on-courses";
    
    public ReportOnCoursesTest() throws IOException {
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
    public void testReportOnCoursesTest() throws InterruptedException, IOException {
        Endpoint endpoint = context.getEndpoint(REPORT_ON_COURSES_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();

        in.setBody(new Object());
        template.send(REPORT_ON_COURSES_URI, exchange);
    }
}
