package com.redhat.gpte.studentregistration.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.helper.DenormalizedStudent;
import com.redhat.gpte.services.AttachmentValidationException;
import com.redhat.gpte.services.ExceptionCodes;
import com.redhat.gpte.services.GPTEBaseServiceBean;
import com.redhat.gpte.studentregistration.util.StudentRegistrationBindy;

public class StudentRegistrationServiceBean extends GPTEBaseServiceBean {

    private Logger logger = Logger.getLogger(getClass());
    private static final String STUDENTS_TO_UPDATE = "studentsToUpdate";
    
    /*  This annotation is supported by Spring by adding the following to your application's:  *-camel-context.xml
     *     - <context:annotation-config />
     *     
     *  It also works on Fuse (when using Spring DSL) on EAP by adding the following:
     *     1)  specify the following in application's jboss-deployment-structure.xml :
     *           <module name="org.springframework.spring" export="true" meta-inf="export" />
     *           
     *         NOTE:  this also allows for use of Spring specific annotations ( when using Fuse on EAP ) such as:
     *            org.springframework.beans.factory.annotation.Autowired
     *            
     *     2)  Need to add the following module dep to:  $JBOSS_HOME/fuse/org/springframework/core/main/module.xml
     *           <module name="javax.annotation.api" export="true" />
     */
    @PostConstruct
    public void init() {
        if(this.canonicalDAO == null)
            throw new RuntimeException("init() canonicalDAO is null!  Problem with Spring configurations ?? ");
        
        logger.info("init() canonicalDAO = "+ this.canonicalDAO);
    }
    
    public List<Student> selectRHTStudentsWithMissingAttributes(Exchange exchange) {
        List<Student> students = canonicalDAO.selectRHTStudentsWithMissingAttributes();
        logger.info("selectRHTStudentsWithMissingAttributes() # of emps with missing attributes = "+students.size());
        if(students == null || students.isEmpty()) {
            logger.info("selectRHTStudentsWithMissingAttributes() ALL STUDENTS HAVE REQUIRED ATTRIBUTES");
        }
        exchange.getIn().setHeader(STUDENTS_TO_UPDATE, students.size());
        return students;
    }
    
    public List<DenormalizedStudent> queryForStudentsToPushToIPA() {
        List<DenormalizedStudent> students = canonicalDAO.selectStudentsByIpaStatus(Student.IPA_UNSYNCHED);
        if(students == null || students.isEmpty()) {
            logger.info("queryForStudentsToPushToIPA() ALL STUDENTS HAVE BEEN PUSHED TO IPA");
        }else {
            logger.info("queryForStudentsToPushToIPA() # of students to push = "+students.size());
        }
        return students;
    }

    public void updateIPAFlagOnStudents(Exchange exchange) {
        List<DenormalizedStudent> students = (List<DenormalizedStudent>)exchange.getIn().getBody();
        logger.info("updateIPAFlagOnStudents() # of students to update = "+students.size());
    }
}
