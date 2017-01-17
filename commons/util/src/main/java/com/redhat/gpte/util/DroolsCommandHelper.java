package com.redhat.gpte.util;

import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.helper.CourseCompletion;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by samueltauil on 2/24/16.
 */
public class DroolsCommandHelper implements ApplicationContextAware {
    
    private static Logger logger = Logger.getLogger("DroolsCommandHelper");
    public static final String KSESSION_NAME = "gpteAccreditationSession";
    public static final String ACCREDITATION_LIST_NAME = "accreditationList";
    private static final String DATE_PATTERN = "dd-MMM-yyyy";

    public ApplicationContext applicationContext;

    public DroolsCommandHelper(){
    }

    public void insertAndFireAll(Exchange exchange) {
        final Message in = exchange.getIn();
        List<CourseCompletion> factList = (List<CourseCompletion>) in.getBody();
        if(factList.size() == 0)
            throw new RuntimeException("DroolsCommandHelper() No facts are being inserted into session");
        
        Student studentObj = factList.get(0).getStudent();

        BatchExecutionCommandImpl command = new BatchExecutionCommandImpl();
        final List<GenericCommand<?>> commands = command.getCommands();
        commands.add(new InsertObjectCommand(studentObj));

        for (CourseCompletion courseCompletion : factList) {
            commands.add(new InsertObjectCommand(courseCompletion));
        }
        commands.add(new FireAllRulesCommand());

        in.setBody(command);
    }

    public void flushKieSession(Exchange exchange) {

        StatefulKnowledgeSession kieSession = (StatefulKnowledgeSession) getApplicationContext().getBean(KSESSION_NAME);
        Collection<FactHandle> facts = kieSession.getFactHandles();
        Iterator<FactHandle> iFacts = facts.iterator();

        while(iFacts.hasNext()){
            FactHandle fHandle = iFacts.next();
            kieSession.delete(fHandle);
        }

        List<Accreditation> accreditationListFromRules = (List<Accreditation>) kieSession.getGlobal(ACCREDITATION_LIST_NAME);
        List<Accreditation> exchangeList = new ArrayList<Accreditation>();
        exchangeList.addAll(accreditationListFromRules);
        accreditationListFromRules.clear();
        List<Accreditation> sortedAccreds = sortAccreditations(exchangeList);
        exchange.getIn().setBody(sortedAccreds);
        if(sortedAccreds.size() > 0) {
            logger.info("following # of accreditations created: "+sortedAccreds.size());
        }
    }

    public List<Accreditation> sortAccreditations(List<Accreditation> accreditations) {
        if(accreditations.size() == 1)
          return accreditations;

        Accreditation[] accredArray = accreditations.toArray(new Accreditation[0]);
        Arrays.sort( accredArray, new AccreditationDateComparator() );
        return Arrays.asList(accredArray);
    }

    public void dumpKieBase(Exchange exchange) {
        StatefulKnowledgeSession kieSession = (StatefulKnowledgeSession) getApplicationContext().getBean(KSESSION_NAME);
        KieBase kBase = kieSession.getKieBase();
        Collection<KiePackage> kPackages = kBase.getKiePackages();
        StringBuilder sBuilder = new StringBuilder(" # of kPackages = "+ kPackages.size());
        int numRules = 0;
        for(KiePackage kPackage : kPackages) {
            Collection<Rule> rules = kPackage.getRules();
            if(rules.size() == 0)
                sBuilder.append("\n\tNo rules for package = "+kPackage.getName());
            for(Rule rObj : rules) {
                numRules++;
                sBuilder.append("\n\t"+numRules+" : package = "+kPackage.getName()+" : rule = "+rObj.getName());
            }
        }
        logger.info("dumpKieBase() \n"+sBuilder.toString()+"\n\n # of rules = "+numRules+"\n\n");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
         this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    public static boolean isLatestCourseCompletionWithinDates(boolean dumpCourseCompletions, 
                                                              String olderDateBoundary, 
                                                              String recentDateBoundary, 
                                                              CourseCompletion... completions) throws ParseException {
        CourseCompletion latestCC = determineMostRecentCourseCompletion(dumpCourseCompletions, completions);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        if(StringUtils.isNotEmpty(olderDateBoundary)){
            Date olderDateObjBoundary = formatter.parse(olderDateBoundary);
            if(olderDateObjBoundary.after(latestCC.getAssessmentDate()))
                return false;
        }
        if(StringUtils.isNotEmpty(recentDateBoundary)){
            Date recentDateObjBoundary = formatter.parse(recentDateBoundary);
            if(recentDateObjBoundary.before(latestCC.getAssessmentDate()))
                return false;
        }
        return true;
    }
    
    public static CourseCompletion determineMostRecentCourseCompletion(boolean dumpCourseCompletions, CourseCompletion... completions) {
        if(completions.length == 1)
            return completions[0];
        
        Arrays.sort(completions, new CourseCompletionDateComparator());
        CourseCompletion latestCC = completions[completions.length - 1];
        if(dumpCourseCompletions)
            dumpCourseCompletionDates(completions);
        return latestCC;
    }
    
    private static void dumpCourseCompletionDates(CourseCompletion[] completions) {
        Collections.reverse(Arrays.asList(completions));
        StringBuilder sBuilder = new StringBuilder("dumpCourseCompletionDates() dates as follows:");
        for(CourseCompletion ccObj : completions){
            sBuilder.append("\n\t"+ccObj.getCourseName()+"\t: "+ccObj.getAssessmentDate());
        }
        logger.info(sBuilder.toString());
    }

}
