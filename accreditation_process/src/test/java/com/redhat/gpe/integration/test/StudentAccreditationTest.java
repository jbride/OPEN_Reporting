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

import com.redhat.gpe.domain.helper.DomainMockObjectHelper;


public class StudentAccreditationTest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentAccreditationTest.class);

    private static final String DETERMINE_ACCREDITATION_FOR_STUDENT_URI = "direct:determine-accred-for-student";
    private static final String DETERMINE_ACCREDITATION_FOR_RANGE_URI = "direct:determine-accreds-for-range";
    private static final String PERSIST_NEW_ACCREDITATION_URI = "direct:persist-new-accreditation";
    private static final String PROCESS_NEW_STUDENT_ACCREDS_URI = "accred_process-push-qualification-to-skillsbase-batch";
    private static final String SET_ACCRED_ID_ON_ACCRED_OBJ_URI = "direct:set-accredId-on-accredObj";
    private static final String IDENTIFY_FIRED_RULES_ONLY_HEADER = "IDENTIFY_FIRED_RULES_ONLY";
    private static final String RESPOND_JSON_HEADER = "RESPOND_JSON";
    private static final String LOW_STUDENT_ID = "LOW_STUDENT_ID";
    private static final String HIGH_STUDENT_ID = "HIGH_STUDENT_ID";
    
    public StudentAccreditationTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    @Before
    public void init() {}


    //@Ignore
    @Test
    public void testDetermineAccreditationForStudent() throws InterruptedException {

        // admin@slamsys.io ; studentId = 10301
        // agomez@criticalperu.com ; student = 10387
        int studentId = 73173;
        
        //int studentId = 10386; // course completions but no accreditations
        //int studentId = 20387; // bogus student

        Endpoint endpoint = context.getEndpoint(DETERMINE_ACCREDITATION_FOR_STUDENT_URI);
        Exchange exchange = endpoint.createExchange();
        Message in = exchange.getIn();
        in.setBody(DomainMockObjectHelper.partnerStudentId);
        in.setHeader(IDENTIFY_FIRED_RULES_ONLY_HEADER, "false");
        in.setHeader(RESPOND_JSON_HEADER, "true");
        Exchange outE = template.send(DETERMINE_ACCREDITATION_FOR_STUDENT_URI, exchange);
        System.out.println("testDeterminAccreditationForStudent() response = "+outE.getIn().getBody());
        
    }

    @Ignore
    @Test
    public void testDetermineAccreditationForStudentRange() throws InterruptedException {
        Endpoint endpoint = context.getEndpoint(DETERMINE_ACCREDITATION_FOR_RANGE_URI);
        Exchange exchange = endpoint.createExchange();
        Message in = exchange.getIn();
        in.setHeader(IDENTIFY_FIRED_RULES_ONLY_HEADER, "true");
        in.setHeader(RESPOND_JSON_HEADER, "true");
        in.setHeader(LOW_STUDENT_ID, "10000");
        in.setHeader(HIGH_STUDENT_ID, "10100");
        Exchange outE = template.send(DETERMINE_ACCREDITATION_FOR_RANGE_URI, exchange);
        System.out.println("testDeterminAccreditationForStudentRange() response = "+outE.getIn().getBody());
        
    }


    /* verification:   select * from StudentAccreditations where accreditationid = 19 and StudentId = 10145;
       cleanup:        delete from StudentAccreditations where accreditationid = 19 and StudentId = 10145;
    */
    @Ignore
    @Test
    public void testPersistNewStudentAccreditation() throws InterruptedException {
        Endpoint endpoint = context.getEndpoint(PERSIST_NEW_ACCREDITATION_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();
        
        AccreditationDefinition accred = DomainMockObjectHelper.getMockAccreditation();
        Student studentObj = DomainMockObjectHelper.getMockStudent();
        Course courseObj = DomainMockObjectHelper.getMockCourse();
        StudentAccreditation sAccredObj = DomainMockObjectHelper.getMockStudentAccreditation();
        
        Accreditation accredWrapper = new Accreditation();
        accredWrapper.setAccreditation(accred);
        accredWrapper.setStudent(studentObj);
        accredWrapper.setCourse(courseObj);
        accredWrapper.setStudentAccred(sAccredObj);
        in.setBody(accredWrapper);
        template.send(PERSIST_NEW_ACCREDITATION_URI, exchange);
    }
    
    @Ignore
    @Test
    public void testSetAccreditationIdOnAccreditationObj() throws InterruptedException {
        Endpoint endpoint = context.getEndpoint(SET_ACCRED_ID_ON_ACCRED_OBJ_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();
        
        AccreditationDefinition accred = DomainMockObjectHelper.getMockAccreditation();
        Student studentObj = DomainMockObjectHelper.getMockStudent();
        Course courseObj = DomainMockObjectHelper.getMockCourse();
        StudentAccreditation sAccredObj = DomainMockObjectHelper.getMockStudentAccreditation();
        
        Accreditation accredWrapper = new Accreditation();
        accredWrapper.setAccreditation(accred);
        accredWrapper.setStudent(studentObj);
        accredWrapper.setCourse(courseObj);
        accredWrapper.setStudentAccred(sAccredObj);
        accredWrapper.setAccreditationId(0);
        in.setBody(accredWrapper);
        template.send(SET_ACCRED_ID_ON_ACCRED_OBJ_URI, exchange);
    }
    
    @Ignore
    @Test
    public void testProcessStudentAccreditations() throws InterruptedException {
        String routeURI = System.getProperty(PROCESS_NEW_STUDENT_ACCREDS_URI);
        if(routeURI == null || routeURI.equals(""))
            throw new RuntimeException("Need to set system property: "+PROCESS_NEW_STUDENT_ACCREDS_URI);
        
        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new Object());
    }
}
