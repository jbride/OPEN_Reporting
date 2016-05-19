package com.redhat.gpe.accreditation.service;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Service;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.aggregate.TimeoutAwareAggregationStrategy;
import org.apache.log4j.Logger;

import com.redhat.gpe.accreditation.util.SpreadsheetRule;

public class ReportOnCoursesResultsAggregator implements Service, TimeoutAwareAggregationStrategy {

    private static final String HEADER_CAMEL_FILENAME="CamelFileName";
    private Logger logger = Logger.getLogger("InvalidAttributeExceptionAggregator");
    
    
    private int successCount = 0;
    
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        
        // 1)  generate ruleName based on spreadsheet row number
        int rNumber = 4;  // there are 3 rows of headers
        List<SpreadsheetRule> latestRules = (List<SpreadsheetRule>)newExchange.getIn().getBody();
        for(SpreadsheetRule sRule : latestRules) {
            sRule.generateRuleName(rNumber);
            rNumber++;
        }
        
        
        // 2) merge new spreadsheet rules into list of old spreadsheet rules
        if( oldExchange == null ) {
            oldExchange = new DefaultExchange(newExchange);
            oldExchange.getIn().setBody(latestRules);
        }else {
            List<SpreadsheetRule> oldRules = (List<SpreadsheetRule>) oldExchange.getIn().getBody();
            oldRules.addAll(latestRules);
        }
        List<SpreadsheetRule> oldRules = (List<SpreadsheetRule>) oldExchange.getIn().getBody();
        
        logger.info("aggregate() latest file = "+newExchange.getIn().getHeader(HEADER_CAMEL_FILENAME)+" : # of aggregated rules = "+oldRules.size());
        return oldExchange;
    }

    public void start() throws Exception {
        logger.info("start()");
    }

    public void stop() throws Exception {
        logger.info("stop()");
    }

    public void timeout(Exchange oldExchange, int arg1, int arg2, long arg3) {
        List<SpreadsheetRule> oldRules = (List<SpreadsheetRule>) oldExchange.getIn().getBody();
        logger.info("timeout() # of aggregated rules = "+oldRules.size());
    }
}