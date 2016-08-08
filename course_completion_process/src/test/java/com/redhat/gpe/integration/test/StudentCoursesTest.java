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

public class StudentCoursesTest extends CamelSpringTestSupport {
    
    private static final String PERSIST_STUDENT_COURSE_URI = "direct:add-student-course-to-db";
    private static final String UPDATE_STUDENT_COURSE_URI = "direct:set-processed-on-student-course-by-student";

    public StudentCoursesTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/course-completion-camel-context.xml");
    }

    @Before
    public void init() {
    }
    
    /* verification:   select * from StudentCourses where StudentId = 10145 and CourseID = "MWS-SE-BPA-ASM-BRMS";
       cleanup:        delete from StudentCourses where studentId = 10145 and CourseID = "MWS-SE-BPA-ASM-BRMS";
     */
    @Ignore
    @Test
    public void testPersistNewStudentCourse() throws InterruptedException {
        Endpoint endpoint = context.getEndpoint(PERSIST_STUDENT_COURSE_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();

        Student studentObj = DomainMockObjectHelper.getMockStudent();
        Course courseObj = DomainMockObjectHelper.getMockCourse();
        Language langObj = DomainMockObjectHelper.getMockLanguage();
        StudentCourse sCourse = DomainMockObjectHelper.getMockStudentCourse();

        CourseCompletion scWrapper = new CourseCompletion(studentObj, courseObj, langObj, sCourse);
        in.setBody(scWrapper);
        template.send(PERSIST_STUDENT_COURSE_URI, exchange);
    }
    
    /* verification:   select * from StudentCourses where StudentId = 10145 and CourseID = "MWS-SE-BPA-ASM-BRMS";
       cleanup:        delete from StudentCourses where studentId = 10145 and CourseID = "MWS-SE-BPA-ASM-BRMS";
     */
    @Ignore
    @Test
    public void testPersistNonPassingNewStudentCourse() throws InterruptedException {
        Endpoint endpoint = context.getEndpoint(PERSIST_STUDENT_COURSE_URI);
        Exchange exchange = endpoint.createExchange();
        exchange.setPattern(ExchangePattern.InOnly);
        Message in = exchange.getIn();

        Student studentObj = DomainMockObjectHelper.getMockStudent();
        Course courseObj = DomainMockObjectHelper.getMockCourse();
        Language langObj = DomainMockObjectHelper.getMockLanguage();
        StudentCourse sCourse = DomainMockObjectHelper.getMockStudentCourse();
        sCourse.setAssessmentscore((byte) 65);

        CourseCompletion scWrapper = new CourseCompletion(studentObj, courseObj, langObj, sCourse);
        in.setBody(scWrapper);
        template.send(PERSIST_STUDENT_COURSE_URI, exchange);
    }

    @Ignore
    @Test
    public void testUpdateStudentCourseProcessedByStudent() throws InterruptedException {
        template.setDefaultEndpointUri(UPDATE_STUDENT_COURSE_URI);
        Student studentObj = DomainMockObjectHelper.getMockStudent();
        template.sendBody(studentObj);
    }
    
    @Ignore
    @Test
    public void testPersistDuplicateStudentCourse() throws InterruptedException {
    	template.setDefaultEndpointUri(PERSIST_STUDENT_COURSE_URI);

        Student studentObj = DomainMockObjectHelper.getMockStudent();
        Course courseObj = DomainMockObjectHelper.getMockCourse();
        Language langObj = DomainMockObjectHelper.getMockLanguage();
        StudentCourse sCourse = DomainMockObjectHelper.getMockStudentCourse();
        sCourse.setAssessmentscore((byte) 90);

        CourseCompletion scWrapper = new CourseCompletion(studentObj, courseObj, langObj, sCourse);

        template.sendBody(scWrapper);
        template.sendBody(scWrapper);
    }
}
