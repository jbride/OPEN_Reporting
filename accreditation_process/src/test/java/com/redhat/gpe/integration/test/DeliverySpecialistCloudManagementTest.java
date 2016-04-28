package com.redhat.gpe.integration.test;

import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Language;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentAccreditation;
import com.redhat.gpe.domain.canonical.StudentCourse;
import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpe.domain.helper.DomainMockObjectHelper;
import com.redhat.gpte.util.DroolsCommandHelper;
import com.redhat.gpte.util.PropertiesSupport;

import org.apache.camel.Endpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by samueltauil on 2/23/16.
 */
public class DeliverySpecialistCloudManagementTest extends CamelSpringTestSupport {

    private static final String DROOLS_CAMEL_ROUTE_URI = "direct:execute-accreditation_rules-uri";

    private static final Logger logger = LoggerFactory.getLogger(DeliverySpecialistCloudManagementTest.class);
    private Endpoint endpoint = null;

    public DeliverySpecialistCloudManagementTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {
        endpoint = context.getEndpoint(DROOLS_CAMEL_ROUTE_URI);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    @Ignore
    @Test
    public void testAccreditationDeliverySpecialistCloudManagement() throws InterruptedException {
        template.setDefaultEndpointUri(DROOLS_CAMEL_ROUTE_URI);

        Student student = DomainMockObjectHelper.getMockStudent();
        
        Language language = DomainMockObjectHelper.getMockLanguage();
        
        StudentCourse sCourse1 = new StudentCourse();
        sCourse1.setAssessmentdate(new Timestamp(new Date().getTime()));
        sCourse1.setAssessmentresult(StudentCourse.ResultTypes.Pass.name());
        sCourse1.setAssessmentscore((byte) 85);
        
        StudentCourse sCourse2 = new StudentCourse();
        sCourse2.setAssessmentdate(new Timestamp(new Date().getTime() - 100000000)); // Make obvious that sCourse2 is not latest course completion
        sCourse2.setAssessmentresult(StudentCourse.ResultTypes.Pass.name());
        sCourse2.setAssessmentscore((byte) 80);

        Course course1 = new Course();
        course1.setCoursename(DomainMockObjectHelper.CLOUDFORMS_FASTRAX);
        Course course2 = new Course();
        course2.setCoursename(DomainMockObjectHelper.CLOUDFORMS_IMPLEMENTATION);

        CourseCompletion cc1 = new CourseCompletion(student, course1, language, sCourse1);
        CourseCompletion cc2 = new CourseCompletion(student, course2, language, sCourse2);
        cc2.setMostRecentCourseCompletion(true);

        List<CourseCompletion> facts = new ArrayList<CourseCompletion>();

        facts.add(cc1);
        facts.add(cc2);
        template.requestBody(facts);

        StatefulKnowledgeSession kieSession = (StatefulKnowledgeSession) applicationContext.getBean(DroolsCommandHelper.KSESSION_NAME);

        ArrayList accreditationList = (ArrayList) kieSession.getGlobal(DroolsCommandHelper.ACCREDITATION_LIST_NAME);

        Accreditation accreditation = new Accreditation();

        accreditation.setCourse(course2);//because it's not processed

        AccreditationDefinition accreditationDefinition = new AccreditationDefinition();
        accreditationDefinition.setAccreditationid(1);
        accreditationDefinition.setAccreditationname("Red Hat Advanced Delivery Specialist - Cloud Management");

        accreditation.setAccreditation(accreditationDefinition);

        StudentAccreditation studentAccreditation = new StudentAccreditation();
        studentAccreditation.setStudentid(student.getStudentid());
        studentAccreditation.setAccreditationtype(StudentAccreditation.Types.Active.name());
        studentAccreditation.setAccreditationid(1);

        accreditation.setStudentAccred(studentAccreditation);

        for (Object each : accreditationList) {
            Accreditation accreditationReturned = (Accreditation) each;

            assertEquals(studentAccreditation.getStudentid(), accreditationReturned.getStudentId());
            assertEquals(accreditation.getAccreditation().getAccreditationid(), accreditationReturned.getAccreditationId());
            assertEquals(studentAccreditation.getAccreditationtype(), accreditationReturned.getAccreditationType());
            assertEquals(accreditation.getCourse().getCoursename(),accreditationReturned.getCourse().getCoursename());
        }

    }
}
