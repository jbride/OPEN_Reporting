package com.redhat.gpe.integration.test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

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

import com.redhat.gpte.util.PropertiesSupport;

import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.StudentAccreditation;
import com.redhat.gpe.domain.canonical.Student;

public class PushAccredToSfdcTest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(PushAccredToSfdcTest.class);
    private String pushAccredToSfdcURI = "direct:push-accred-to-sfdc";
    private String pushAccredToSfdcBatchURI = "direct:push-accred-to-sfdc-uri";

    public PushAccredToSfdcTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {

    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    //@Ignore
    @Test
    public void testForUnpushedSFDCAccred() throws InterruptedException {
        template.setDefaultEndpointUri(pushAccredToSfdcBatchURI);
        Endpoint endpoint = context.getEndpoint(pushAccredToSfdcBatchURI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();
        in.setHeader("DETERMINE_UNSFDCPUSH_ACCREDS_ONLY", true);
        template.send(pushAccredToSfdcBatchURI, exchange);
    }

    //@Ignore
    @Test
    public void testPushAccredToSfdcGivenKnownStudent() throws InterruptedException {

        Course course2 = new Course();
        course2.setCourseid("DCI-S-PLT-SAL-SAT");
        course2.setCoursename("RHN Satellite Sales");

        Student student = new Student();
        student.setStudentid(10251);
        student.setSalesforcefederationid("45");
        student.setEmail("adam.lister@e-business.com");

        Accreditation accreditation = new Accreditation();
        accreditation.setCourse(course2);

        AccreditationDefinition accreditationDefinition = new AccreditationDefinition();
        accreditationDefinition.setAccreditationid(45);
        accreditationDefinition.setAccreditationname("Red Hat Sales Specialist - Platform_with_2_courses");

        accreditation.setAccreditation(accreditationDefinition);

        StudentAccreditation studentAccreditation = new StudentAccreditation();
        studentAccreditation.setStudentid(10251);
        studentAccreditation.setAccreditationtype(StudentAccreditation.Types.Active.name());
        studentAccreditation.setAccreditationid(23);
        studentAccreditation.setAccreditationdate(new Timestamp(new Date().getTime()));

        accreditation.setStudentAccred(studentAccreditation);
        accreditation.setStudent(student);
        logger.info("====== JUnit - Pushing data to sfdc ======");
        template.setDefaultEndpointUri(pushAccredToSfdcURI);
        template.sendBody(accreditation);
    }
    
}
