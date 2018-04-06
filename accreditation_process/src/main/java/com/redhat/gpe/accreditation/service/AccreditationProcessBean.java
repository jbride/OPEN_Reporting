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
import com.redhat.gpe.domain.helper.GPTEBaseCondition;
import com.redhat.gpte.services.AttachmentValidationException;
import com.redhat.gpte.services.GPTEBaseServiceBean;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class AccreditationProcessBean extends GPTEBaseServiceBean {

    private static final String GRANT_TYPE = "sb_grantType";
    private static final String SB_TOKEN_URL = "sb_tokenUrl";
    private static final String SB_PERSON_URL = "sb_personUrl";
    private static final String SB_Q_URL = "sb_qualificationUrl";
    private static final String EXPIRED_MONTHS = "sb_qualificationEndDateDurationInMonths";

    private static final String CLIENT_ID = "sb_skillsbase_clientId";
    private static final String CLIENT_SECRET = "sb_skillsbase_clientSecret";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String CAMEL_FILE_HEADER_NAME = "CamelFileName";
    private static final String DRL_PATH = "accred_drl_rules_path";
    private static final String TSV = "tsv";
    private static final String DRL = "drl";
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
    private static final String SB_DATA = "data";
    private static final String SB_NULL = "null";
    private static final String SB_END_DATE = "end_date";
    private static final String SB_NAME = "name";
    private static final String SB_STATUS = "status";
    private static final String SB_START_DATE = "start_date";
    private static final String SB_NEVER_CHECK_FOR_EXISTING_ACCRED = "sb_neverCheckForExistingAccred";
    private static final String SB_STUDENT_REGISTERED="SB_STUDENT_REGISTERED";
    private static final String SB_STUDENT_NOT_REGISTERED="SB_STUDENT_NOT_REGISTERED";
    private static final String SB_PUSH_RESULTS_CACHE="SB_PUSH_RESULTS_CACHE";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final SimpleDateFormat sdfObj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    
    private static Object accredProcessLock = new Object();
    private static boolean isLocked = false;

    private Logger logger = Logger.getLogger(getClass());
    
    private String tokenUrl;
    private String sbPersonUrl;
    private String sbQualificationUrl;
    private int expiredMonths = 24;

    /* Cache Skills Base Token
     *   Purpose:
     *      Allow for Skills Base API rate limit increase which is applied for an individual access token across the entire Skills Base API
     *   Contents of cache:
     *      sbTokenArray[0] = String token
     *      sbTokenArray[1] = Calendar object indicating token expiration
    */
    private Object[] sbTokenArray = new Object[2];

    private String grantType;
    private String clientId;
    private String clientSecret;
    private String drlPath;
    
    private DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private boolean sb_neverCheckForExistingAccred = true;
    
    //Variables for salesforce integration
    private static final String SF_USR = "sf_usr";
    private static final String SF_PWD = "sf_pwd";
    private static final String SF_LOGINURL = "sf_loginurl";
    private static final String SF_GRANTSERVICE = "sf_grantservice";
    private static final String SF_CLIENTID = "sf_clientid";
    private static final String SF_CLIENTSECRET = "sf_clientsecret";
    private String sfUsr;
    private String sfPwd;
    private String sfLoginUrl;
    private String sfGrantService;
    private String sfClientId;
    private String sfClientSecret;
    private String sfToken;
    private static final String ACTIVE = "Active";
    
    public AccreditationProcessBean() {
        tokenUrl = System.getProperty(SB_TOKEN_URL);
        if(StringUtils.isEmpty(tokenUrl))
            throw new RuntimeException("must set system property: "+SB_TOKEN_URL);

        sbPersonUrl = System.getProperty(SB_PERSON_URL);
        if(StringUtils.isEmpty(sbPersonUrl))
            throw new RuntimeException("must set system property: "+SB_PERSON_URL);

        sbQualificationUrl = System.getProperty(SB_Q_URL);
        if(StringUtils.isEmpty(sbQualificationUrl))
            throw new RuntimeException("must set system property: "+SB_Q_URL);

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

        String emString = System.getProperty(EXPIRED_MONTHS);
        if(StringUtils.isEmpty(emString))
            throw new RuntimeException("must set system property: "+EXPIRED_MONTHS);
        expiredMonths = Integer.parseInt(emString);

        String checkString = System.getProperty(SB_NEVER_CHECK_FOR_EXISTING_ACCRED);
        if(StringUtils.isEmpty(checkString))
            throw new RuntimeException("must set system property: "+SB_NEVER_CHECK_FOR_EXISTING_ACCRED);
        sb_neverCheckForExistingAccred = Boolean.parseBoolean(checkString);

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("init() \n    tokenUrl = "+tokenUrl);
        sBuilder.append("\n    sbPersonUrl = "+sbPersonUrl);
        sBuilder.append("\n    sbQualificationUrl = "+sbQualificationUrl);
        sBuilder.append("\n    clientId = "+clientId);
        sBuilder.append("\n    clientSecret = "+clientSecret);
        sBuilder.append("\n    grantType = "+grantType);
        sBuilder.append("\n    expiredMonths = "+expiredMonths);
        sBuilder.append("\n    sb_neverCheckForExistingAccred = "+sb_neverCheckForExistingAccred);
        logger.info(sBuilder.toString()); 

        sfLoginUrl = System.getProperty(SF_LOGINURL);
        if (StringUtils.isEmpty(sfLoginUrl))
            throw new RuntimeException("must set system property: " + SF_LOGINURL);

        sfUsr = System.getProperty(SF_USR);
        if (StringUtils.isEmpty(sfUsr))
            throw new RuntimeException("must set system property: " + SF_USR);

        sfPwd = System.getProperty(SF_PWD);
        if (StringUtils.isEmpty(sfPwd))
            throw new RuntimeException("must set system property: " + SF_PWD);

        sfClientId = System.getProperty(SF_CLIENTID);
        if (StringUtils.isEmpty(sfClientId))
            throw new RuntimeException("must set system property: " + SF_CLIENTID);

        sfClientSecret = System.getProperty(SF_CLIENTSECRET);
        if (StringUtils.isEmpty(sfClientSecret))
            throw new RuntimeException("must set system property: " + SF_CLIENTSECRET);

        sfGrantService = System.getProperty(SF_GRANTSERVICE);
        if (StringUtils.isEmpty(sfGrantService))
            throw new RuntimeException("must set system property: " + SF_GRANTSERVICE);

        StringBuilder sSFBuilder = new StringBuilder();
        sSFBuilder.append("init() \n    sfLoginUrl = " + sfLoginUrl);
        sSFBuilder.append("\n    sfClientId = " + sfClientId);
        sSFBuilder.append("\n    sfClientSecret = " + sfClientSecret);
        logger.info(sSFBuilder.toString());
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
    
    public List<Accreditation> selectStudentAccreditationsNotUploadedToSkillsBase() {
        List<Accreditation> sAccreds = canonicalDAO.selectStudentAccreditations(StudentAccreditation.NOT_UPLOADED, -1, Student.RED_HAT_EMAIL_SUFFIX, null);
        if(sAccreds == null || sAccreds.isEmpty()) {
            logger.info("selectStudentAccreditationsNotUploadedToSkillsBase() no StudentAccreditation objects found with status = "+StudentAccreditation.NOT_UPLOADED);
        }
        else {
            logger.info("selectStudentAccreditationsNotUploadedToSkillsBase() Will evaluate following # of StudentAccreditation objects: "+sAccreds.size());
        }
        return sAccreds;
    }
    
    // Method for selecting student list to push accreditation into salesforce
    public List<Accreditation> selectStudentAccreditationsNotUploadedToSfdc() {
        List<Accreditation> sAccreds = canonicalDAO.selectStudentAccreditations(-1, StudentAccreditation.NOT_UPLOADED, null, Student.RED_HAT_EMAIL_SUFFIX);
        if(sAccreds == null || sAccreds.isEmpty()) {
            logger.info("selectStudentAccreditationsNotUploadedToSfdc() no StudentAccreditation objects found with status = "+StudentAccreditation.NOT_UPLOADED);
        }
        else {
            logger.info("selectStudentAccreditationsNotUploadedToSfdc() Will evaluate following # of StudentAccreditation objects: "+sAccreds.size());
        }
        return sAccreds;
    }

    public List<Accreditation> selectStudentAccreditationByStudentId(@Body Integer studentId) {
        List<Accreditation> sAccreds = canonicalDAO.selectStudentAccreditationByStudentId(studentId, 0);
        if(sAccreds == null || sAccreds.isEmpty()) {
            logger.info("selectStudentAccreditationByStudentId() no StudentAccreditation objects found with status = "+StudentAccreditation.UNPROCESSED);
        }
        else {
            logger.info("selectStudentAccreditationByStudentId() Will evaluate following # of StudentAccreditation objects: "+sAccreds.size());
        }
        return sAccreds;
    }

    public List<Accreditation> filterDuplicateAccreds(Exchange exchange) {

        // List of student accreditations that may have already existed prior to accreditation rules having been executed for this student
        List<Accreditation> existingAccreds = (List<Accreditation>)exchange.getIn().getHeader(ORIGINAL_STUDENT_ACCREDS_HEADER);

        // List of student accreditations that were newly determined by accreditation rules in this transaction
        List<Accreditation> newAccreds = (List<Accreditation>)exchange.getIn().getBody();

        // List of new accreditations that will not be filtered out.
        // This list is returned at the conclusion of this function
        List<Accreditation> unfilteredAccreds = null;

        if(existingAccreds == null || (existingAccreds.size() < 1) ) {

            // No previous accreds, will exit this function and return entire list of newly geenerated accreds
            unfilteredAccreds = newAccreds;

        } else {
            unfilteredAccreds = new ArrayList<Accreditation>();
            
            //1) Change existingAccreds List into a Map keyed by Course
            Map<String, Accreditation> existingAccredMap = new HashMap<String, Accreditation>();
            for(Accreditation eAccred : existingAccreds) {
                existingAccredMap.put(eAccred.getCourseId(), eAccred);
            }
            String email = null;
            
            //2) iterate through new student accreds and determine whether any of them are duplicates of student accreds that are already known to GPTE Reporting
            for(Accreditation newAccred : newAccreds) {
                if(email == null) 
                    email = newAccred.getStudent().getEmail();
                
                String courseId = newAccred.getCourseId();
                Accreditation oldMatchAccred = existingAccredMap.get(courseId);
                if(oldMatchAccred == null)

                    // This is a new accred that does not have an existing match.
                    // Add to the list that is returned at conclusion of function
                    unfilteredAccreds.add(newAccred);

                else {

                    //3) Determine dates for both existing and new student accreds for date comparison in the next step
                    Date oldAccredDate = oldMatchAccred.getCompletionDate();
                    Calendar oldAccredCal = Calendar.getInstance();
                    oldAccredCal.setTime(oldAccredDate);
                    
                    Date newAccredDate = newAccred.getCompletionDate();
                    Calendar newAccredCal = Calendar.getInstance();
                    newAccredCal.setTime(newAccredDate);
                   
                    //4) Date comparison 
                    if(DateUtils.isSameDay(oldAccredCal, newAccredCal)) {
                        logger.info(email+" : "+ courseId+" : Same day for old and new accreds.  Will filter out");
                    }else {
                        logger.info(email +" : "+ courseId+" : More than one day difference between old and new accreds.  Will not filter out");
                        unfilteredAccreds.add(newAccred);
                    }
                }
            }
            StringBuilder sBuilder = new StringBuilder().append(email+" : \n\t# pre-filtered sAccreds = "+newAccreds.size());
            sBuilder.append("\n\t# post-filtered accreds = "+unfilteredAccreds.size());
            logger.info(sBuilder.toString());
        }
        return unfilteredAccreds; 
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
    
    public boolean shouldStudentAccreditationBePushedToSkillsBase(@Body Accreditation saObj) {
        if((saObj.getStudent().getEmail().indexOf(RED_HAT_SUFFIX) > 0) || (saObj.getStudent().getSkillsbasePartner() == Student.IS_SKILLSBASE_PARTNER) ) {
            return true;
        }else {
            logger.debug(saObj.getStudent().getEmail()+" : Neither a Red Hat associate nor eligible partner.  Will not update SkillsBase");
            return false;
        }
    }
    
    // Method to check student partner is salesforce
    public boolean shouldStudentAccreditationBePushedToSfdc(@Body Accreditation saObj) {
        if ((saObj.getStudent().getEmail().indexOf(RED_HAT_SUFFIX) < 0)
                && null != saObj.getAccreditation().getAccreditationid()
                && StringUtils.isNoneBlank(saObj.getStudent().getSalesforcefederationid())
                && null != saObj.getStudentAccred().getAccreditationdate()) {
            return true;
        } else {
            logger.info("===== Incomplete Data. Will not update Sfdc. Accreditationid="
                    + saObj.getAccreditation().getAccreditationid() + "StudentEmail=" + saObj.getStudent().getEmail());
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
        List<GPTEBaseCondition> studentCourses = (List<GPTEBaseCondition>) exchange.getIn().getHeader(STUDENT_COURSES_HEADER);
        JSONObject jObject = new JSONObject();
        if(studentCourses != null) {
            
            // 1)  add email
            GPTEBaseCondition firstCCObj = studentCourses.get(0);
            Student studentObj = firstCCObj.getStudent();
            jObject.put("email", studentObj.getEmail());
            
            // 2)  add List of CourseCompletion names
            List<String> courseCompletions = new ArrayList<String>();
            for(GPTEBaseCondition cc : studentCourses){
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
            //jObject.put("email", "Student not found");
        }
        
        exchange.getIn().setBody(jObject);
    }

/* ************************************************************************************ */















/*  *************               SkillsBase Integration              ******************  */

    // When using a ‘.split()’, all subsequent messages will have the same headers from the original message  
    public void resetSkillsBasePushResultsCache(Exchange exchange) {
        Map<Integer, String> skBasePushResults = (Map<Integer, String>)exchange.getIn().getHeader(SB_PUSH_RESULTS_CACHE);
        if(skBasePushResults == null) {
            logger.info("resetSkillsBasePushResultsCache() creating new map");
            skBasePushResults = new HashMap<Integer, String>();
        } else {
            logger.info("resetSkillsBasePushResultsCache() re-setting existing map");
            skBasePushResults.clear();
        }

        exchange.getIn().setHeader(SB_PUSH_RESULTS_CACHE, skBasePushResults);
    }

    public void determineStudentInSkillsBaseCache(Exchange exchange) {
        Map<Integer, String> skBasePushResults = (Map<Integer, String>)exchange.getIn().getHeader(SB_PUSH_RESULTS_CACHE);
        if(skBasePushResults != null) {
            Accreditation studentAccredObj = exchange.getIn().getBody(Accreditation.class);
            Student sObj = studentAccredObj.getStudent();
            Object entry = skBasePushResults.get(sObj.getStudentid());
            if(entry != null) {
                exchange.getIn().setHeader(SB_STUDENT_NOT_REGISTERED, SB_STUDENT_NOT_REGISTERED);
            }
        }
    }

    public void setStudentNotFoundOnSkillsBaseResultCache(Exchange exchange) {
        Map<Integer, String> skBasePushResults = (Map<Integer, String>)exchange.getIn().getHeader(SB_PUSH_RESULTS_CACHE);
        if(skBasePushResults != null) {
            Accreditation studentAccredObj = exchange.getIn().getBody(Accreditation.class);
            Student sObj = studentAccredObj.getStudent();
            logger.info(sObj.getEmail()+" : setStudentNotFoundOnSkillsBaseResultCache() Will not attempt to push any new quals to skillsbase for this student");
            skBasePushResults.put(sObj.getStudentid(), SB_STUDENT_NOT_REGISTERED);
        }
    }

    private void getSkillsBaseToken() throws SkillsBaseCommunicationException{
        
        String response = null;
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
            response = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonResponse = new JSONObject(response);
            String token = jsonResponse.getString(ACCESS_TOKEN);
            String expiresInString = jsonResponse.getString(EXPIRES_IN);
            int expiresInSeconds = Integer.parseInt(expiresInString);
            Calendar tokenExpDate = Calendar.getInstance();
            tokenExpDate.add(Calendar.SECOND, expiresInSeconds);

            sbTokenArray[0] = token;
            sbTokenArray[1] = tokenExpDate;
            StringBuilder sBuilder = new StringBuilder("getSkillsBaseToken() just retrieved the following skillsbase token:\n\t");
            sBuilder.append("token = "+token+"\n\t");
            sBuilder.append("expires at = "+sdfObj.format(tokenExpDate.getTime()));
            logger.info(sBuilder.toString());
        } catch (Exception exc) {
            handleSkillsBaseResponseException(exc, response);
            throw (new SkillsBaseCommunicationException());
        }
    }
    
    private void checkAccessToken() throws SkillsBaseCommunicationException {
        Calendar tenSecondsFromNow = Calendar.getInstance();
        tenSecondsFromNow.add(Calendar.SECOND, 10);

        Calendar tokenExpDate = (Calendar)sbTokenArray[1];
        if(tokenExpDate == null || tokenExpDate.before(tenSecondsFromNow) ) {
            this.getSkillsBaseToken();
        }
    }
    
    public void getSkillsBasePersonId(Exchange exchange) throws SkillsBaseCommunicationException {

        Message in = exchange.getIn();
        Accreditation studentAccredObj = in.getBody(Accreditation.class);
        String theEmail = studentAccredObj.getStudent().getEmail();
        if(StringUtils.isEmpty(theEmail))
            throw new RuntimeException("getSkillsBasePersonId() Accreditation object passed to this function must have a student email");

        logger.info(theEmail+" : Getting personId from Skills Base web service.");
        String response = null;
        
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient = WebClientDevWrapper.wrapClient(httpclient);
            HttpPost post = new HttpPost(sbPersonUrl+"search");

            // set up header
            checkAccessToken();
            post.setHeader("Authorization", "Bearer " + sbTokenArray[0]);
            
            // set up name value pairs
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("email", theEmail));

            // set HTTP request message body
            post.setEntity(new UrlEncodedFormEntity(nvps));

            // send POST message
            HttpResponse httpResponse = httpclient.execute(post);

            // process response
            response = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonResponse = new JSONObject(response);

            String status = jsonResponse.getString(SB_STATUS);
            
            logger.info(theEmail+" : getSkillsPersonId() SkillsBase response: " + response+" : status = "+status);

            // parse person id
            String personId = null;
            if (SUCCESS.equalsIgnoreCase(status)) {
                JSONArray jArray = jsonResponse.optJSONArray(SB_DATA);
                if(jArray != null && jArray.length() > 0) {
                    JSONObject jsonData = jsonResponse.optJSONArray(SB_DATA).getJSONObject(0);
                    personId = jsonData.getString("id");
                    studentAccredObj.getStudent().setSkillsbasePersonId(personId);
                    return;
                }
            }
            String message = jsonResponse.getString("message");

            if (message.contains(SB_NULL)) {
                studentAccredObj.getStudent().setSkillsbasePersonId(null);
            } else {
                throw new RuntimeException(message);
            }
            
        } catch (Exception exc) {
            handleSkillsBaseResponseException(exc, response);
            throw (new SkillsBaseCommunicationException());
        }
    }

    public void checkSkillsBaseForExistingAccred(Exchange exchange) throws SkillsBaseCommunicationException {
        Message in = exchange.getIn();
        Accreditation denormalizedStudentAccred = in.getBody(Accreditation.class);

        // Requirements regarding the need to check for existing SkillsBase qualifications are in flux.
        if(sb_neverCheckForExistingAccred) {
            denormalizedStudentAccred.setSkillsBaseQualExists(false);
            return;
        }
        
        Student studentObj = denormalizedStudentAccred.getStudent();
        AccreditationDefinition accredObj = denormalizedStudentAccred.getAccreditation();
        StudentAccreditation sAccredObj = denormalizedStudentAccred.getStudentAccred();
        String sEmail = studentObj.getEmail();
        String personId = studentObj.getSkillsbasePersonId();

        String accredName = accredObj.getAccreditationname();
        String response = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient = WebClientDevWrapper.wrapClient(httpclient);

            String getStudentQualUrl = sbQualificationUrl+"personId/"+personId;
            //logger.info(sEmail+" : checkSkillsBaseForExistingAccred() getStudentQualUrl = "+getStudentQualUrl);

            HttpGet get = new HttpGet(getStudentQualUrl);

            // set up header
            checkAccessToken();
            get.setHeader("Authorization", "Bearer " + sbTokenArray[0]);

            HttpResponse httpResponse = httpclient.execute(get);

            // process response
            long responseLength = httpResponse.getEntity().getContentLength();
            response = EntityUtils.toString(httpResponse.getEntity());
            StatusLine sLine = httpResponse.getStatusLine();
            logger.info(sEmail+" : skillsbase personId = "+personId+"\n\tstatusCode = "+sLine.getStatusCode()+"\n\tresponse content length = "+responseLength+"\n\tresponse reason phrase = "+sLine.getReasonPhrase()+"\n\tresponse: " + response);

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jArray = jsonResponse.optJSONArray(SB_DATA);
            if(jArray.length() < 1) {
                logger.warn(sEmail+" checkSkillsBaseForExistingAccred() no qualifications found");
            } else {
                for(int i=0; i < jArray.length(); i++) {
                    JSONObject jObj = jArray.getJSONObject(i);
                    String existingQual = jObj.getString(SB_NAME);
                    //log.info("testPersonQualificationsParsing() existingQual = "+existingQual+"\n\tobj = "+jObj.toString());
                    if(existingQual.equals(accredName)) {
                        denormalizedStudentAccred.setSkillsBaseQualExists(true);
                        return;
                    }
                }
            }

        } catch (Exception exc) {
            handleSkillsBaseResponseException(exc, response);
            throw (new SkillsBaseCommunicationException());
        }

    }
    
    public void postAccreditationToSkillsBase(Exchange exchange) throws SkillsBaseCommunicationException {

        Message in = exchange.getIn();
        Accreditation denormalizedStudentAccred = in.getBody(Accreditation.class);
        
        Student studentObj = denormalizedStudentAccred.getStudent();
        AccreditationDefinition accredObj = denormalizedStudentAccred.getAccreditation();
        StudentAccreditation sAccredObj = denormalizedStudentAccred.getStudentAccred();

        String accredName = accredObj.getAccreditationname();

        String response = null;
        
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient = WebClientDevWrapper.wrapClient(httpclient);

            HttpPost post = new HttpPost(sbQualificationUrl);
            logger.info(studentObj.getEmail() +" : Sending the following qualification to Skills Base web service : " + accredName);

            // set up header
            checkAccessToken();
            post.setHeader("Authorization", "Bearer " + sbTokenArray[0]);
            
            // set up name value pairs
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("name", accredName));
            nvps.add(new BasicNameValuePair("person_id", studentObj.getSkillsbasePersonId()));
            nvps.add(new BasicNameValuePair(SB_STATUS, Constants.COMPLETED));
            
            String startDateStr = dateFormatter.format(sAccredObj.getAccreditationdate());
            nvps.add(new BasicNameValuePair("start_date", startDateStr));
           
            Date accredEndDate = addMonthsToDate(sAccredObj.getAccreditationdate(), expiredMonths); 
            String endDateStr = dateFormatter.format(accredEndDate);
            nvps.add(new BasicNameValuePair("end_date", endDateStr));

            // set HTTP request message body
            post.setEntity(new UrlEncodedFormEntity(nvps));

            // send POST message
            HttpResponse httpResponse = httpclient.execute(post);

            // process response
            response = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonResponse = new JSONObject(response);

            String status = jsonResponse.getString(SB_STATUS);

            logger.info(studentObj.getEmail() +" : addQualification() \n\tendDate = "+endDateStr+"\n\tresponse: " + response+" : status = "+status);

            if (!"success".equalsIgnoreCase(status)) {
                String message = "Error sending qualification. Student email: " + studentObj.getEmail() + ",  qualification: " + accredName;
                logger.error(message);
            }
        } catch (Exception exc) {
            handleSkillsBaseResponseException(exc, response);
            throw (new SkillsBaseCommunicationException());
        }
    }
    
    /**
     * Method for generating and setting SalesForce token
     */
    public void setSfToken() {
        HttpClient httpclient = HttpClients.createDefault();

        // Assemble the login request URL
        String loginURL = sfLoginUrl + sfGrantService + "&client_id=" + sfClientId + "&client_secret=" + sfClientSecret
                + "&username=" + sfUsr + "&password=" + sfPwd;

        // Login requests must be POSTs
        HttpPost httpPost = new HttpPost(loginURL);
        HttpResponse response = null;

        try {
            // Execute the login POST request
            response = httpclient.execute(httpPost);
        } catch (ClientProtocolException cpException) {
            // Handle protocol exception
        } catch (IOException ioException) {
            // Handle system IO exception
        }

        // verify response is HTTP OK
        final int statusCode = response.getStatusLine().getStatusCode();
        logger.info("---- authStatusCode=" + statusCode);

        String resp = null;
        try {
            resp = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            // Handle system IO exception
        }
        JSONObject jsonObject = null;
        String authTokenOrSessionId = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(resp).nextValue();
            authTokenOrSessionId = jsonObject.getString("access_token");
        } catch (JSONException jsonException) {
            // Handle JSON exception
        }
        // release connection
        httpPost.releaseConnection();
        sfToken = authTokenOrSessionId;
    }

    /**
     * Method for posting Student Accreditation record to SalesForce
     */
    public void postAccreditationToSfdc(Exchange exchange) throws SkillsBaseCommunicationException {
        Message in = exchange.getIn();
        Accreditation denormalizedStudentAccred = in.getBody(Accreditation.class);

        Student studentObj = denormalizedStudentAccred.getStudent();
        AccreditationDefinition accredObj = denormalizedStudentAccred.getAccreditation();
        StudentAccreditation sAccredObj = denormalizedStudentAccred.getStudentAccred();
            String accredName = accredObj.getAccreditationname();
            String response = null;
            try {
                HttpClient httpclient = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(
                        sfLoginUrl + "/services/data/v42.0/sobjects/LMS_Training__c/External_ID__c/"
                                + accredObj.getAccreditationid());
                httpGet.setHeader("Content-Type", "application/json");
                setSfToken();
                httpGet.setHeader("Authorization", "Bearer " + sfToken);
                HttpResponse getResponse = httpclient.execute(httpGet);
                final int getStatusCode = getResponse.getStatusLine().getStatusCode();
                if (HttpStatus.SC_NOT_FOUND == getStatusCode) {
                    // set up SalesForce request
                    String accredDateStr = dateFormatter.format(sAccredObj.getAccreditationdate());
                    Date accredEndDate = addMonthsToDate(sAccredObj.getAccreditationdate(), expiredMonths);
                    String expirationDateStr = dateFormatter.format(accredEndDate);

                    boolean isActive = null != sAccredObj.getAccreditationtype()
                            && ACTIVE.equalsIgnoreCase(sAccredObj.getAccreditationtype());

                    StringEntity request = new StringEntity(
                            "{\"Federation_ID__c\":\"" + studentObj.getSalesforcefederationid() + "\", "
                                    + "\"Date_Achieved__c\":\"" + accredDateStr + "\", " + "\"Expiration_Date__c\":\""
                                    + expirationDateStr + "\", " + "\"is_Active__c\":\"" + isActive + "\"}");

                    HttpPatch httpPatch = new HttpPatch(
                            sfLoginUrl + "/services/data/v42.0/sobjects/LMS_Training__c/External_ID__c/"
                                    + accredObj.getAccreditationid());
                    httpPatch.setHeader("Content-Type", "application/json");
                    logger.info("---- Salesforce patchResquest=" + request.toString());
                    httpPatch.setEntity(request);
                    httpPatch.setHeader("Authorization", "Bearer " + sfToken);
                    // send POST message
                    HttpResponse httpResponse = httpclient.execute(httpPatch);

                    final int statusCode = httpResponse.getStatusLine().getStatusCode();
                    // process response
                    response = EntityUtils.toString(httpResponse.getEntity());
                    logger.info("---- Salesforce patchResponse=" + response);
                    if (statusCode != HttpStatus.SC_CREATED) {
                        String message = "Error pushing qualification to salesforce. Student email: "
                                + studentObj.getEmail() + ",  qualification: " + accredName + ", External_ID__c="
                                + accredObj.getAccreditationid() + ", Accreditationid="
                                + accredObj.getAccreditationid();
                        logger.error(message);
                    }
                    httpPatch.releaseConnection();
                } else {
                    logger.info("===== Record exists in salesforce. External_ID__c=" + accredObj.getAccreditationid()
                            + ", Student email: " + studentObj.getEmail() + ", qualification: " + accredName);

                }
                httpGet.releaseConnection();
            } catch (Exception exc) {
                handleSkillsBaseResponseException(exc, response);
                throw (new SkillsBaseCommunicationException());
            }
    }

    public void setPushedAccreditationToSfdc(@Body Accreditation accredObj ) {
        accredObj.getStudentAccred().setSalesforceuploaded(StudentAccreditation.PROCESSED_SALESFORCE_ONLY);;
    }

    private void handleSkillsBaseResponseException(Exception x, String skillsBaseResponse) {
        logger.error("handleSkillsBaseResponseException() skillsBaseResponse = "+skillsBaseResponse);
        x.printStackTrace();
    }


    private Date addMonthsToDate(Date originalDate, int monthsToAdd) {
        Calendar calObj = Calendar.getInstance();
        calObj.setTime(originalDate);
        calObj.add( Calendar.MONTH, monthsToAdd );
        return calObj.getTime();
    }

/*  ********************************************************************************************** */

}
