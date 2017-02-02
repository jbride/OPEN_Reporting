package com.redhat.gpe.accreditation.service;

import com.redhat.gpe.accreditation.util.Constants;
import com.redhat.gpe.accreditation.util.SpreadsheetRule;
import com.redhat.gpe.accreditation.util.WebClientDevWrapper;
import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentAccreditation;
import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpte.services.AttachmentValidationException;
import com.redhat.gpte.services.GPTEBaseServiceBean;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class AccreditationProcessBean extends GPTEBaseServiceBean {

    private static final String TOKEN_URL = "sb_tokenURL";
    private static final String PERSON_EMAIL = "sb_personIdByEmailURL";
    private static final String Q_URL = "sb_addQualificationUrl";
    private static final String GRANT_TYPE = "sb_grantType";

    private static final String CLIENT_ID = "sb_skillsbase_clientId";
    private static final String CLIENT_SECRET = "sb_skillsbase_clientSecret";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String CAMEL_FILE_HEADER_NAME = "CamelFileName";
    private static final String DRL_PATH = "accred_drl_rules_path";
    private static final String TSV = "tsv";
    private static final String DRL = "drl";
    private static final String LMS_REFRESH_STORED_PROC = "call lms_transactional.refresh_lms_reporting";
    private static final String OPEN_PAREN = "{";
    private static final String CLOSED_PAREN = "}";
    private static final Object OPEN_BRACKET = "[";
    private static final Object CLOSED_BRACKET = "]";
    private static final String GAINED_ACCRED_LOCK="gainedAccredLock";
    private static final String WHEN = "when";
    private static final String DOUBLE_CLOSE_PARENS = "))";
    private static final String SINGLE_CLOSE_PAREN = ")";
    private static final String EVAL = "eval(";
    private static final String NEW_LINE = "\n";
    private static final String CONSEQUENCE_FUNCTION = "determineMostRecentCourseCompletion";
    
    private static Object accredProcessLock = new Object();
    private static boolean isLocked = false;

    private Logger logger = Logger.getLogger(getClass());
    
    private String tokenUrl;
    private String personIdByEmailUrl;
    private String addQualificationUrl;

    private String grantType;
    private String clientId;
    private String clientSecret;
    private String drlPath;
    
    private DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public AccreditationProcessBean() {
        tokenUrl = System.getProperty(TOKEN_URL);
        if(StringUtils.isEmpty(tokenUrl))
            throw new RuntimeException("must set system property: "+TOKEN_URL);

        personIdByEmailUrl = System.getProperty(PERSON_EMAIL);
        if(StringUtils.isEmpty(personIdByEmailUrl))
            throw new RuntimeException("must set system property: "+PERSON_EMAIL);

        addQualificationUrl = System.getProperty(Q_URL);
        if(StringUtils.isEmpty(addQualificationUrl))
            throw new RuntimeException("must set system property: "+Q_URL);

        clientId = System.getProperty(CLIENT_ID);
        if(StringUtils.isEmpty(clientId))
            throw new RuntimeException("must set system property: "+CLIENT_ID);

        clientSecret = System.getProperty(CLIENT_SECRET);
        if(StringUtils.isEmpty(clientSecret))
            throw new RuntimeException("must set system property: "+CLIENT_SECRET);

        grantType = System.getProperty(GRANT_TYPE);
        if(StringUtils.isEmpty(grantType))
            throw new RuntimeException("must set system property: "+GRANT_TYPE);

        drlPath = System.getProperty(DRL_PATH);
        if(StringUtils.isEmpty(drlPath))
            throw new RuntimeException("must set system property: "+DRL_PATH);

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("init() \n    tokenUrl = "+tokenUrl);
        sBuilder.append("\n    personIdByEmailUrl = "+personIdByEmailUrl);
        sBuilder.append("\n    addQualificationUrl = "+addQualificationUrl);
        sBuilder.append("\n    clientId = "+clientId);
        sBuilder.append("\n    clientSecret = "+clientSecret);
        sBuilder.append("\n    grantType = "+grantType);
        logger.info(sBuilder.toString()); 
    }







/*  ****************        Accreditation Logic Processing locking mechanism        *******************  */
    public boolean isAccredLogicLocked() {
        return isLocked;
    }
    
    public void acquireAccredLogicLock(Exchange exchange) {
        exchange.getIn().setHeader(GAINED_ACCRED_LOCK, false);
        if(isLocked)
            return;
        else {
            synchronized(accredProcessLock) {
                if(isLocked)
                    return;

                isLocked = true;
                exchange.getIn().setHeader(GAINED_ACCRED_LOCK, true);
            }
        }
    }

    public void releaseAccredLogicLock() {
        isLocked = false;
    }
/* *****************************************************************************************************  */






/*  ************    Student Accreditation CRUD Operations     *************************** */
    
    public List<Accreditation> selectUnprocessedStudentAccreditations() {
        List<Accreditation> sAccreds = canonicalDAO.selectUnprocessedStudentAccreditationsByProcessStatus(StudentAccreditation.UNPROCESSED, Student.RED_HAT_EMAIL_SUFFIX);
        if(sAccreds == null || sAccreds.isEmpty()) {
            logger.info("selectUnprocessedStudentAccreditations() no StudentAccreditation objects found with status = "+StudentAccreditation.UNPROCESSED);
        }
        else {
            logger.info("selectUnprocessedStudentAccreditations() Will evaluate following # of StudentAccreditation objects: "+sAccreds.size());
        }
        return sAccreds;
    }
    
    public void setAccreditationIdOnAccreditationObj(@Body Accreditation accredObj){
        if(accredObj.getAccreditationId() != null && accredObj.getAccreditationId() != 0)
            return;
        
        int accredId = canonicalDAO.getAccreditationIdGivenName(accredObj.getName());
        logger.debug(accredObj.getEmail()+" setAccreditationIdOnAccreditationObj() : accredId = "+accredId+" : accredName = "+accredObj.getName());
        accredObj.setAccreditationId(accredId);
    }
    
    public void addStudentAccreditationToDB(@Body Accreditation apWrapper) {
        StudentAccreditation sAccredObj = apWrapper.getStudentAccred();
        try { 
            canonicalDAO.addStudentAccreditation(sAccredObj);
        }catch(Exception x) {
            String eMessage = apWrapper.getStudent().getEmail()+" : "+ sAccredObj.getRuleFired()+"\naddStudentAccreditationToDB() exception thrown: \n\n"+ x.getMessage();
            x.printStackTrace();
            throw new RuntimeException(eMessage);
        }
    }
    
    public boolean isRedHatStudentFromStudentAccreditation(@Body Accreditation saObj) {
        if(saObj.getStudent().getEmail().indexOf(RED_HAT_SUFFIX) > 0) {
            return true;
        }else {
            logger.debug(saObj.getStudent().getEmail()+" : Not a Red Hat associate.  Will not update SkillsBase");
            return false;
        }
    }
    
    public void setProcessedOnAccreditation(@Body Accreditation accredObj ) {
        accredObj.getStudentAccred().setProcessed(StudentAccreditation.PROCESSED_SKILLS_BASE_ONLY);
    }
    
    public int changeStatusOnExpiredStudentAccreditations() {
        return canonicalDAO.changeStatusOnExpiredStudentAccreditations();
    }
/* ************************************************************************************ */
   
    
   







/*  ********************        Accreditation Rule Spreadsheet Processing   ************* */    
    public void validateSpreadsheetRules(Exchange exchange) {

        @SuppressWarnings("unchecked")
        List<SpreadsheetRule> sRules = (List<SpreadsheetRule>)exchange.getIn().getBody();
        if(sRules.isEmpty())
            throw new RuntimeException("no Spreadsheet Rules parsed");
        
        int rNumber = 4;  // there are 3 rows of headers
        Integer problemNumber = 0;
        String fileName = (String) exchange.getIn().getHeader(CAMEL_FILE_HEADER_NAME);
        StringBuilder eBuilder = new StringBuilder(fileName);

        Set<String> courseSet = new HashSet<String>();
        Set<String> accredSet = new HashSet<String>();
        
        for(SpreadsheetRule sRule : sRules) {
            
            // 0) set generated RuleName
            sRule.generateRuleName(rNumber);
            
            // 1)  Validate dates
            problemNumber = checkDate(problemNumber, rNumber, sRule, eBuilder, sRule.getBeginDate());
            problemNumber = checkDate(problemNumber, rNumber, sRule, eBuilder, sRule.getEndDate());
            
            // 2) Validate optional accredition condition
            problemNumber = checkAccredCondition(problemNumber, rNumber, sRule, eBuilder, sRule.getAccredCondition(), accredSet);
            
            // 3)  Validate course completions
            problemNumber = checkCourse(problemNumber, rNumber, sRule, eBuilder, sRule.getCourse1(), courseSet);
            problemNumber = checkCourse(problemNumber, rNumber, sRule, eBuilder, sRule.getCourse2(), courseSet);
            problemNumber = checkCourse(problemNumber, rNumber, sRule, eBuilder, sRule.getCourse3(), courseSet);
            problemNumber = checkCourse(problemNumber, rNumber, sRule, eBuilder, sRule.getCourse4(), courseSet);
            problemNumber = checkCourse(problemNumber, rNumber, sRule, eBuilder, sRule.getCourse5(), courseSet);
            problemNumber = checkCourse(problemNumber, rNumber, sRule, eBuilder, sRule.getCourse6(), courseSet);
            problemNumber = checkCourse(problemNumber, rNumber, sRule, eBuilder, sRule.getCourse7(), courseSet);
            problemNumber = checkCourse(problemNumber, rNumber, sRule, eBuilder, sRule.getCourse8(), courseSet);
            
            // 4)  Validate Accreditation
            problemNumber = checkAccreditation(problemNumber, rNumber, sRule, eBuilder, sRule.getAccredName(), accredSet);
 
            rNumber++;
        }
        
        // 4 throw exception if validation errors exist
        if(problemNumber > 0)
               exchange.setException(new AttachmentValidationException("\nTotal # of Spreadsheet Validation errors: "+problemNumber+"\n"+eBuilder.toString()));
    }
    
    public void reportOnCanonicalCourses(Exchange exchange) {
        
        StringBuilder sBuilder = new StringBuilder();
        Map<String, List<String>> reportMap = new HashMap<String, List<String>>();  //key=courseName, value=List of associated rule names
        Map<String, List<String>> issueMap = new HashMap<String, List<String>>();  //key=ruleName, value=List of unknown courses
        Map<String, List<String>> warningMap = new HashMap<String, List<String>>();  //key=ruleName, value=List of problemetic course
        
        // 1) Get list of spreadsheet rules from exchange
        List<SpreadsheetRule> rules = (List<SpreadsheetRule>) exchange.getIn().getBody();
        if(rules == null || rules.size() ==0) {
            logger.error("reportOnCanonicalCourses() # of rules is zero");
            return;
        }
        sBuilder.append("\n# of rules: "+rules.size());
        
        // 2) Get list of canonical courses
        List<Course> courseList = canonicalDAO.listCanonicalCourses();
        for(Course courseObj : courseList){
            reportMap.put(courseObj.getCoursename(), new ArrayList<String>());
        }
        sBuilder.append("\n# of Canonical Courses: "+reportMap.size());
        
        // 3) Associate rules to courses
        for(SpreadsheetRule rule: rules) {
            associateRulesToCourses(reportMap, warningMap,  rule, issueMap);
        }
        
        // 4)  Sort    :  http://www.mkyong.com/java/how-to-sort-a-map-in-java/
        Map<String, Integer> unsortMap = new HashMap<String, Integer>();
        for(Map.Entry<String, List<String>> eResult : reportMap.entrySet()) {
            unsortMap.put(eResult.getKey(), eResult.getValue().size());
        }
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        

        // 5)  Print Course association results
        sBuilder.append("\n\nResults:  Course / # of rules referencing this course\n");
        for(Map.Entry<String, Integer> eResult : sortedMap.entrySet()) {
            sBuilder.append("\n\t"+eResult.getKey()+"\t\t : "+eResult.getValue());
        }
        
        // 6)  Print Issues
        sBuilder.append("\n\nIssues:  rule name / unknown course");
        for(Map.Entry<String, List<String>> eResult : issueMap.entrySet()) {
            for(String course : eResult.getValue())
                sBuilder.append("\n\t"+eResult.getKey()+"\t\t\t\t : \""+course+"\"");
        }
        
        // 7)  Print Warnings
        sBuilder.append("\n\nWarnings:  rule name / course with trailing white space");
        for(Map.Entry<String, List<String>> eResult : warningMap.entrySet()) {
            for(String course : eResult.getValue())
                sBuilder.append("\n\t"+eResult.getKey()+"\t\t\t\t : \""+course+"\"");
        }
        
        logger.info(sBuilder.toString());
        
        exchange.getIn().setBody(sBuilder.toString());
    }
    
    private void associateRulesToCourses(Map<String, List<String>> reportMap, Map<String, List<String>> warningMap, SpreadsheetRule rule, Map<String, List<String>> issueMap) {

        if(StringUtils.isNotEmpty(rule.getCourse1())){
            ruleHelper(reportMap, warningMap, rule.getCourse1(), rule, issueMap);
            if(StringUtils.isNotEmpty(rule.getCourse2())){
                ruleHelper(reportMap, warningMap, rule.getCourse2(), rule, issueMap);
                if(StringUtils.isNotEmpty(rule.getCourse3())){
                    ruleHelper(reportMap, warningMap, rule.getCourse3(), rule, issueMap);
                    if(StringUtils.isNotEmpty(rule.getCourse4())){
                        ruleHelper(reportMap, warningMap, rule.getCourse4(), rule, issueMap);
                        if(StringUtils.isNotEmpty(rule.getCourse5())){
                            ruleHelper(reportMap, warningMap, rule.getCourse5(), rule, issueMap);
                            if(StringUtils.isNotEmpty(rule.getCourse6())){
                                ruleHelper(reportMap, warningMap, rule.getCourse6(), rule, issueMap);
                                if(StringUtils.isNotEmpty(rule.getCourse7())){
                                    ruleHelper(reportMap, warningMap, rule.getCourse7(), rule, issueMap);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void ruleHelper(Map<String, List<String>> reportMap, Map<String, List<String>> warningMap, String course, SpreadsheetRule rule, Map<String, List<String>> issueMap){
        List<String> rules = reportMap.get(course);
        if(rules != null)
            rules.add(rule.getRuleName());
        else{
            // Determine if the course exists if make case insensitive and remove trailing spaces
            String trimmedCourse = course.trim();
            List<String> trimmedRules = reportMap.get(trimmedCourse);
            if(trimmedRules != null) {
                if(! warningMap.containsKey(rule.getRuleName())){
                    warningMap.put(rule.getRuleName(), new ArrayList<String>());
                }
                warningMap.get(rule.getRuleName()).add(course);
                trimmedRules.add(rule.getRuleName());
            } else {
                if(! issueMap.containsKey(rule.getRuleName())){
                    issueMap.put(rule.getRuleName(), new ArrayList<String>());
                }
                issueMap.get(rule.getRuleName()).add(course);
            }
        }
    }
    
    private Integer checkDate(Integer problemNumber, int rNumber, SpreadsheetRule sRule, StringBuilder eBuilder, String dString){
        if(!StringUtils.isEmpty(dString)){
            try {
                SpreadsheetRule.rulesSDF.parse(dString);
            } catch (ParseException e) {
                problemNumber++;
                eBuilder.append("\n\n"+problemNumber+") Row "+rNumber+" : Date related ParseException: "+dString);
                eBuilder.append("\n"+sRule);
            }
        }
        return problemNumber;
    }
    
    private Integer checkCourse(Integer problemNumber, int rNumber, SpreadsheetRule sRule, StringBuilder eBuilder, String courseName, Set courseSet) {
        if(!StringUtils.isEmpty(courseName)) {
            try {
                if(!courseSet.contains(courseName)) {
                    courseSet.add(courseName);
                    this.canonicalDAO.getCourseByCourseName(courseName, null);
                }
            } catch(org.springframework.dao.EmptyResultDataAccessException e) {
                problemNumber++;
                eBuilder.append("\n\n"+problemNumber+") Row "+rNumber+" : Canonical course name not found : \""+courseName+"\"");
                eBuilder.append("\n"+sRule);
            } catch(org.springframework.dao.IncorrectResultSizeDataAccessException e) {
                problemNumber++;
                eBuilder.append("\n\n"+problemNumber+") Row "+rNumber+" : Canonical course name  : \""+courseName+ "\" : Exception message = "+e.getLocalizedMessage());
                eBuilder.append("\n"+sRule);
            }
        }
        return problemNumber;
    }
    
    private Integer checkAccredCondition(Integer problemNumber, int rNumber, SpreadsheetRule sRule, StringBuilder eBuilder, String accredCondition, Set accredSet) {
        if(!StringUtils.isEmpty(accredCondition)) {
            try {
                if(!accredSet.contains(accredCondition)) {
                    accredSet.add(accredCondition);
                    this.canonicalDAO.getAccreditationIdGivenName(accredCondition);
                }
            } catch(org.springframework.dao.IncorrectResultSizeDataAccessException e) {
                problemNumber++;
                eBuilder.append("\n\n"+problemNumber+") Row "+rNumber+" : Accreditation condition name not found : \""+accredCondition+"\"");
                eBuilder.append("\n"+sRule);
            }
        }
        return problemNumber;
        
    }
    
    private Integer checkAccreditation(Integer problemNumber, int rNumber, SpreadsheetRule sRule, StringBuilder eBuilder, String accredName, Set accredSet) {
        if(StringUtils.isEmpty(accredName)){
            problemNumber++;
            eBuilder.append("\n\n"+problemNumber+") Row "+rNumber+" : Accreditation column must not be null.");
            eBuilder.append("\n"+sRule);
        }else {
            try {
                if(!accredSet.contains(accredName)) {
                    accredSet.add(accredName);
                    this.canonicalDAO.getAccreditationIdGivenName(accredName);
                }
            } catch(org.springframework.dao.IncorrectResultSizeDataAccessException e) {
                problemNumber++;
                eBuilder.append("\n\n"+problemNumber+") Row "+rNumber+" : Accreditation name not found : \""+accredName+"\"");
                eBuilder.append("\n"+sRule);
            }
        }
        return problemNumber;
    }


    public void setHeadersForGeneratingDRL(Exchange exchange)  {
           SpreadsheetRule sRule = (SpreadsheetRule) exchange.getIn().getBody();

           exchange.getIn().setHeader(SpreadsheetRule.BEGIN_DATE, sRule.getBeginDate());
           exchange.getIn().setHeader(SpreadsheetRule.END_DATE, sRule.getEndDate() == null?"":sRule.getEndDate());
           
           if(StringUtils.isNotEmpty(sRule.getAccredCondition()))
               exchange.getIn().setHeader(SpreadsheetRule.ACCRED_CONDITION, sRule.getAccredCondition());
           
            exchange.getIn().setHeader(SpreadsheetRule.COURSE1, sRule.getCourse1());
            if(StringUtils.isNotEmpty(sRule.getCourse2()))
                exchange.getIn().setHeader(SpreadsheetRule.COURSE2, sRule.getCourse2());
            if(StringUtils.isNotEmpty(sRule.getCourse3()))
                exchange.getIn().setHeader(SpreadsheetRule.COURSE3, sRule.getCourse3());
            if(StringUtils.isNotEmpty(sRule.getCourse4()))
                exchange.getIn().setHeader(SpreadsheetRule.COURSE4, sRule.getCourse4());
            if(StringUtils.isNotEmpty(sRule.getCourse5()))
                exchange.getIn().setHeader(SpreadsheetRule.COURSE5, sRule.getCourse5());
            if(StringUtils.isNotEmpty(sRule.getCourse6()))
                exchange.getIn().setHeader(SpreadsheetRule.COURSE6, sRule.getCourse6());
            if(StringUtils.isNotEmpty(sRule.getCourse7()))
                exchange.getIn().setHeader(SpreadsheetRule.COURSE7, sRule.getCourse7());
            
            exchange.getIn().setHeader(SpreadsheetRule.ACCRED_NAME, sRule.getAccredName());
            exchange.getIn().setHeader(SpreadsheetRule.RULE_NAME, sRule.getRuleName());
    }
    
    public void weaveAccredConditionIntoRule(Exchange exchange) {
        String drl = (String)exchange.getIn().getBody();
        String accredCondition = (String)exchange.getIn().getHeader(SpreadsheetRule.ACCRED_CONDITION);
        logger.info("weaveAccredConditionIntoRule(); accredCondition = "+accredCondition);

        // 1) weave accred fact fact into rule
        drl = drl.replace(WHEN, WHEN+"\n    $ac:Accreditation(name == \""+accredCondition+"\")");

        // 2) weave accred fact into date evaluation condition
        int startPos = drl.indexOf(EVAL) - 5;
        int endPos = drl.indexOf(NEW_LINE, startPos+1);
        String evalLine = drl.substring(startPos, endPos);
        String modifiedEvalLine = evalLine.replace(DOUBLE_CLOSE_PARENS, ", $ac"+DOUBLE_CLOSE_PARENS);
        logger.info("weaveAccredConditionIntoRule() startPos = "+startPos+" : endPos = "+endPos+" : evalLine = "+evalLine);
        drl = drl.replace(evalLine, modifiedEvalLine);

        // 3) weave accred fact into consequence
        startPos = drl.indexOf(CONSEQUENCE_FUNCTION);
        endPos = drl.indexOf(SINGLE_CLOSE_PAREN, startPos)+1;
        String consequenceString = drl.substring(startPos, endPos);
        logger.info("weaveAccredConditionIntoRule() startPos = "+startPos+" : endPos = "+endPos+" : consequenceString = "+consequenceString);
        String modifiedString = consequenceString.replace(SINGLE_CLOSE_PAREN, ", $ac"+SINGLE_CLOSE_PAREN);
        drl = drl.replace(consequenceString, modifiedString);

        exchange.getIn().setBody(drl);
    }

    public void changeSuffixOfRuleFileName(Exchange exchange) {

        String fileName = (String) exchange.getIn().getHeader(CAMEL_FILE_HEADER_NAME);
        String newFileName = fileName.replace(TSV, DRL);
        logger.info("changeSuffixOfRuleFileName() new rule file name = "+newFileName);
        exchange.getIn().setHeader(CAMEL_FILE_HEADER_NAME, newFileName);
    }

    public void setStudentAccreditationsJSONResponse(Exchange exchange) throws org.json.JSONException {
        List<CourseCompletion> studentCourses = (List<CourseCompletion>) exchange.getIn().getHeader(STUDENT_COURSES_HEADER);
        JSONObject jObject = new JSONObject();
        if(studentCourses != null) {
            
            // 1)  add email
            CourseCompletion firstCCObj = studentCourses.get(0);
            Student studentObj = firstCCObj.getStudent();
            jObject.put("email", studentObj.getEmail());
            
            // 2)  add List of CourseCompletion names
            List<String> courseCompletions = new ArrayList<String>();
            for(CourseCompletion cc : studentCourses){
                courseCompletions.add(cc.getName());
            }
            jObject.put("courseCompletions", courseCompletions);
        
            // 3) add List of Accreditation names
            List<Accreditation> accreds = (List<Accreditation>)exchange.getIn().getHeader(RULES_FIRED_HEADER);
            List<String> rulesFired = new ArrayList<String>();
            for(Accreditation aObj : accreds){
                rulesFired.add(aObj.getRuleFired());
            }
            jObject.put("accredRulesFired", rulesFired);
        } else {
            jObject.put("email", "Student not found");
        }
        
        exchange.getIn().setBody(jObject);
    }

/* ************************************************************************************ */















/*  *************               SkillsBase Integration              ******************  */

    public void getSkillsBaseToken(Exchange exchange) {
        
        Message in = exchange.getIn();
        try {
                
            HttpClient httpclient = new DefaultHttpClient();
            httpclient = WebClientDevWrapper.wrapClient(httpclient);
            
            HttpPost post = new HttpPost(tokenUrl);
            
            // set up name value pairs
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("grant_type", grantType));
            nvps.add(new BasicNameValuePair("client_id", clientId));
            nvps.add(new BasicNameValuePair("client_secret", clientSecret));

            // set HTTP request message body
            post.setEntity(new UrlEncodedFormEntity(nvps));

            // send POST message
            HttpResponse httpResponse = httpclient.execute(post);

            // process response
            String response = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonResponse = new JSONObject(response);
            String token = jsonResponse.getString("access_token");

            in.setHeader(Constants.TOKEN, token);
        } catch (Exception exc) {
            String message = "Failure making REST API CALL!";
            logger.error(message, exc);
            throw (new RuntimeException(message, exc));
        }
    }
    
    public void getSkillsBasePersonId(Exchange exchange) {

        Message in = exchange.getIn();
        Accreditation studentAccredObj = in.getBody(Accreditation.class);
        String theEmail = studentAccredObj.getStudent().getEmail();
        if(StringUtils.isEmpty(theEmail))
            throw new RuntimeException("getSkillsBasePersonId() Accreditation object passed to this function must have a student email");

        logger.info(theEmail+" : Getting personId from Skills Base web service.");
        
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient = WebClientDevWrapper.wrapClient(httpclient);
            HttpPost post = new HttpPost(personIdByEmailUrl);

            // set up header
            post.setHeader("Authorization", "Bearer " + in.getHeader(Constants.TOKEN));
            
            // set up name value pairs
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("email", theEmail));

            // set HTTP request message body
            post.setEntity(new UrlEncodedFormEntity(nvps));

            // send POST message
            HttpResponse httpResponse = httpclient.execute(post);

            // process response
            String response = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonResponse = new JSONObject(response);

            String status = jsonResponse.getString("status");
            
            logger.info(theEmail+" : response: " + response+" : status = "+status);

            // parse person id
            String personId = null;
            if (SUCCESS.equalsIgnoreCase(status)) {
                JSONObject jsonData = jsonResponse.getJSONObject("data");
                personId = jsonData.getString("id");

                studentAccredObj.getStudent().setSkillsbasePersonId(personId);
            } else if (ERROR.equalsIgnoreCase(status)) {
                String message = jsonResponse.getString("message");

                if (message.contains("No person found")) {
                    studentAccredObj.getStudent().setSkillsbasePersonId(null);
                } else {
                    throw new RuntimeException(message);
                }
            }
        } catch (Exception exc) {
            String message = "Failure making REST API CALL!";
            logger.error(message, exc);
            throw (new RuntimeException(message, exc));
        }
    }
    
    public void postAccreditationToSkillsBase(Exchange exchange) {

        Message in = exchange.getIn();
        Accreditation denormalizedStudentAccred = in.getBody(Accreditation.class);
        
        Student studentObj = denormalizedStudentAccred.getStudent();
        AccreditationDefinition accredObj = denormalizedStudentAccred.getAccreditation();
        StudentAccreditation sAccredObj = denormalizedStudentAccred.getStudentAccred();

        String accredName = accredObj.getAccreditationname();
        
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient = WebClientDevWrapper.wrapClient(httpclient);

            HttpPost post = new HttpPost(addQualificationUrl);
            logger.info(studentObj.getEmail() +" : Sending the following qualification to Skills Base web service : " + accredName);

            // set up header
            post.setHeader("Authorization", "Bearer " + in.getHeader(Constants.TOKEN));
            
            // set up name value pairs
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("name", accredName));
            nvps.add(new BasicNameValuePair("person_id", studentObj.getSkillsbasePersonId()));
            nvps.add(new BasicNameValuePair("status", Constants.COMPLETED));
            
            String startDateStr = dateFormatter.format(sAccredObj.getAccreditationdate());
            nvps.add(new BasicNameValuePair("start_date", startDateStr));
            
            String endDateStr = dateFormatter.format(sAccredObj.getAccreditationdate());
            nvps.add(new BasicNameValuePair("end_date", endDateStr));

            // set HTTP request message body
            post.setEntity(new UrlEncodedFormEntity(nvps));

            // send POST message
            HttpResponse httpResponse = httpclient.execute(post);

            // process response
            String response = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonResponse = new JSONObject(response);

            String status = jsonResponse.getString("status");

            logger.info(studentObj.getEmail() +" : addQualification() response: " + response+" : status = "+status);

            if (!"success".equalsIgnoreCase(status)) {
                String message = "Error sending qualification. Student email: " + studentObj.getEmail() + ",  qualification: " + accredName;
                logger.error(message);
                throw new RuntimeException(message);
            }
        } catch (Exception exc) {
            String message = "Failure making REST API CALL!";
            logger.error(message, exc);
            throw (new RuntimeException(message, exc));
        }
    }

/*  ********************************************************************************************** */

}
