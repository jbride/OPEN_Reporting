package com.redhat.gpe.integration.test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

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

import org.json.JSONArray;
import org.json.JSONObject;

import com.redhat.gpte.util.PropertiesSupport;

import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.StudentAccreditation;
import com.redhat.gpe.domain.helper.DomainMockObjectHelper;
import com.redhat.gpe.domain.canonical.Student;

public class PushQualToSkillsBaseTest extends CamelSpringTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(PushQualToSkillsBaseTest.class);
    private String pushQualToSkillsBaseURI = "direct:push-qual-to-skills-base";
    private String pushQualsToSkillsBaseBatchURI = "direct:push-qual-to-skillsbase-uri";

    private String personQualifications = "{\"status\":\"success\",\"data\":[{\"name\":\"Bachelor of Science in Material Science Engineering\",\"person_id\":295,\"status\":\"completed\",\"start_date\":\"Sep-03-1990\",\"end_date\":null},{\"name\":\"Red Hat Advanced Delivery Specialist - Cloud Management\",\"person_id\":295,\"status\":\"completed\",\"start_date\":\"Apr-12-2017\",\"end_date\":\"Apr-12-2019\"},{\"name\":\"Red Hat Advanced Delivery Specialist - Cloud Management\",\"person_id\":295,\"status\":\"completed\",\"start_date\":\"Apr-12-2017\",\"end_date\":\"Apr-12-2019\"},{\"name\":\"Red Hat Advanced Delivery Specialist - Cloud Management\",\"person_id\":295,\"status\":\"completed\",\"start_date\":\"Apr-12-2017\",\"end_date\":\"Apr-12-2019\"},{\"name\":\"Red Hat Advanced Delivery Specialist - Cloud Management\",\"person_id\":295,\"status\":\"completed\",\"start_date\":\"Apr-12-2017\",\"end_date\":\"Apr-12-2019\"},{\"name\":\"Red Hat Advanced Delivery Specialist - Cloud Management\",\"person_id\":295,\"status\":\"completed\",\"start_date\":\"Apr-12-2017\",\"end_date\":\"Apr-12-2019\"},{\"name\":\"Red Hat Advanced Delivery Specialist - Cloud Management\",\"person_id\":295,\"status\":\"completed\",\"start_date\":\"Apr-12-2017\",\"end_date\":\"Apr-12-2019\"}]}";
    
    public PushQualToSkillsBaseTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {

    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    @Ignore
    @Test
    public void testPersonQualificationsParsing() throws org.json.JSONException {
        JSONObject jsonResponse = new JSONObject(personQualifications);
        JSONArray jArray = jsonResponse.optJSONArray("data");
        for(int i=0; i < jArray.length(); i++) {
            JSONObject jObj = jArray.getJSONObject(i);
            String END_DATE = "end_date";
            String NAME = "name";
            String STATUS = "status";
            String START_DATE = "start_date";

            String existingQual = jObj.getString(NAME);
            System.out.println("testPersonQualificationsParsing() existingQual = "+existingQual+"\n\tobj = "+jObj.toString());
        }
    }

    @Ignore
    @Test
    public void testQueryForUnpushedQuals() throws InterruptedException {
        template.setDefaultEndpointUri(pushQualsToSkillsBaseBatchURI);
        Endpoint endpoint = context.getEndpoint(pushQualsToSkillsBaseBatchURI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();
        in.setHeader("DETERMINE_UNPROCESSED_ACCREDS_ONLY", true);
        template.send(pushQualsToSkillsBaseBatchURI, exchange);
    }

    //@Ignore
    @Test
    public void testPushQualToSkillsBaseGivenKnownRHTStudent() throws InterruptedException {

        Course course2 = new Course();
        course2.setCoursename(DomainMockObjectHelper.CLOUDFORMS_IMPLEMENTATION);

        Student student = DomainMockObjectHelper.getMockRHTStudent();

        Accreditation accreditation = new Accreditation();
        accreditation.setCourse(course2);

        AccreditationDefinition accreditationDefinition = new AccreditationDefinition();
        accreditationDefinition.setAccreditationid(1);
        accreditationDefinition.setAccreditationname("Red Hat Advanced Delivery Specialist - Cloud Management");

        accreditation.setAccreditation(accreditationDefinition);

        StudentAccreditation studentAccreditation = new StudentAccreditation();
        studentAccreditation.setStudentid(student.getStudentid());
        studentAccreditation.setAccreditationtype(StudentAccreditation.Types.Active.name());
        studentAccreditation.setAccreditationid(1);
        studentAccreditation.setAccreditationdate(new Timestamp(new Date().getTime()));

        accreditation.setStudentAccred(studentAccreditation);
        accreditation.setStudent(student);

        template.setDefaultEndpointUri(pushQualToSkillsBaseURI);
        template.sendBody(accreditation);
    }
    
}
