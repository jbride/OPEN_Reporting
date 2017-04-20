package com.redhat.gpe.coursecompletion.service;

import com.redhat.gpe.coursecompletion.dokeos.DokeosCourseCompletion;
import com.redhat.gpe.coursecompletion.sumtotal.SumtotalCourseCompletion;
import com.redhat.gpe.coursecompletion.dao.TotaraShadowDAO;
import com.redhat.gpe.domain.canonical.*;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpte.services.ExceptionCodes;
import com.redhat.gpte.services.GPTEBaseServiceBean;
import com.redhat.gpte.services.InvalidCourseException;
import com.redhat.gpte.services.InvalidStudentException;
import com.redhat.gpe.coursecompletion.domain.TotaraCourseCompletion;

import org.apache.camel.Body;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Producer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class CourseCompletionServiceBean extends GPTEBaseServiceBean {

    private static final String COURSE_REFRESH_COUNTER = "COURSE_REFRESH_COUNTER";    
    private static final String ENGLISH = "EN_US";
    private static final String SUMTOTAL_COMPLETED = "COMPLETED";
    private static final byte coursePassingValue = 70;
    private static final String ISSUES_SUFFIX="_issues.txt";
    private static final String COURSE_COMPLETION_ISSUES_SUFFIX="_completion_validation_issues.txt";

    public static final String DETERMINE_COMPANY_ID_AND_PERSIST_COMPANY = "vm:determineCompanyIdAndPersistCompanyIfNeedBe";
    private static final String CC_APPEND_COURSE_ISSUES_TO_FILE = "cc_append_course_issues_to_file";
    private static final String COURSE_ISSUES_OUTPUT_DIR="/tmp/gpte/courseCompletionIssues";
    private static final String COURSE_ISSUES_HEADER = "Activity Code,Activity Name";
    private static final String SUMTOTAL_REJECTION_CODES_FILE = "sumtotal_codes_to_reject.txt";

    private static final String CC_APPEND_STUDENT_ISSUES_TO_FILE = "cc_append_student_issues_to_file";
    private static final String STUDENT_ISSUES_OUTPUT_DIR="/tmp/gpte/studentIssues";
    private static final String STUDENT_ISSUES_OUTPUT = "gpte_student_issues.txt";

    private static final String COURSE_ROWS_AFFECTED = "COURSE_ROWS_AFFECTED";
    private static final String COURSE_MAPPING_ROWS_AFFECTED = "COURSE_MAPPING_ROWS_AFFECTED";

    private static final String COMMA = ",";
    private static final String TAB = "\\t";
    private static final String SINGLE_TAB = "\t";

    private static final String TOTARA_COURSE_COMPLETION_LIMIT = "TOTARA_COURSE_COMPLETION_LIMIT";

    private static final String TOTARA_LOW_CC_ID="LOW_CC_ID";
    private static final String TOTARA_HIGH_CC_ID="HIGH_CC_ID";

    private Logger logger = Logger.getLogger(getClass());
    private boolean cc_append_course_issues_to_file = true;
    private boolean cc_append_student_issues_to_file = false;
    private File studentIssuesFile = null;
    private Map<String, Course> courseMapTempCache = new HashMap<String, Course>();  // <mappedCourseId, Course obj>
    
    private static Object synchObj = new Object();
    private static final String underscoreFilter="_$";
    private static final String dashFilter="-$";
    private static String langFilter;
    private static int rhtCompanyId = 0;
    private static Set sumtotalRejectCodeSet = new HashSet<String>();
    private File courseIssuesDir = new File(COURSE_ISSUES_OUTPUT_DIR);

    @Autowired
    protected TotaraShadowDAO totaraShadowDAO;
    
   
    public CourseCompletionServiceBean() throws IOException {


        // 1) make sure COURSE_ISSUES_OUTPUT_DIR is created on filesystem
        courseIssuesDir.mkdirs();

        // 2) sees a STUDENT_ISSUES_OUTPUT file on filesystem
        File studentIssuesDir = new File(STUDENT_ISSUES_OUTPUT_DIR);
        studentIssuesDir.mkdirs();
        String x = System.getProperty(CC_APPEND_STUDENT_ISSUES_TO_FILE);
        if(StringUtils.isNotEmpty(x)){
            this.cc_append_student_issues_to_file = Boolean.parseBoolean(x);
            if(cc_append_student_issues_to_file) {
                studentIssuesFile = new File(STUDENT_ISSUES_OUTPUT_DIR, STUDENT_ISSUES_OUTPUT);
                studentIssuesFile.createNewFile();
                FileOutputStream fStream = null;
                try {
                    fStream = new FileOutputStream(studentIssuesFile);
                    String output = "Email,CompanyName";
                    fStream.write(output.getBytes());
                    fStream.flush();
                } finally {
                    if(fStream != null)
                        fStream.close();
                }
                logger.info("CourseCompletionServiceBean:  appending student issues to: "+studentIssuesFile.getAbsolutePath());
            }
        }

        // 3) read sumtotal rejection codes file and populate a Set 
        InputStream iStream = null;
        try {
            iStream = this.getClass().getClassLoader().getResourceAsStream("/"+SUMTOTAL_REJECTION_CODES_FILE); // works in JEE server
            if(iStream == null) {
                iStream = this.getClass().getClassLoader().getResourceAsStream(SUMTOTAL_REJECTION_CODES_FILE); // works during maven tests
                if(iStream == null) 
                    throw new RuntimeException("Unable to locate the following file: "+SUMTOTAL_REJECTION_CODES_FILE);
            }

            BufferedReader r = new BufferedReader(new InputStreamReader(iStream));
            String line;
            while ((line=r.readLine()) != null) {
                sumtotalRejectCodeSet.add(line);
            }

        } finally {
            if(iStream != null)
                iStream.close();
        }
        logger.info("CourseCompletionServiceBean: # of sumtotal code rejections = "+sumtotalRejectCodeSet.size());
        
    }
    
    public void setLangFilter() {
        if(langFilter == null) {
            synchronized(synchObj) {
                if(langFilter == null) {
                    List<Language> langs = canonicalDAO.getLanguages();
                    StringBuilder sBuilder = new StringBuilder();
                    sBuilder.append("(");
                    int y = 0;
                    for(Language lang : langs){
                        sBuilder.append(lang.getLanguageid());
                        sBuilder.append("$");
                        if(y < langs.size() - 1) {
                            sBuilder.append("|");
                            y++;
                        }else {
                            break;
                        }
                    }
                    sBuilder.append(")");
                    langFilter = sBuilder.toString();
                    logger.info("setLangFilter(): langFilter = "+langFilter);
                }
            }
        }
    }
    

/* ***********      Student    ************************ */
    public void insertNewStudentGivenSumtotalCourseCompletion(Exchange exchange) throws Exception {
        SumtotalCourseCompletion stCourseCompletion = (SumtotalCourseCompletion)exchange.getIn().getBody();
        Student sObj = new Student();
        sObj.setEmail(stCourseCompletion.getEmail());
        sObj.setFirstname(stCourseCompletion.getFirstName());
        sObj.setLastname(stCourseCompletion.getLastName());
        sObj.setCompanyName(stCourseCompletion.getOrganization());

        String uNumber = stCourseCompletion.getUserNumber();
        if(uNumber.length() > 3 ) {
            sObj.setSumtotalid(uNumber);
            sObj.setSalesforcecontactid(uNumber.substring(0,uNumber.length() - 3));
        }
        sObj.setIpaStatus(1);
        sObj.setCountry(stCourseCompletion.getCountry());
        sObj.setRegion(stCourseCompletion.getRegion());

        insertNewStudent(exchange, sObj);
    }
    
    public void insertNewStudentGivenDokeosCourseCompletion(Exchange exchange) throws Exception {
        
        DokeosCourseCompletion dokeosCourseCompletion = (DokeosCourseCompletion) exchange.getIn().getBody();
        Student sObj = new Student();
        sObj.setEmail(dokeosCourseCompletion.getEmail());
        sObj.setFirstname(dokeosCourseCompletion.calculateFirstName());
        sObj.setLastname(dokeosCourseCompletion.calculateLastName());
        sObj.setIpaStatus(1);
        insertNewStudent(exchange, sObj);
    }
    
    public void insertNewStudentGivenTotaraCourseCompletion(Exchange exchange) throws Exception {
        
        TotaraCourseCompletion tCourseCompletion = (TotaraCourseCompletion) exchange.getIn().getBody();
        Student sObj = new Student();
        sObj.setEmail(tCourseCompletion.getEmail());
        sObj.setFirstname(tCourseCompletion.getStudentFirstName());
        sObj.setLastname(tCourseCompletion.getStudentLastName());
        sObj.setIpaStatus(1);
        insertNewStudent(exchange, sObj);
    }
    
    private void insertNewStudent(Exchange exchange, Student studentIn) throws Exception {
        int companyId = 0;
        String studentEmail = studentIn.getEmail();

        // 0)  Exchange should return from this function with the same body it came in with
        Object origBody = exchange.getIn().getBody();
        
        // 1)  Determine a companyId that the student is affiliated with
        if(studentEmail.indexOf(RED_HAT_SUFFIX) > 0) {
            
            // 1.1) If RHT student, then identify (and cache) companyId for Red Hat
            if(this.rhtCompanyId == 0)
                companyId = canonicalDAO.getCompanyID(Company.RED_HAT_COMPANY_NAME);
            else
                companyId = this.rhtCompanyId;
        } else {
            // TO-DO:  https://github.com/redhat-gpe/OPEN_Reporting/issues/40
            
            // 1.2)  If not RHT student, then query GPTE LDAP for student info (to include company name)
            CamelContext cContext = exchange.getContext();
            Endpoint endpoint = cContext.getEndpoint(DETERMINE_COMPANY_ID_AND_PERSIST_COMPANY);
            exchange.setPattern(ExchangePattern.InOut);
            Message in = exchange.getIn();
            in.setBody(studentIn);
            Producer producer = null;
            Exchange getCompanyExchange = null;
            try {
                producer = endpoint.createProducer();
                producer.start();
                getCompanyExchange = producer.createExchange();
                getCompanyExchange.setPattern(ExchangePattern.InOut);
                getCompanyExchange.getIn().setBody(studentIn);
                getCompanyExchange.getIn().setHeader(QUERY_LDAP, "TRUE");
                getCompanyExchange.getIn().setHeader(UPDATE_COMPANY, "FALSE");
                producer.process(getCompanyExchange);
                Student studentOut = (Student)getCompanyExchange.getIn().getBody();
                
                //1.3 setCompanyId (used downstream to persist student) from response from remote service 
                companyId = studentOut.getCompanyid();
                
                if(companyId == 0){
                  
                    // 1.4)  Not good.  Not able to identify a companyId for this student
                    /*
                    if(cc_append_student_issues_to_file ) {
                        String output = "\n"+studentEmail+","+studentIn.getCompanyName();
                        Files.write(Paths.get(studentIssuesFile.getAbsolutePath()), output.getBytes(), StandardOpenOption.APPEND);
                    }
                    StringBuilder sBuilder = new StringBuilder("insertNewStudent() not able to identify company information for this student.");
                    sBuilder.append("\n\temail: "+studentEmail);
                    throw new RuntimeException(sBuilder.toString());
                    */
                    // https://github.com/redhat-gpe/OPEN_Reporting/issues/113
                    companyId = Company.COMPANY_UNKNOWN_ID;
                }
            } finally {
                try {
                    if(producer != null)
                        producer.stop();
                } catch(Exception y) {
                    y.printStackTrace();
                }
            }
        }
        
        // 2)  persist student
        studentIn.setCompanyid(companyId);
        canonicalDAO.updateStudent(studentIn);

        // reset exchange with original body
        exchange.getIn().setBody(origBody);
    } 
/**
 * @throws IOException ******************************************************************/
    
    
    

/* **************       Student Courses        ********************** */
   
    /*
     * Given a collection of SumtotalCourseCompletion objects, iterates and validates each one
     */ 
    public void validateSumtotalCourseCompletions(Exchange exchange) throws IOException {

        // 1)  make sure what is used in this function is a Collection<SumtotalCourseCompletion> 
        Object body = exchange.getIn().getBody();
        Collection<SumtotalCourseCompletion> sCourseCompletions = null;
        if(body instanceof SumtotalCourseCompletion) {
            sCourseCompletions = new ArrayList<SumtotalCourseCompletion>();
            sCourseCompletions.add((SumtotalCourseCompletion)body);
        } else {
            sCourseCompletions = (Collection<SumtotalCourseCompletion>)exchange.getIn().getBody();
        }

        // 2)  Instantiate new course issues file
        //     Contents of this file contain a list of all invalid courses encountered while processing this collection of sumtotalcoursecompletion objects
        String issueFileName = (String)exchange.getIn().getHeader(CAMEL_FILE_NAME);
        File courseIssuesFile = new File(COURSE_ISSUES_OUTPUT_DIR, issueFileName+ISSUES_SUFFIX);
        if(!courseIssuesDir.exists()) {
            courseIssuesDir.mkdirs();
        }
        courseIssuesFile.createNewFile();
        FileOutputStream fStream = null;
        try {
            fStream = new FileOutputStream(courseIssuesFile);
            fStream.write(COURSE_ISSUES_HEADER.getBytes());
            fStream.flush();
        } finally {
            if(fStream != null)
                fStream.close();
        }

        // 2.5)  Instantiate new course completion validation issues file
        File cCompletionIssuesFile = new File(COURSE_ISSUES_OUTPUT_DIR, issueFileName+COURSE_COMPLETION_ISSUES_SUFFIX);
        if(cCompletionIssuesFile.exists())
            cCompletionIssuesFile.delete();
        cCompletionIssuesFile.createNewFile();
        
        int i = 2;
        boolean invalidCourseExceptionThrown = false;

        // 3) this is the datastructure of validated course completions that is passed to downstream business logic
        Collection<SumtotalCourseCompletion> prunedCourseCompletions = new ArrayList<SumtotalCourseCompletion>();

        // 3.5) count the # of rejected course completions
        int rejectCount = 0;

        int courseValidationProblems = 0;
        int unknownCourseProblems = 0;

        // 4) iterate via each unvalidated sumtotal course completion
        for(SumtotalCourseCompletion stCC : sCourseCompletions) {

            try {
                stCC.validate();
            }catch(Exception t) {
                logger.error(t.getMessage());
                Files.write(Paths.get(cCompletionIssuesFile.getAbsolutePath()), t.getMessage().getBytes(), StandardOpenOption.APPEND);
                courseValidationProblems++;
                continue;
            }

            String aCode = stCC.getActivityCode();
            if(!StringUtils.isEmpty(aCode)) {

                // 5)  filter out course completions that have been identified as bogus as per sumtotal LMS team
                if(sumtotalRejectCodeSet.contains(aCode)){
                    rejectCount++;
                    continue;
                }
            
                try {
                    // 6) identify the canonical course specified in the sumtotal activity code
                    //    if not a known course, then catch exception and prevent further processing on this course completion
                    Course course = getCourseFromSumtotalCompletion(stCC, courseIssuesFile);
                    prunedCourseCompletions.add(stCC);
                }catch(InvalidCourseException x) {
                    invalidCourseExceptionThrown = true;
                    unknownCourseProblems++;
                }
            }else {
                logger.error("validateSumtotalCourseCompletion() passed an empty activity code");
            }
            i++;
        }
        if(invalidCourseExceptionThrown)
            logger.error("validateSumtotalCourseCompletion() issues with Sumtotal activity codes found in the following file: "+courseIssuesFile.getAbsolutePath());
        else {
            logger.info("validateSumtotalCourseCompletions() all activity codes from the following # of sumtotal course completions are found to have corresponding course Ids in GPTE database: "+prunedCourseCompletions.size());
        }

        // 7) set prunedCourseCompletions to exchange body for further downstream processing
        exchange.getIn().setBody(prunedCourseCompletions);

        StringBuilder sBuilder = new StringBuilder("\n********** validateSumtotalCourseCompletions report:   **********\n");
        sBuilder.append("# of initial course completions  = "+sCourseCompletions.size());
        sBuilder.append("\n# of rejected course completions = "+rejectCount);
        sBuilder.append("\n# of course validation problems = "+courseValidationProblems);
        sBuilder.append("\n# of unknownCourseProblems = "+unknownCourseProblems);
        sBuilder.append("\n# of course completions to persist = "+prunedCourseCompletions.size());
        sBuilder.append("\n****************************************\n");
        logger.info(sBuilder.toString());
    }

    private Course getCourseFromSumtotalCompletion(SumtotalCourseCompletion stCC, File courseIssuesFile) throws IOException, InvalidCourseException {
        if(StringUtils.isEmpty(this.langFilter)){
            this.setLangFilter();
        }
        String aCode = stCC.getActivityCode();
        if(StringUtils.isEmpty(aCode))
            throw new InvalidCourseException();

        aCode = aCode.replaceAll(this.langFilter, "");
        aCode = aCode.replaceAll(this.underscoreFilter, "");
        aCode = aCode.replaceAll(this.dashFilter, "");
        stCC.setActivityCode(aCode);
        
        Course course = null;
        if(courseMapTempCache.containsKey(stCC.getActivityCode())){
            course = courseMapTempCache.get(stCC.getActivityCode());
        } else {
            try {
                course = canonicalDAO.getCourseByCourseId(stCC.getActivityCode());
                this.courseMapTempCache.put(stCC.getActivityCode(), course);
            } catch(org.springframework.dao.EmptyResultDataAccessException x) {
                //courseMapTempCache.put(stCC.getActivityCode(), null);
                if(cc_append_course_issues_to_file ) {
                    String output = "\n"+stCC.getActivityCode()+","+stCC.getActivityName();
                    Files.write(Paths.get(courseIssuesFile.getAbsolutePath()), output.getBytes(), StandardOpenOption.APPEND);
                }
                throw new InvalidCourseException("Unable to locate a course with the following course Id: \""+stCC.getActivityCode()+"\"");
            }
        }
        return course;
    }

    public Course getCourseByCourseName(@Body String courseName) {
        return canonicalDAO.getCourseByCourseName(courseName, null);
    }
    
    public CourseCompletion convertSumtotalCourseCompletionToStudentCourse(Exchange exchange) throws IOException, InvalidCourseException {

        SumtotalCourseCompletion stCC = (SumtotalCourseCompletion)exchange.getIn().getBody();
        
        // 1)  Identify student
        Student student = canonicalDAO.getStudentByEmail(stCC.getEmail()); // throws org.springframework.dao.EmptyResultDataAccessException

        // 2)  Identify canonical course name given a sumtotal "activity code"
        logger.info(stCC.getEmail()+" : converting from sumtotal course completion to canonical StudentCourse. ActivityCode = "+stCC.getActivityCode());
        Course course = getCourseFromSumtotalCompletion(stCC, null);
        
        // 3)  Create StudentCourse object
        Language language = new Language();
        language.setLanguageid(ENGLISH);
        StudentCourse sCourse = new StudentCourse();
        sCourse.setStudentid(student.getStudentid());
        sCourse.setCourseid(course.getCourseid());
        sCourse.setLanguageid(language.getLanguageid());
        sCourse.setAssessmentdate(new Timestamp(stCC.getAttemptEndDate().getTime()));
        
        String dSuccess = stCC.getCompletionStatus();
        if(StringUtils.isNotEmpty(dSuccess)) {
            if(dSuccess.contains(SUMTOTAL_COMPLETED))
                dSuccess = StudentCourse.ResultTypes.Pass.name();
            else
                dSuccess = StudentCourse.ResultTypes.Fail.name();
        }else {
            //throw new RuntimeException("No value for Sumtotal CourseCompletion Status for email = "+student.getEmail()+" : courseName = "+course.getCoursename());
            logger.warn("No value for Sumtotal CourseCompletion Status for email = "+student.getEmail()+" : courseName = "+course.getCoursename());
            dSuccess = StudentCourse.ResultTypes.Pass.name();
        }
        sCourse.setAssessmentresult(dSuccess);
        
        // 4)  Transform into canonical CourseCompletion
        CourseCompletion dStudentCourse = new CourseCompletion(student, course, language, sCourse);
        return dStudentCourse;
    }
    
    public void setSumtotalProcessingExceptionsToBody(Exchange exchange) throws java.io.IOException {

        String issueFileName = (String)exchange.getIn().getHeader(CAMEL_FILE_NAME);
        File courseIssuesFile = new File(COURSE_ISSUES_OUTPUT_DIR, issueFileName+ISSUES_SUFFIX);
        if(! courseIssuesFile.exists())
            throw new RuntimeException("setSumtotalProcessingExceptions() no file found at: "+courseIssuesFile.getAbsolutePath());

        String courseIssues = FileUtils.readFileToString(courseIssuesFile);
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("Sumtotal Course Completion Processing\n\n");
        if(COURSE_ISSUES_HEADER.equals(courseIssues)) {
            sBuilder.append("All Sumtotal Activity Codes are valid");
        }else {
            sBuilder.append("Unknown Sumtotal Activity Codes as follows:\n\n");
            sBuilder.append(courseIssues);
            sBuilder.append("\n\nPlease work with GPTE ops team to register these unknown Sumtotal Activity Codes with GPTE Reporting:\n\n");
        }
        exchange.getIn().setBody(sBuilder.toString());

    }
    
    public CourseCompletion convertDokeosCourseCompletionToStudentCourse(Exchange exchange) throws com.redhat.gpte.services.InvalidCourseException {
        DokeosCourseCompletion dokeosCourseCompletion = (DokeosCourseCompletion)exchange.getIn().getBody();
        if(StringUtils.isEmpty(dokeosCourseCompletion.getEmail()))
            throw new RuntimeException(ExceptionCodes.GPTE_CC1001+dokeosCourseCompletion.toString());
        
        logger.info(dokeosCourseCompletion.getEmail()+" : converting from dokeos course completion to canonical StudentCourse");

        // If student not found, throws: org.springframework.dao.EmptyResultDataAccessException
        Student student = canonicalDAO.getStudentByEmail(dokeosCourseCompletion.getEmail());
        
        // https://github.com/redhat-gpe/OPEN_Reporting/issues/37
        dokeosCourseCompletion.pruneQuizName();
        
        Course course = null;
        try {
            course = canonicalDAO.getCourseByCourseName(dokeosCourseCompletion.getQuizName(), DokeosCourseCompletion.COURSE_COMPLETION_MAPPING_NAME);
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            StringBuilder sBuilder = new StringBuilder(ExceptionCodes.GPTE_CC1001+"Unable to locate a course with the following course name: \""+dokeosCourseCompletion.getQuizName()+"\"");
            sBuilder.append("\nCourse completion info as follows:");
            sBuilder.append("\n\temail: "+dokeosCourseCompletion.getEmail());
            sBuilder.append("\n\tassessmentDate: "+dokeosCourseCompletion.getAssessmentDate());
            exchange.setException(new Exception(sBuilder.toString()));
            throw new com.redhat.gpte.services.InvalidCourseException(sBuilder.toString());
        }
        Language language = new Language();
        language.setLanguageid(ENGLISH);
        StudentCourse sCourse = new StudentCourse();
        sCourse.setStudentid(student.getStudentid());
        sCourse.setCourseid(course.getCourseid());
        sCourse.setLanguageid(language.getLanguageid());
        
        
        try {
            sCourse.setAssessmentdate(convertDokeosAssessmentDateToCanonical(dokeosCourseCompletion.getAssessmentDate()));
        } catch(ParseException x) {
            throw new RuntimeException("Not able to parse the following dokeos date for: email = "+student.getEmail()+" : courseName = "+course.getCoursename()+" : date = "+dokeosCourseCompletion.getAssessmentDate());
        }
        
        byte canonicalScore = convertDokeosAssessmentScoreToCanonical(dokeosCourseCompletion.getScore());
        sCourse.setAssessmentscore(canonicalScore);
        
        String dSuccess = null;
        if(canonicalScore >= coursePassingValue)
            dSuccess = StudentCourse.ResultTypes.Pass.name();
        else
            dSuccess = StudentCourse.ResultTypes.Fail.name();
        sCourse.setAssessmentresult(dSuccess);
        
        CourseCompletion dStudentCourse = new CourseCompletion(student, course, language, sCourse);
        return dStudentCourse;
    }
    
    private Timestamp convertDokeosAssessmentDateToCanonical(String dateString) throws ParseException {
        Date dokeosDate = DokeosCourseCompletion.dokeosSDF.parse(dateString);
        return new Timestamp(dokeosDate.getTime());
    }
    
    private byte convertDokeosAssessmentScoreToCanonical(String dScore) {
        String tempScore = dScore.substring(0, dScore.indexOf("%"));
        byte tempByte = Byte.valueOf(tempScore);
        return tempByte;
    }
    
    public void addStudentCourseToDB(@Body CourseCompletion sCourseWrapper) {
        StudentCourse scObj = sCourseWrapper.getStudentCourse();
        if(canonicalDAO.getUniqueStudentCourseCount(scObj) < 1 ) {
            canonicalDAO.addStudentCourse(scObj);
        }else {
            logger.warn("addStudentCourseDB() Student Course already exists with following attributes: "+scObj.getStudentid()+" : " + scObj.getCourseid() +" : "+ scObj.getAssessmentdate());
        }
    }
    
    public boolean isNewStudentCourseForStudent(@Body StudentCourse sCourse) {
        boolean result = canonicalDAO.isNewStudentCourseForStudent(sCourse);
        return result;
    }

/* ****************************************************************************** */




/* **************           Courses and CourseMappings              *************   */


    public void deleteAllFromCourseMappings(Exchange exchange) {
        int result = canonicalDAO.deleteAllFromCourseMappings();
        exchange.getIn().setHeader(COURSE_MAPPING_ROWS_AFFECTED, new Integer(result).toString());
    }

    public void processCourseRefreshSpreadsheetRecord(Exchange exchange) {
        String ssRecord = (String)exchange.getIn().getBody();

        try {
            if(StringUtils.isEmpty(ssRecord) || (ssRecord.indexOf(COURSE_MAPPINGS_FIRST_LINE) > 0) || ssRecord.startsWith(SINGLE_TAB) || ssRecord.equals("}") )
                return;

            Integer counter = (Integer)exchange.getProperty("CamelSplitIndex");

            //logger.info("processCourseRefreshSpreadsheetRecord() ssRecord = "+ssRecord);
            String[] courseMFields = ssRecord.split(TAB);
            String prunedMappedName = courseMFields[0].trim();
            String courseId = courseMFields[1].trim();
            String courseName = courseMFields[2].trim();

            // Allow prunedMapped Name to be null, a mapping to a courseId is not mandatory
            if( StringUtils.isEmpty(courseId) || StringUtils.isEmpty(courseName) )
                throw new RuntimeException(counter+" : processCourseRefreshSpreadsheetRecord() empty field: "+prunedMappedName+" : "+courseId+" : "+courseName);

            canonicalDAO.insertIntoCourseAndMappings(courseId, courseName, prunedMappedName);
 
            //logger.info(counter+" processCourseRefreshSpreadsheetRecord() ssRecord = "+ssRecord);
        }catch(Exception x) {
            logger.error("processCourseRefreshSpreadsheetRecord() ssRecord = \""+ssRecord+"\"");
            x.printStackTrace();
        }
    }

    public int getMostRecentTotaraCourseCompletionId() {
        return canonicalDAO.getMostRecentTotaraCourseCompletionId();
    }
   
    public int testTotaraJDBCConnection() {
        return totaraShadowDAO.testTotaraJDBCConnection();
    }

    public  List<TotaraCourseCompletion> getLatestTotaraCourseCompletions(Exchange exchange) {
        int latestKnownCC = (Integer)exchange.getIn().getBody();
        int totaraCourseCompletionLimit = -1;
        String headerVal = (String)exchange.getIn().getHeader(TOTARA_COURSE_COMPLETION_LIMIT);
        if(headerVal != null)
            totaraCourseCompletionLimit = Integer.parseInt(headerVal);

        List<TotaraCourseCompletion> totaraCourseCompletions =  totaraShadowDAO.getLatestCourseCompletions(latestKnownCC, totaraCourseCompletionLimit);
        logger.info("getLatestTotaraCourseCompletions() # of totaraCourseCompletions = "+totaraCourseCompletions.size());
        return totaraCourseCompletions;
    }

    public List<TotaraCourseCompletion> getTotaraCourseCompletionsByRange(Exchange exchange) {
        int lowId;
        int highId;
        Object lowIdObject = exchange.getIn().getHeader(TOTARA_LOW_CC_ID);
        if(lowIdObject == null)
            throw new RuntimeException("getTotaraCourseCompletionsByRange() please ensure that the following header is set: "+TOTARA_LOW_CC_ID );
        else {
            if(lowIdObject instanceof String) {
                lowId = Integer.parseInt((String)lowIdObject);
            } else {
                lowId = (Integer)lowIdObject;
            }
        }

        Object highIdObject = exchange.getIn().getHeader(TOTARA_HIGH_CC_ID);
        if(highIdObject == null)
            throw new RuntimeException("getTotaraCourseCompletionsByRange() please ensure that the following header is set: "+TOTARA_HIGH_CC_ID );
        else {
            if(highIdObject instanceof String) {
                highId = Integer.parseInt((String)highIdObject);
            } else {
                highId = (Integer)highIdObject;
            }
        }

        if(lowId < 1 || highId < 1 || highId < lowId)
            throw new RuntimeException("getTotaraCourseCompletionsByRange() please ensure that lowId and highId are set and that highId >= lowId "+lowId+" : "+highId);

        List<TotaraCourseCompletion> totaraCourseCompletions =  totaraShadowDAO.getCourseCompletionsByRange(lowId, highId);
        logger.info("getTotaraCourseCompletionsByRange() # of totaraCourseCompletions = "+totaraCourseCompletions.size());
        return totaraCourseCompletions;
    }

    public CourseCompletion convertTotaraCourseCompletion(Exchange exchange) throws InvalidStudentException, InvalidCourseException {
        TotaraCourseCompletion tCC = (TotaraCourseCompletion)exchange.getIn().getBody();
        
        Student studentObj = null;
        try {
            studentObj = canonicalDAO.getStudentByEmail(tCC.getEmail()); 
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            throw new InvalidStudentException(tCC.getEmail());
        }
        
        Course courseObj = null;
        try {
            courseObj = canonicalDAO.getCourseByCourseName(tCC.getCourseFullName(), null);
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            throw new InvalidCourseException(tCC.getCourseFullName());
        }
        
        Language langObj = new Language();
        langObj.setLanguageid(Language.EN_US);
        
        StudentCourse sCourseObj = new StudentCourse();
        sCourseObj.setAssessmentdate( new Timestamp( new Date().getTime()) );
        sCourseObj.setAssessmentresult(StudentCourse.ResultTypes.Pass.toString());
        sCourseObj.setAssessmentscore((byte) 100);
        sCourseObj.setCourseid(courseObj.getCourseid());
        sCourseObj.setLanguageid(langObj.getLanguageid());
        sCourseObj.setStudentid(studentObj.getStudentid());

        CourseCompletion ccObj = new CourseCompletion();
        ccObj.setStudent(studentObj);
        ccObj.setLanguage(langObj);
        ccObj.setStudentCourse(sCourseObj);
        return ccObj;
    }
 
}
