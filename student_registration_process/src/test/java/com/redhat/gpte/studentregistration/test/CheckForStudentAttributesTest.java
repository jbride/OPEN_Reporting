package com.redhat.gpte.studentregistration.test;

import java.io.IOException;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpte.util.PropertiesSupport;

// Purpose:  test processing of valid CSV input file
public class CheckForStudentAttributesTest extends CamelSpringTestSupport {
    
    public static final String CHECK_RHT_STUDENT_ATTRIBUTES_BATCH_URI = "sr_check-for-rht-student_attributes_batch_uri";
    public static final String GET_STUDENT_ATTRIBUTES_FROM_IPA_URI = "vm:get-student-attributes-from-ipa";
    private static final Logger logger = LoggerFactory.getLogger(CheckForStudentAttributesTest.class);
    
    public CheckForStudentAttributesTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/student-registration-camel-context.xml");
    }

    @Ignore
    @Test
    public void testCheckForRHTStudentAttributesBatchTest() throws InterruptedException {
        String routeURI = System.getProperty(CHECK_RHT_STUDENT_ATTRIBUTES_BATCH_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+CHECK_RHT_STUDENT_ATTRIBUTES_BATCH_URI);
        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new Object());
    }

    @Ignore
    @Test
    public void testGetStudentAttributesFromIPATest() throws InterruptedException {
        String email = "junxian.xu@yun-idc.com";
        Student studentIn = new Student();
        studentIn.setEmail(email);

        Endpoint endpoint = context.getEndpoint(GET_STUDENT_ATTRIBUTES_FROM_IPA_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOut);
        Message in = exchange.getIn();
        in.setBody(studentIn);
        exchange = template.send(GET_STUDENT_ATTRIBUTES_FROM_IPA_URI, exchange);
        Student studentOut = (Student)exchange.getIn().getBody();
        System.out.println("testGetStudentAttributesFromIPATest() email = "+email+" ; companyName = "+studentOut.getCompanyName());

    }
}
