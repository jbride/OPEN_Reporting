package com.redhat.gpte.studentregistration.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.redhat.gpte.studentregistration.util.StudentRegistrationBindy;

@Stateless
@Path("/rs/")
public class StudentCompanyHttp {

    private static final String PROCESS_DENORMALIZED_STUDENT_VM_URI="vm:process-denormalized-student-vm";
    private static final String PROCESS_STUDENT_REGISTRATION_VM_URI="vm:process-student-registrations-vm";
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
     *  curl -v -X PUT -HAccept:text/plain -HContent-Type:application/json --upload-file student_registration_process/src/test/resources/rs/studentregistration.json $HOSTNAME:8205/user-registration-process/rs/student/123456
     */
    @PUT
    @Path("/student/{salesForceId: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ "text/plain" })
    public Response processStudentReg(@PathParam("salesForceId")final String salesForceId,
                                  final String payload
                                 ) {
        ResponseBuilder builder = Response.ok();
        Producer producer = null;
        StudentRegistrationBindy sObj = null;
        try {
            logger.info("processStudentReg() salesForceId = "+salesForceId);
            sObj = jsonMapper.readValue(payload, StudentRegistrationBindy.class);
            logger.info("processStudentReg() payload = "+sObj);
        }catch(java.io.IOException x) {
            x.printStackTrace();
            builder = Response.status(Status.BAD_REQUEST);
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
        }finally {
            try {
                if(producer != null)
                    producer.stop();
            } catch(Exception y) {
                y.printStackTrace();
            }
        }
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
