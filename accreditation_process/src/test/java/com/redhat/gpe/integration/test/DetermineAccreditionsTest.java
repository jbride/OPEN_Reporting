package com.redhat.gpe.integration.test;

import com.redhat.gpe.domain.canonical.*;
import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.helper.DomainMockObjectHelper;
import com.redhat.gpte.util.DroolsCommandHelper;
import com.redhat.gpte.util.PropertiesSupport;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;

public class DetermineAccreditionsTest extends CamelSpringTestSupport {
    
    private static final String PROCESS_NEW_STUDENT_COURSES_URI = "accred_determine-accreditations-uri";
    private static final String UPDATE_STUDENT_COURSE_URI = "direct:set-processed-on-student-course-by-student";

    public DetermineAccreditionsTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    @Before
    public void init() {
    }
   

    /* verification:    INSERT INTO `lms_transactional`.`StudentCourses` (`StudentCourseID`,`StudentID`,`CourseID`,`LanguageID`,`AssessmentDate`,`AssessmentResult`,`AssessmentScore`,`CourseName`,`Processed`)
                        VALUES (null,10000,'CLI-TECH-CMT-EXAM-CF3.1FST','EN_US',now(),'Pass','1.00000','CloudForms FASTRAX',0);

                        INSERT INTO `lms_transactional`.`StudentCourses` (`StudentCourseID`,`StudentID`,`CourseID`,`LanguageID`,`AssessmentDate`,`AssessmentResult`,`AssessmentScore`,`CourseName`,`Processed`)
                        VALUES (null,10000,'CLI-DEL-CMT-EXAM-CF3.1IMP','EN_US',now(),'Pass','1.00000','CloudForms Implementation',0);

       cleanup:        delete from StudentCourses where StudentID = 10000 and CourseID = 'CLI-TECH-CMT-EXAM-CF3.1FST';
                       delete from StudentCourses where StudentID = 10000 and CourseID = 'CLI-DEL-CMT-EXAM-CF3.1IMP';
     */
    @Ignore
    @Test
    public void testProcessStudentCourses() throws InterruptedException {

        String routeURI = System.getProperty(PROCESS_NEW_STUDENT_COURSES_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+PROCESS_NEW_STUDENT_COURSES_URI);
        template.setDefaultEndpointUri(routeURI);
        template.requestBody(new Object());

        StatefulKnowledgeSession kieSession = (StatefulKnowledgeSession) applicationContext.getBean(DroolsCommandHelper.KSESSION_NAME);

        ArrayList accreditationList = (ArrayList) kieSession.getGlobal(DroolsCommandHelper.ACCREDITATION_LIST_NAME);

        AccreditationDefinition accreditationDefinition = new AccreditationDefinition();
        accreditationDefinition.setAccreditationname("Red Hat Advanced Delivery Specialist - Cloud Management");

        Accreditation accreditation = new Accreditation();
        accreditation.setAccreditation(accreditationDefinition);

        StudentAccreditation studentAccreditation = new StudentAccreditation();
        studentAccreditation.setStudentid(10000);
        studentAccreditation.setAccreditationtype(StudentAccreditation.Types.Active.name());
        studentAccreditation.setAccreditationid(1);

        for (Object each : accreditationList) {
            Accreditation accreditationReturned = (Accreditation) each;
            assertEquals(studentAccreditation.getStudentid(), accreditationReturned.getStudent().getStudentid());
            assertEquals(studentAccreditation.getAccreditationtype(), accreditationReturned.getStudentAccred().getAccreditationtype());
            assertEquals(studentAccreditation.getAccreditationid(), accreditationReturned.getAccreditation().getAccreditationid());
            assertEquals(accreditationDefinition.getAccreditationname(),accreditationReturned.getAccreditation().getAccreditationname());
        }

    }

    @Ignore
    @Test
    public void testUpdateStudentCourseProcessedByStudent() throws InterruptedException {
        template.setDefaultEndpointUri(UPDATE_STUDENT_COURSE_URI);
        Student studentObj = DomainMockObjectHelper.getMockStudent();
        template.sendBody(studentObj);
    }
}
