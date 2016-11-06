package com.redhat.gpte.studentregistration.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.helper.DenormalizedStudent;
import com.redhat.gpte.services.AttachmentValidationException;
import com.redhat.gpte.services.ExceptionCodes;
import com.redhat.gpte.services.GPTEBaseServiceBean;
import com.redhat.gpte.studentregistration.service.studentreg.StudentRegistrationBindy;

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
        Map<String, String> uploadExceptionMap = (Map<String,String>)exchange.getIn().getHeader(UPLOAD_EXCEPTION_MAP);
        exchange.getIn().removeHeader(UPLOAD_EXCEPTION_MAP);
        
        int count = 0;
        for(DenormalizedStudent sObj : students) {
            String email = sObj.getStudentObj().getEmail();
            if(uploadExceptionMap.containsKey(email)){
                logger.error(email+" : will not update ipaStatus because of upload issues");
            }else {
                int sUpdate = this.canonicalDAO.updateStudentStatus(sObj.getStudentObj().getEmail(), 1, Student.IPA_STATUS);
                count = count + sUpdate;
            }
        }
        logger.info("updateIPAFlagOnStudents() # of students to update = "+students.size()+" : total updated = "+count);
        
        if(uploadExceptionMap.size() > 0){
            StringBuilder sBuilder = new StringBuilder("updateIPAFlagOnStudents() following students not updated due to upload issues with LDAP: \n\n");
            Iterator emailIt = uploadExceptionMap.keySet().iterator();
            while(emailIt.hasNext()){
                String email = (String)emailIt.next();
                sBuilder.append(email);
                sBuilder.append("\n");
                sBuilder.append(uploadExceptionMap.get(email));
                sBuilder.append("\n\n");
            }
            throw new RuntimeException(sBuilder.toString());
        }
    }
}
