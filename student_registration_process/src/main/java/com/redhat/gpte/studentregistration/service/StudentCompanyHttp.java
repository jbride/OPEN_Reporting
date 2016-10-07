package com.redhat.gpte.studentregistration.service;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.DomainValidationException;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.helper.DenormalizedStudent;
import com.redhat.gpte.services.GPTEBaseServiceBean;
import com.redhat.gpte.studentregistration.util.StudentRegistrationBindy;

@Stateless
@Path("/rs/")
public class StudentCompanyHttp {

    private static final String PROCESS_DENORMALIZED_STUDENT_VM_URI="vm:process-denormalized-student-vm";
    private static final String PROCESS_STUDENT_REGISTRATION_VM_URI="vm:process-student-registrations-vm";
    private static final String FALSE = "FALSE";
    private static final String UPLOAD_TO_GPTE_IPA = "UPLOAD_TO_GPTE_IPA";
    private static final String SALES_FORCE_ID = "salesForceId";
    private static Logger logger = LoggerFactory.getLogger("StudentCompanyHttp");
    private static ObjectMapper jsonMapper;
    private static Object lockObj = new Object();

    private String studentRegServiceUri = null;


    public StudentCompanyHttp() {

        if(jsonMapper == null) {
            synchronized(lockObj) {
                if(jsonMapper != null)
                    return;

                jsonMapper = new ObjectMapper();
            }
        }
    }

    /**
     * sample usage :
     *  curl -v -X PUT -HAccept:text/plain -HContent-Type:application/json --upload-file student_registration_process/src/test/resources/rs/studentregistration.json $HOSTNAME:8205/user-registration-process/rs/studentregistration/123456
     */
    @PUT
    @Path("/studentregistration/{salesForceId: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ "text/plain" })
    public Response processStudentReg(@PathParam(SALES_FORCE_ID)final String salesForceId,
                                  final String payload
                                 ) {
        ResponseBuilder builder = Response.ok();
        Producer producer = null;
        StudentRegistrationBindy sObj = null;
        try {
            logger.info("processStudentReg() salesForceId = "+salesForceId);
            sObj = jsonMapper.readValue(payload, StudentRegistrationBindy.class);
            logger.debug("processStudentReg() payload = "+sObj);
        }catch(java.io.IOException x) {
            x.printStackTrace();
            builder = Response.status(Status.BAD_REQUEST);
            return builder.build();
        }

        try {

            CamelContext cContext = new DefaultCamelContext();
            Endpoint endpoint = cContext.getEndpoint(PROCESS_STUDENT_REGISTRATION_VM_URI);
            producer = endpoint.createProducer();
            producer.start();
            Exchange exchange = producer.createExchange();
            exchange.getIn().setBody(sObj);
            producer.process(exchange);
        }catch(Exception x){
            x.printStackTrace();
            builder = Response.status(Status.SERVICE_UNAVAILABLE);
            return builder.build();
        }finally {
            try {
                if(producer != null)
                    producer.stop();
            } catch(Exception y) {
                y.printStackTrace();
            }
        }
        builder = Response.ok("successfully processed student registration with following salesforceid: "+salesForceId+"\n");
        return builder.build();
    }
    
    /**
     * sample usage :
     *  Post to GPTE but don't upload to IPA:  curl -v -X PUT -HAccept:text/plain -HContent-Type:application/json --upload-file student_registration_process/src/test/resources/rs/student.json $HOSTNAME:8205/user-registration-process/rs/student/123456
     *  Post to GPTE and upload to IPA:        curl -v -X PUT -HAccept:text/plain -HContent-Type:application/json -HUPLOAD_TO_GPTE_IPA:TRUE --upload-file student_registration_process/src/test/resources/rs/student.json $HOSTNAME:8205/user-registration-process/rs/student/123456
     */
    @PUT
    @Path("/student/{salesForceId: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ "text/plain" })
    public Response processStudent(@PathParam(SALES_FORCE_ID)final String salesForceId,
                                   @HeaderParam(UPLOAD_TO_GPTE_IPA) String uploadIPA,
                                   final String payload
                                 ) {
        ResponseBuilder builder = Response.ok();
        Producer producer = null;
        Student sObj = null;
        try {
            logger.info("processStudentReg() salesForceId = "+salesForceId);
            sObj = jsonMapper.readValue(payload, Student.class);
            sObj.validate();
            
            if(StringUtils.isNotEmpty(uploadIPA))
                sObj.setShouldUpdateIPA(Boolean.parseBoolean(uploadIPA));
            
            logger.debug("processStudentReg() payload = "+sObj);
        }catch(DomainValidationException x) {
            builder = Response.status(Status.BAD_REQUEST);
            builder.entity(x.getLocalizedMessage());
            return builder.build();
        }catch(java.io.IOException x) {
            x.printStackTrace();
            builder = Response.status(Status.BAD_REQUEST);
            return builder.build();
        }

        try {
            Company cObj = new Company();
            cObj.setCompanyname(sObj.getCompanyName());

            DenormalizedStudent dsObj = new DenormalizedStudent();
            dsObj.setStudentObj(sObj);
            dsObj.setCompanyObj(cObj);
            
            CamelContext cContext = new DefaultCamelContext();
            Endpoint endpoint = cContext.getEndpoint(PROCESS_DENORMALIZED_STUDENT_VM_URI);
            producer = endpoint.createProducer();
            producer.start();
            Exchange exchange = producer.createExchange();
            exchange.getIn().setBody(dsObj);
            exchange.getIn().setHeader(GPTEBaseServiceBean.QUERY_LDAP, FALSE);
            exchange.getIn().setHeader(GPTEBaseServiceBean.UPDATE_COMPANY, FALSE);
            producer.process(exchange);
        }catch(Exception x){
            x.printStackTrace();
            builder = Response.status(Status.SERVICE_UNAVAILABLE);
            return builder.build();
        }finally {
            try {
                if(producer != null)
                    producer.stop();
            } catch(Exception y) {
                y.printStackTrace();
            }
        }
        builder = Response.ok("successfully processed student with following salesforceid: "+salesForceId+"\n");
        return builder.build();
    }

    /**
     * sample usage :
     *   curl -X GET -HAccept:text/plain $HOSTNAME:8205/user-registration-process/rs/sanityCheck
     */
    @GET
    @Path("/sanityCheck")
    @Produces({ "text/plain" })
    public Response sanityCheck() {
        ResponseBuilder builder = Response.ok("good to go\n");
        return builder.build();
    }

}
