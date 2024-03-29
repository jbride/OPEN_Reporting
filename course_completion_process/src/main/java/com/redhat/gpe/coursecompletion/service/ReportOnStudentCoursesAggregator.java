package com.redhat.gpe.coursecompletion.service;

import org.apache.camel.Exchange;
import org.apache.camel.Service;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;
import org.apache.log4j.Logger;

import com.redhat.gpte.services.InvalidCourseException;

public class ReportOnStudentCoursesAggregator implements Service, CompletionAwareAggregationStrategy {

    private static final String AGGREGATION_STOP="aggregation_stop";
    private static final String STUDENTS_UPDATED_COUNT = "studentsUpdatedCount";
    private static final String STOP = "STOP";
    private static final String ALL_IS_WELL = "GPTE Course Completion Processing:  The following number of students updated with LDAP attributes: ";
    private static final String ALL_IS_NOT_WELL = "GPTE Course Completion Processing: Errors while updating student records\n";
    private Logger logger = Logger.getLogger("ReportOnStudentCoursesAggregator");
    
    private int successCount = 0;
    
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        
        if( oldExchange == null ) {
            oldExchange = new DefaultExchange(newExchange);
        }
        Object aggregatorStopObj = newExchange.getIn().getHeader(AGGREGATION_STOP);
        Object newExceptionBuilderObj = newExchange.getIn().getHeader(InvalidCourseException.VALIDATION_EXCEPTION_BUFFER);
        Object oldExceptionBuilderObj = oldExchange.getIn().getHeader(InvalidCourseException.VALIDATION_EXCEPTION_BUFFER);
        
        if(aggregatorStopObj != null) {
            // This is a aggregator STOP message
            
            //set newExchange with final count of successful updates
            oldExchange.getIn().setHeader(STUDENTS_UPDATED_COUNT, successCount);
            
            //Set STOP String as body of oldMessage to trigger aggregator completion
            oldExchange.getIn().setBody(STOP);
            
        }else if (newExceptionBuilderObj == null){
            
            // No Errors in header; this must be a successfully updated student
            successCount++;
        }else {
            
            // New Message contains a StringBuilder with an exception
            
            StringBuilder newExceptionBuilder = (StringBuilder)newExceptionBuilderObj;
            if(oldExceptionBuilderObj == null) {
                oldExchange.getIn().setHeader(InvalidCourseException.VALIDATION_EXCEPTION_BUFFER, newExceptionBuilder);
            }else {
                StringBuilder oldBuilder = (StringBuilder)oldExchange.getIn().getHeader(InvalidCourseException.VALIDATION_EXCEPTION_BUFFER);
                oldBuilder.append(newExceptionBuilder.toString());
            }
        }
        return oldExchange;
    }

    public void start() throws Exception {
        logger.info("start()");
    }

    public void stop() throws Exception {
        logger.info("stop()");
    }

    public void onCompletion(Exchange exchange) {
        logger.info("onCompletetion  number of successfully updated students = "+successCount);
        
        //If exceptions do exist, then set the exception on the Camel message
        if(exchange.getIn().getHeader(InvalidCourseException.VALIDATION_EXCEPTION_BUFFER) != null)
            exchange.setException(new Exception(ALL_IS_NOT_WELL+((StringBuilder)exchange.getIn().getHeader(InvalidCourseException.VALIDATION_EXCEPTION_BUFFER)).toString()));
        
        successCount = 0;
    }
}
