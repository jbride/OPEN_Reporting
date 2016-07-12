package com.redhat.gpe.integration.test;

import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentAccreditation;
import com.redhat.gpe.domain.helper.Accreditation;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;


public class CheckExpiredAccredsTest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckExpiredAccredsTest.class);

    private static final String CHECK_EXPIRED_ACCREDS_URI = "accred_check_expired_accreds_uri";

    public CheckExpiredAccredsTest() throws IOException {
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
    public void testExpiredAccredsTest() throws InterruptedException {
        String routeURI = System.getProperty(CHECK_EXPIRED_ACCREDS_URI);
        if(routeURI == null || routeURI.equals(""))
            throw new RuntimeException("Need to set system property: "+CHECK_EXPIRED_ACCREDS_URI);
        
        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new Object());
    }
}
