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
    private static final String ATTACHMENT_VALIDATION_EXCEPTIONS = "ATTACHMENT_VALIDATION_EXCEPTIONS";
    
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
    
    public Collection<Student> convertToCanonicalStudents(Exchange exchange) {
        Map<String,Student> sMap = new HashMap<String, Student>();
        Map<String, AttachmentValidationException> exceptions = new HashMap<String, AttachmentValidationException>();
        int dupsCounter = 0;
        int updateStudentsCounter = 0;
        @SuppressWarnings("unchecked")
        List<StudentRegistrationBindy> sBindyList = (List<StudentRegistrationBindy>) exchange.getIn().getBody();
        for(StudentRegistrationBindy sBindy : sBindyList){
            if(sMap.containsKey(sBindy.getEmail()) || exceptions.containsKey(sBindy.getEmail())) {
                dupsCounter++;
            }else {
                Student student;
                try {
                    student = this.convertStudentRegBindyToCanonicalStudent(sBindy);
                    if(student.getStudentid() != 0)
                        updateStudentsCounter++;
                    sMap.put(student.getEmail(), student);
                } catch (AttachmentValidationException e) {
                    exceptions.put(sBindy.getEmail(), e);
                }
            }
        }
        logger.info("convertToCanonicalStudents() total students = "+sMap.size()+" : updatedStudents = "+updateStudentsCounter+" : dups = "+dupsCounter+" : exceptions = "+exceptions.size());
        if(exceptions.size() > 0)
            exchange.getIn().setHeader(ATTACHMENT_VALIDATION_EXCEPTIONS, exceptions.values());
        return sMap.values();
    }
    
    public Student convertStudentRegBindyToCanonicalStudent(@Body StudentRegistrationBindy studentBindy) throws AttachmentValidationException{
        if(studentBindy == null)
            throw new AttachmentValidationException("convertStudentRegBindyToCanonicalStudent() passed null studentBindy");
        
        int companyId = 0;
        try {
            companyId = canonicalDAO.getCompanyID(studentBindy.getCompany());
        }catch(org.springframework.dao.EmptyResultDataAccessException x) {
            String eMessage = ExceptionCodes.GPTE_SR1000+"= "+studentBindy.getName()+" : companyName = "+studentBindy.getCompany();
            throw new AttachmentValidationException(eMessage);
        }
        logger.debug("convertStudentRegBindyToCanonicalStudent() student = "+studentBindy.getName()+" : companyName = "+studentBindy.getCompany()+" : companyId = "+companyId);
        Student sObj = studentBindy.convertToCanonicalStudent();
        
        sObj.setCompanyid(companyId);
        
        // Check if student already exists in DB
        try {
            Student sObjFromDB = canonicalDAO.getStudentByEmail(studentBindy.getEmail());
            sObj.setStudentid(sObjFromDB.getStudentid());
        }catch(org.springframework.dao.EmptyResultDataAccessException x){
            
        }
        return sObj;
    }
    
    public void throwAnyCachedExceptions(Exchange exchange) {
        // purge any existing exceptions
        exchange.setException(null);
        
        Object exObj = exchange.getIn().getHeader(ATTACHMENT_VALIDATION_EXCEPTIONS);
        if(exObj != null){
            Collection<AttachmentValidationException> exceptions = (Collection<AttachmentValidationException>)exObj;
            StringBuilder sBuilder = new StringBuilder("\n");
            for(AttachmentValidationException x : exceptions) {
                sBuilder.append(x.getLocalizedMessage()+"\n");
            }
            exchange.setException(new Exception(sBuilder.toString()));
        }
        
    }
}
