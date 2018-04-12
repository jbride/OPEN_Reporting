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

import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.helper.DomainMockObjectHelper;
import com.redhat.gpte.util.PropertiesSupport;

public class StudentTest extends CamelSpringTestSupport {

    private static final String PERSIST_STUDENT_URI = "direct:persist-student";

    public StudentTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/student-registration-camel-context.xml");
    }

    @Before
    public void init() {
    }
    
    @Ignore
    @Test
    public void testPersistPartnerStudent() throws InterruptedException {
        Student studentObj = DomainMockObjectHelper.getMockStudent();
        template.setDefaultEndpointUri(PERSIST_STUDENT_URI);
        template.sendBody(studentObj);
    }
    
    //@Ignore
    @Test
    public void testPersistRHTStudent() throws InterruptedException {
        
        Student studentObj = DomainMockObjectHelper.getMockRHTStudent();
        template.setDefaultEndpointUri(PERSIST_STUDENT_URI);
        template.sendBody(studentObj);
    }

}
