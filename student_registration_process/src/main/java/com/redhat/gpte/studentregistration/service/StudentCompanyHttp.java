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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Path("/rs/")
public class StudentCompanyHttp {

    private Logger logger = LoggerFactory.getLogger("StudentCompanyHttp");

    /**
     * sample usage :
     *  curl -X PUT -HAccept:text/plain $HOSTNAME:8330/knowledgeService/kbase
     *  curl -X PUT -HAccept:text/plain http://pfpcore-jbride0.rhcloud.com/knowledgeService/kbase
     */
    /*
    @PUT
    @Path("/student")
    public Response updateStudent(@PathParam("salesForceId")final String salesForceId,
                                 ) {
        ResponseBuilder builder = Response.ok();
        try {
            log.info("updateStudent() ");
        }catch(RuntimeException x){
            builder = Response.status(Status.SERVICE_UNAVAILABLE);
        }
        return builder.build();
    }
    */

    /**
     * sample usage :
     *  curl -X GET -HAccept:text/plain $HOSTNAME:8330/knowledgeService/sanityCheck
     */
    @GET
    @Path("/sanityCheck")
    @Produces({ "text/plain" })
    public Response sanityCheck() {
        ResponseBuilder builder = Response.ok("good to go\n");
        return builder.build();
    }

}
