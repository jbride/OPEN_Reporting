package com.redhat.gpe.integration.test;

import com.redhat.gpe.domain.canonical.*;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpe.domain.helper.DomainMockObjectHelper;
import com.redhat.gpte.util.PropertiesSupport;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class TotaraCourseCompletionsTest extends CamelSpringTestSupport {
    
    private static final String POLL_NEW_TOTARA_COURSE_COMPLETIONS_URI = "direct:poll_totara_course_completions";
    private static final String PROCESS_TOTARA_COURSE_COMPLETIONS_BY_RANGE_URI = "direct:process-totara-course-completions-by-range";
    private static final String TEST_TOTARA_JDBC_CONNECTION_URI = "direct:test-totara-jdbc-connection";
    private static final String TOTARA_COURSE_COMPLETION_LIMIT = "TOTARA_COURSE_COMPLETION_LIMIT";
    private static final String TOTARA_LOW_CC_ID="LOW_CC_ID";
    private static final String TOTARA_HIGH_CC_ID="HIGH_CC_ID";

    public TotaraCourseCompletionsTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/course-completion-camel-context.xml");
    }

    @Before
    public void init() {
    }

    @Ignore
    @Test
    public void testTotaraJdbcConnection() throws InterruptedException {
       Endpoint endpoint = context.getEndpoint(TEST_TOTARA_JDBC_CONNECTION_URI);
       Exchange exchange = endpoint.createExchange();
       exchange.setPattern(ExchangePattern.InOnly);
       Message in = exchange.getIn();

       in.setBody(new Object());
       template.send(TEST_TOTARA_JDBC_CONNECTION_URI, exchange);
    }

    @Ignore
    @Test
    public void testProcessTotaraCourseCompletionsByRangeTest() throws InterruptedException {
       Endpoint endpoint = context.getEndpoint(PROCESS_TOTARA_COURSE_COMPLETIONS_BY_RANGE_URI);
       Exchange exchange = endpoint.createExchange();
       exchange.setPattern(ExchangePattern.InOnly);
       Message in = exchange.getIn();
       in.setHeader(TOTARA_LOW_CC_ID, 1493371045);
       in.setHeader(TOTARA_HIGH_CC_ID, 1493518306);

       in.setBody(new Object());
       template.send(PROCESS_TOTARA_COURSE_COMPLETIONS_BY_RANGE_URI, exchange);
    }
    
    @Ignore
    @Test
    public void testPollTotaraCourseCompletionsTest() throws InterruptedException {
       Endpoint endpoint = context.getEndpoint(POLL_NEW_TOTARA_COURSE_COMPLETIONS_URI);
       Exchange exchange = endpoint.createExchange();
       exchange.setPattern(ExchangePattern.InOnly);
       Message in = exchange.getIn();
       in.setHeader(TOTARA_COURSE_COMPLETION_LIMIT, "1");

       in.setBody(new Object());
       template.send(POLL_NEW_TOTARA_COURSE_COMPLETIONS_URI, exchange);
       
    }
}
