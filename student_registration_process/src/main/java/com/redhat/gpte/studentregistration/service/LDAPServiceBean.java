package com.redhat.gpte.studentregistration.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.helper.DenormalizedStudent;
import com.redhat.gpte.services.AttachmentValidationException;
import com.redhat.gpte.services.GPTEBaseServiceBean;
import com.redhat.gpe.studentregistration.studentreg.StudentRegistrationBindy;
import com.redhat.gpte.studentregistration.util.InvalidAttributeException;
import com.redhat.gpte.services.ExceptionCodes;


/* 
 * Currently uses connection pooling from JNDI API.
 * Other alternative java based LDAP tools to consider listed here:  http://directory.apache.org/api/java-api.html
 */
@SuppressWarnings("restriction")
public class LDAPServiceBean extends GPTEBaseServiceBean {
    
    private static final String ATTACHMENT_VALIDATION_EXCEPTIONS = "ATTACHMENT_VALIDATION_EXCEPTIONS";
    public static final String MAIL = "mail=";
    public static final String COMMA = ",";
    public static final String BASE_CTX_DN = "cn=users,cn=accounts,dc=opentlc,dc=com";
    public static final String IPA_PROVIDER_URL = "ipa.provider.url";
    public static final String IPA_SECURITY_PRINCIPAL = "ipa.security.principal";
    public static final String IPA_SECURITY_CREDENTIALS = "ipa.security.credentials";
    private static final String OPEN_PAREN = "(";
    private static final String CLOSE_PAREN = ")";
    private static final String TITLE_ATTRIBUTE = "title";
    private static final String GEO_ATTRIBUTE_PREFIX = "cn=rhpds-geo-";
    private static final String PARTNER_ATTRIBUTE_PREFIX = "cn=rhpds-partner";
    public static final int GPTE_QUERY_SIZE_LIMIT = 2000;
    public static final String GPSETRAINING_WILDCARD = "gpsetraining*";
    private static final String MEMBEROF_ATTRIBUTE = "memberOf";
    private static final String GEO_ATTRIBUTE_SUFFIX = ",cn=groups,cn=accounts,dc=opentlc,dc=com";
    private static final String PARTNER_ATTRIBUTE_SUFFIX = ",cn=groups,cn=accounts,dc=opentlc,dc=com";
    private static final String SOURCE_OF_PROBLEM = "\n\tSource of problem:\t";
    private static final char AMPERSAND = '@';
    private static final char DASH = '-';
    private static final String SREG_PERSIST_COMPANY = "sreg_persist_company";
    private static final String NULL_STRING = "null";
    public static final String SR_DENORMALIZED_STUDENTS_TO_PROCESS="sr_denormalized_students_to_process";
    private String providerUrl = null;
    private String securityPrincipal = null;
    private String securityCredentials = null;
    private Hashtable<String,String> env = null;
    
    private Logger logger = Logger.getLogger("LDAPServiceBean");
    
    // Data structure of userIds that have been verified to exist in LDAP
    private Set<String> verifiedLADPRegisteredUsers = new HashSet<String>();
    
    // This cache can go stale if/when the underlying database is reloaded with new data
    // The data in this cache will no longer reflect this data
    // Scenario happens from time to time in dev
    // Need to bounce studentreg service 
    private Map<String,Integer> verifiedCompanies = new HashMap<String,Integer>();

    private boolean sregPersistCompany = true;

    public LDAPServiceBean() {
        if(System.getProperty(SREG_PERSIST_COMPANY) != null) {
            sregPersistCompany = Boolean.parseBoolean(System.getProperty(SREG_PERSIST_COMPANY));
        }

        logger.info("LDAPServiceBean()  starting .... sregPersistCompany = "+sregPersistCompany);
        
        providerUrl = System.getProperty(IPA_PROVIDER_URL);
        if(StringUtils.isEmpty(providerUrl))
            throw new RuntimeException("start() must pass system property of: "+IPA_PROVIDER_URL);
        securityPrincipal = System.getProperty(IPA_SECURITY_PRINCIPAL); 
        if(StringUtils.isEmpty(securityPrincipal))
            throw new RuntimeException("start() must pass system property of: "+IPA_SECURITY_PRINCIPAL);
        securityCredentials = System.getProperty(IPA_SECURITY_CREDENTIALS);
        if(StringUtils.isEmpty(securityCredentials))
            throw new RuntimeException("start() must pass system property of: "+IPA_SECURITY_CREDENTIALS);
        
        env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
        env.put("com.sun.jndi.ldap.connect.pool", "true"); // http://docs.oracle.com/javase/jndi/tutorial/ldap/connect/pool.html

        /*String testLDAPId = System.getProperty(TEST_LDAP_ID);
        StringBuilder sBuilder = new StringBuilder(UID);
        sBuilder.append(testLDAPId);
        sBuilder.append(COMMA);
        sBuilder.append(this.BASE_CTX_DN);
        DirContext testCtx = null;
        LdapCtx lookupResult = null;
        try {
            testCtx = grabLDAPConnectionFromPool();
            lookupResult = (LdapCtx) testCtx.lookup(sBuilder.toString());
            logger.info("LDAPSearchProcessor() lookupResult = "+lookupResult+" using the following ldapId = "+testLDAPId+"\n");
        } finally {
            if(lookupResult != null)
                lookupResult.close();
            if(testCtx != null) {
                testCtx.close();
            }
        }*/
    }
    
    
    /*
     * Accepts a DenormalizedStudent object (with companyName field populated) in body of Exchange message
     * Populates companyId on student obj
     * Populates geo, role and company name attributes on student obj
     * Persists a new company if doesn't already exist
     */
    public void determineCompanyIdAndPersistCompanyIfNeedBeGivenDenormalizedStudent(Exchange exchange) throws AttachmentValidationException {
        DenormalizedStudent dStudent = (DenormalizedStudent)exchange.getIn().getBody();
        boolean queryLdap = false;
        boolean alwaysUpdateCompany = true;
        
        String queryLdapHeader = (String)exchange.getIn().getHeader(QUERY_LDAP);
        String updateCompanyHeader = (String)exchange.getIn().getHeader(UPDATE_COMPANY);
        if(StringUtils.isNotEmpty(queryLdapHeader))
            queryLdap = Boolean.parseBoolean(queryLdapHeader);
        if(StringUtils.isNotEmpty(updateCompanyHeader))
            alwaysUpdateCompany = Boolean.getBoolean(updateCompanyHeader);
            
        this.determineCompanyIdAndPersistCompanyIfNeedBe(dStudent.getStudentObj(), queryLdap, null, alwaysUpdateCompany);
    }
    
    
    /*
     * Accepts a Student object (with companyName field populated) in body of Exchange message
     * Populates companyId on student obj
     * Populates geo, role and company name attributes on student obj
     * Persists a new company if doesn't already exist
     */
    public void determineCompanyIdAndPersistCompanyIfNeedBe(Exchange exchange) throws AttachmentValidationException{
        Student origStudent = (Student)exchange.getIn().getBody();
        boolean queryLdap = false;
        boolean alwaysUpdateCompany = true;
        
        String queryLdapHeader = (String)exchange.getIn().getHeader(QUERY_LDAP);
        String updateCompanyHeader = (String)exchange.getIn().getHeader(UPDATE_COMPANY);
        if(StringUtils.isNotEmpty(queryLdapHeader))
            queryLdap = Boolean.parseBoolean(queryLdapHeader);
        if(StringUtils.isNotEmpty(updateCompanyHeader))
            alwaysUpdateCompany = Boolean.getBoolean(updateCompanyHeader);
            
        this.determineCompanyIdAndPersistCompanyIfNeedBe(origStudent, queryLdap, null, alwaysUpdateCompany);
    }
    
    /* Given a student object with no affiliated companyId, executes the following:
     *   1)  determines "canonical company name"  by optionally querying LDAP or generating via local algorithm
     *   2)  Determines if company (given original company name), has already been persisted in Companies table
     *   3)  Creates and persists new company if need be
     *   4)  Populates companyId on student object
     */
    private void determineCompanyIdAndPersistCompanyIfNeedBe(Student origStudent, boolean queryLdap, Company companyObj, boolean alwaysUpdateCompany) throws AttachmentValidationException {
        
        String origCompanyName = origStudent.getCompanyName();
        String email = origStudent.getEmail();
        String canonicalCompanyName  = null;
        Integer companyId = 0;
        
        if(StringUtils.isEmpty(origCompanyName)) {

            // https://github.com/redhat-gpe/OPEN_Reporting/issues/44
            companyId = Company.COMPANY_UNKNOWN_ID;
        
        }else if(this.verifiedCompanies.containsKey(origCompanyName)){

            // 1) if companyId already cached, then use it to set on student without any further lookups
            companyId = this.verifiedCompanies.get(origCompanyName);

        }else {
            if(queryLdap) {
                // 2)  go to ldap to attempt to get the canonical company name
                Student tempStudent = new Student();
                tempStudent.setEmail(email);
                try {
                    getStudentAttributesFromLDAP(tempStudent);
                } catch (InvalidAttributeException e) {
                    logger.error(e.getMessage());
                }
                canonicalCompanyName = tempStudent.getCompanyName();
            }
            
            if(StringUtils.isEmpty(canonicalCompanyName)){

                // 3)  Not able to identify canonical company name (given student email) from LDAP;  will need to generate it
                canonicalCompanyName = this.transformToCanonicalCompanyName(origCompanyName);
            }

            // 4)  Create company object (which could optionally be used to persist/update database)
            if(companyObj == null) {
                companyObj = new Company();
                companyObj.setCompanyname(origCompanyName);
            }

            // 4.5) Always set LdapID to canonical company name
            companyObj.setLdapId(canonicalCompanyName);
            
            boolean companyUpdated = false;
            try {
                // 5)  determine whether this company already exists in database
                companyId = this.canonicalDAO.getCompanyID(origCompanyName);
                companyObj.setCompanyid(companyId);
            } catch(org.springframework.dao.EmptyResultDataAccessException x) {
                
                // 6)  Not in database so insert a new company
                updateCompany(companyObj);
                companyId = this.canonicalDAO.getCompanyID(origCompanyName);
                companyUpdated = true;
            }
            
            if(alwaysUpdateCompany && !companyUpdated) {
                // 7) OK; always update company in database; at this point, it is guaranteed that a companyId will now exist
                updateCompany(companyObj);
            }
            
            this.verifiedCompanies.put(origCompanyName, companyId);
        }
        
        // 8) set companyId on student obj
        origStudent.setCompanyid(companyId);
    }
    
    /*
     * Accepts a Student object in body of Exchange message.
     * Queries LDAP and populates the same student object with attributes such as companyName, role and geo
     */
    public void getStudentAttributesFromLDAP(Exchange exchange) {
        Student student = (Student)exchange.getIn().getBody();
        try {
            this.getStudentAttributesFromLDAP(student);
        }catch(InvalidAttributeException x) {
            handleValidationException(exchange, x.getMessage());
        }
    }
    
    /*
     * Queries LDAP and populates student object with attributes such as companyName, role and geo
     */
    public void getStudentAttributesFromLDAP(Student student) throws InvalidAttributeException {
        
        String companyName = student.getCompanyName();
        
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(OPEN_PAREN);
        sBuilder.append(MAIL);
        sBuilder.append(student.getEmail());
        sBuilder.append(CLOSE_PAREN);
        logger.info("getStudentAttributesFromLDAP()  will search on: "+sBuilder.toString());
    
        DirContext ctx = null;
        NamingEnumeration<SearchResult> listResults = null;
        
        try {
            ctx = grabLDAPConnectionFromPool();
            
            SearchControls ctls = new SearchControls();
            ctls.setCountLimit(GPTE_QUERY_SIZE_LIMIT);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            listResults = (NamingEnumeration<SearchResult>) ctx.search(BASE_CTX_DN, sBuilder.toString(), ctls);
            int count = 0;
                while (listResults.hasMore()) {
                    if(count > 0) {
                        throw new InvalidAttributeException("\nStudent records in LDAP should be unique. However, more than one result returned for : "+sBuilder.toString());
                    }

                    SearchResult si =(SearchResult)listResults.next();
                    Attributes attrs = si.getAttributes();
                    if (attrs == null) {
                        throw new InvalidAttributeException("\n"+student.getEmail()+ExceptionCodes.GPTE_SR1003); // No attributes for this user
                    }
                    NamingEnumeration<?> ae = attrs.getAll(); 
                    while (ae.hasMoreElements()) {
                        Attribute attr =(Attribute)ae.next();
                        String id = attr.getID();
                        if(TITLE_ATTRIBUTE.equals(id)){
                            String attValue = (String)attr.get(0);
                            if(NULL_STRING.equals(attValue))
                                logger.warn(student.getEmail()+" : null role from LDAP");
                            else {
                                student.setRoles(attValue);
                                try { 
                                    Student.Titles.valueOf(student.getRoles());
                                }catch(IllegalArgumentException x ) {
                                    String message = student.getEmail()+ExceptionCodes.GPTE_SR1002+",\t\t  title = "+student.getRoles(); // Invalid attributes from LDAP
                                    throw new InvalidAttributeException(message);
                                }
                            }
                        }else if(MEMBEROF_ATTRIBUTE.equals(id)) {
                            Enumeration<?> vals = attr.getAll();
                            while (vals.hasMoreElements()) {
                                String attributeValue = (String)vals.nextElement();
                                if(attributeValue.startsWith(GEO_ATTRIBUTE_PREFIX)){
                                    String geoValue = attributeValue.substring(13, attributeValue.indexOf(GEO_ATTRIBUTE_SUFFIX));
                                    //System.out.println("searchName() geo attribute; value = " + geoValue);
                                    if(NULL_STRING.equals(geoValue))
                                        logger.warn(student.getEmail()+" : null geo from LDAP");
                                    else {
                                        student.setRegion(geoValue);
                                        try{
                                            Student.Geos.valueOf(student.getRegion());
                                        }catch(IllegalArgumentException x ) {
                                            String message = student.getEmail()+ExceptionCodes.GPTE_SR1002+",\t\t  geo = "+student.getRegion(); // Invalid attributes from LDAP
                                            throw new InvalidAttributeException(message);
                                        }
                                    }
                                }else if(attributeValue.startsWith(PARTNER_ATTRIBUTE_PREFIX)) {
                                    if(NULL_STRING.equals(attributeValue))
                                        logger.warn(student.getEmail()+" : null partner name from LDAP");
                                    else {
                                        String partnerValue = attributeValue.substring(17, attributeValue.indexOf(PARTNER_ATTRIBUTE_SUFFIX));
                                        student.setCompanyName(partnerValue);
                                    }
                                }
                            }
                        }
                    }
                    count++;
                }
            if(count == 0){

                // https://github.com/redhat-gpe/OPEN_Reporting/issues/77#issuecomment-241466635
                student.setRoles(Student.Titles.other.name());
                student.setRegion(Student.Geos.global.name());

                throw new InvalidAttributeException(student.getEmail()+ExceptionCodes.GPTE_SR1001); // Not able to locate this user in LDAP
            }
        }catch(InvalidAttributeException x) {
            throw x;
        }catch(Exception x) {
            x.printStackTrace();
            logger.error("NamingException: "+x.getLocalizedMessage()+" : query = : "+sBuilder.toString());
        } finally {
                
                // Critical that both LDAP result object and the connection are closed
                // http://blog.avisi.nl/2015/03/27/java-ldap-connection-pooling/
            try {
                if(listResults != null)
                    listResults.close();
                if(ctx != null) 
                    ctx.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    
    public boolean hasNoExceptionProperty(Exchange exchange) {
        Object validationExBuffer  = exchange.getIn().getHeader(InvalidAttributeException.VALIDATION_EXCEPTION_BUFFER);
        if(validationExBuffer == null)
            return true;
        else
            return false;
    }
   
/* 
    // Queries for presence of existing student records in IPA LDAP server
    @SuppressWarnings("unchecked")
    public void removeDuplicateStudents(Exchange exchange) throws NamingException {
        
        // get the list of students
        List<StudentRegistrationBindy> students = (List<StudentRegistrationBindy>) exchange.getIn().getBody();
        Iterator<StudentRegistrationBindy> studentIterator = students.iterator();
        logger.info("removeDuplicateStudents() starting with the following # of students "+students.size());
        
        int dupCount = 0;
        int ldapQCount = 0;
        while (studentIterator.hasNext()) {
            StudentRegistrationBindy tempStudent = studentIterator.next();
            String email = tempStudent.getEmail();
            
            // 1)  if student already in temp data structure, then skip
            if(!verifiedUsers.contains(email)) {
                ldapQCount++;
                
                // 2) check if student already registered in LDAP
                DirContext testCtx = null;
                LdapCtx lookupResult = null;
                StringBuilder sBuilder = new StringBuilder(MAIL);
                
                try {
                    testCtx = grabLDAPConnectionFromPool();                    
                } catch(NamingException x) {
                    // This is a serious problem that requires admin attention
                    throw x;
                }
                
                try {
                    lookupResult = (LdapCtx) testCtx.lookup(sBuilder.toString());
                    logger.debug("process() found user in ldap "+email);
                    studentIterator.remove();
                    continue;
                } catch(NamingException x) {
                    logger.debug("process() did not find user in ldap "+email);
                } finally {
                    try {
                        // Critical that both LDAP result object and the connection are closed
                        if(lookupResult != null)
                            lookupResult.close();
                        if(testCtx != null) {
                            testCtx.close();
                        }
                    } catch(NamingException x){ x.printStackTrace(); }
                }
                
             // 3)  student must be uploaded to LDAP so will not remove from Camel message
                verifiedUsers.add(email);
                
            } else {
                studentIterator.remove();
                dupCount++;
            }
        }
        logger.info("removeDuplicateStudents() finishing with the following # of students "+students.size()+" : # of dups = "+dupCount+" : # of ldap queries = "+ldapQCount);
    }
*/
    
    private DirContext grabLDAPConnectionFromPool() throws NamingException {
        try {
            DirContext ctx = new InitialDirContext(env);
            //logger.debug("grabLDAPConnectionFromPool() ctx = "+ctx);
            return ctx;
        }catch(AuthenticationException x) {
            logger.error("init() unable to connect to: "+providerUrl+" using user="+securityPrincipal);
            throw x;
        }catch(javax.naming.CommunicationException x) {
            logger.error("init() unable to connect to: "+providerUrl+" using user="+securityPrincipal);
            throw x;
        }
    }
    
    private String getOPENTLCIDfromEmail(String email) {
        return email.replace(AMPERSAND, DASH);
    }
    
    
    
    
    
    /*  Inspect exchange header for:  ATTACHMENT_VALIDATION_EXCEPTIONS
     *  If found, iterates through exceptions, creates a String concatenation and sets exception on Exchange
     */
    public void throwAnyCachedExceptions(Exchange exchange) {
        // purge any existing exceptions
        exchange.setException(null);
        
        Object exObj = exchange.getIn().getHeader(ATTACHMENT_VALIDATION_EXCEPTIONS);
        if(exObj != null){
            Collection<AttachmentValidationException> exceptions = (Collection<AttachmentValidationException>)exObj;
            StringBuilder sBuilder = new StringBuilder("\n");
            for(AttachmentValidationException x : exceptions) {
                sBuilder.append(x.getLocalizedMessage()+"\n");
                x.printStackTrace();
            }
            exchange.setException(new Exception(sBuilder.toString()));
        }
        
    }

    /* Given List<StudentRegistrationBindy>, transforms to Collection<Student>
     * Removes any duplicates that might be in student registration CSV
     * All students objects created by this function will include flag for upload to GPTE IPA
     * Also creates Collection<DenormalizedStudents> and sets to header = SR_DENORMALIZED_STUDENTS_TO_PROCESS
     */
    public void convertStudentRegBindyToCanonicalStudents(Exchange exchange) {
        Map<String,Student> noDupsStudentMap = new HashMap<String, Student>();
        Map<String, AttachmentValidationException> exceptions = new HashMap<String, AttachmentValidationException>();
        int dupsCounter = 0;
        int updateStudentsCounter = 0;
        Object body = exchange.getIn().getBody();
        List<StudentRegistrationBindy> sBindyList = null;
        List<DenormalizedStudent> dStudents = new ArrayList<DenormalizedStudent>();
        
        
        // 1)  Make sure always dealing with a list
        //     If there is only one record in data file, then bindy will not return a list
        if( body instanceof java.util.List ) {
            sBindyList = (List<StudentRegistrationBindy>)exchange.getIn().getBody();
        }else {
            sBindyList = new ArrayList<StudentRegistrationBindy>();
            sBindyList.add((StudentRegistrationBindy)body);
        }
       
        // 2) Iterate through list and filter out potential duplicates by email address
        for(StudentRegistrationBindy sBindy : sBindyList){
        	String email = sBindy.getEmail();
            if(noDupsStudentMap.containsKey(email) || exceptions.containsKey(sBindy.getEmail())) {
                dupsCounter++;
            }else {
            	
            	//Prune any entries that are known to have recently been successfully pushed to IPA
            	if(this.verifiedLADPRegisteredUsers.contains(email) ) {
            		logger.info(email+" : convertStudentRegBindy() Already verified to have been successfully pushed to IPA LDAP.  Will not process again");
            		continue;
            	}
                
                try {
                    // 3) Grab student and company data as parsed by bindy
                    Student student = sBindy.convertToCanonicalStudent();
                    Company company = sBindy.convertToCanonicalCompany();
                    DenormalizedStudent dStudent = new DenormalizedStudent();
                    dStudent.setStudentObj(student);
                    dStudent.setCompanyObj(company);
                    
                    // 4) Determine companyId of affiliated company (based on company name provided in bindy)
                    this.determineCompanyIdAndPersistCompanyIfNeedBe(student, false, company, sregPersistCompany);
                    updateStudentsCounter++;
                    
                    // 5)  Ensure that all students get uploaded to IPA
                    student.setShouldUpdateIPA(true);

                    if(updateStudentsCounter%100 == 0)
                        logger.info("convertStudentRegBindyToCanonicalStudents() processing # "+updateStudentsCounter);

                    noDupsStudentMap.put(student.getEmail(), student);
                    dStudents.add(dStudent); 

                    Thread.sleep(100);
                } catch (AttachmentValidationException e) {
                    exceptions.put(sBindy.getEmail(), e);
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptions.put(sBindy.getEmail(), new AttachmentValidationException(sBindy.getEmail()+" : "+e.getMessage()));
                }
            }
        }

        logger.info("convertToCanonicalStudents() total students = "+noDupsStudentMap.size()+" : updatedStudents = "+updateStudentsCounter+" : dups = "+dupsCounter+" : exceptions = "+exceptions.size());
        if(exceptions.size() > 0)
            exchange.getIn().setHeader(ATTACHMENT_VALIDATION_EXCEPTIONS, exceptions.values());

        exchange.getIn().setHeader(SR_DENORMALIZED_STUDENTS_TO_PROCESS, dStudents);
        exchange.getIn().setBody(noDupsStudentMap.values());
    }
    
    public void updateIPAFlagOnStudents(Exchange exchange) {
        List<DenormalizedStudent> students = (List<DenormalizedStudent>)exchange.getIn().getBody();
        Map<String, String> uploadExceptionMap = (Map<String,String>)exchange.getIn().getHeader(UPLOAD_EXCEPTION_MAP);
        exchange.getIn().removeHeader(UPLOAD_EXCEPTION_MAP);
        
        int count = 0;
        for(DenormalizedStudent sObj : students) {
            String email = sObj.getStudentObj().getEmail();
            if(uploadExceptionMap.containsKey(email)){
                logger.error(email+" : will not update ipaStatus because of upload issues");
            }else {
                int sUpdate = this.canonicalDAO.updateStudentStatus(email, 1, Student.IPA_STATUS);
                count = count + sUpdate;
                
                // Set verifiedLDAPRegisteredUsers so that future processing of student registrations can skip this student
                this.verifiedLADPRegisteredUsers.add(email);
            }
        }
        logger.info("updateIPAFlagOnStudents() # of students to update = "+students.size()+" : total updated = "+count);
        
        if(uploadExceptionMap.size() > 0){
            StringBuilder sBuilder = new StringBuilder("updateIPAFlagOnStudents() following students not updated due to upload issues with LDAP: \n\n");
            Iterator emailIt = uploadExceptionMap.keySet().iterator();
            while(emailIt.hasNext()){
                String email = (String)emailIt.next();
                sBuilder.append(email);
                sBuilder.append("\n");
                sBuilder.append(uploadExceptionMap.get(email));
                sBuilder.append("\n\n");
            }
            throw new RuntimeException(sBuilder.toString());
        }
    }
    
    private void handleValidationException(Exchange exchange, String message) {
        StringBuilder exBuilder;
        Object validationExBuilderObj  = exchange.getIn().getHeader(InvalidAttributeException.VALIDATION_EXCEPTION_BUFFER);
        if(validationExBuilderObj == null){
            exBuilder = new StringBuilder();
            exchange.getIn().setHeader(InvalidAttributeException.VALIDATION_EXCEPTION_BUFFER, exBuilder);
        } else
            exBuilder = (StringBuilder)validationExBuilderObj;
        
        exBuilder.append("\n");
        exBuilder.append(message);
    }

    
    public void setStudentToHeader(Exchange exchange) {
        DenormalizedStudent dsObj = (DenormalizedStudent)exchange.getIn().getBody();
        exchange.getIn().setHeader("STUDENT_OBJ", dsObj.getStudentObj());
    }
    public void setStudentToBody(Exchange exchange) {
        exchange.getIn().setBody(exchange.getIn().getHeader("STUDENT_OBJ"));
    }
}
