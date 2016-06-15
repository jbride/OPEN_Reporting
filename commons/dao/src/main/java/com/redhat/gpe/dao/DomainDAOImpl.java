package com.redhat.gpe.dao;

import com.redhat.gpe.domain.canonical.*;
import com.redhat.gpe.domain.helper.Accreditation;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpe.domain.helper.DenormalizedStudent;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.List;

import javax.sql.DataSource;

public class DomainDAOImpl implements CanonicalDomainDAO {

    private static final String OK = "Ok";
    private static final String EQUAL="=?,";
    private static final String EMP_NOT_FOUND_IN_SB = "Student not found in Skills Base";
    private int rht_company_id = 0;

    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    private JdbcTemplate sbJdbcTemplate;
    
    private SimpleJdbcCall simpleJdbcCall;
    
    @Autowired
    public void setDataSource(DataSource transactionalDS) {
        simpleJdbcCall = new SimpleJdbcCall(transactionalDS);
    }
    
    /* *********************        Company                ***************************** */
    
    public int updateCompany(Company companyObj) {
        int updatedCount = 0;
        if(companyObj.getCompanyid() == 0) {
            StringBuilder sql = new StringBuilder("insert into Companies values (null,?,?,?,?,?) ");
            updatedCount = sbJdbcTemplate.update(sql.toString(), 
                    companyObj.getAccountid(), 
                    companyObj.getCompanyname(),
                    companyObj.getPartnertype(),
                    companyObj.getPartnertier(),
                    companyObj.getLdapId()
            );
        }else {
            StringBuilder sBuilder = new StringBuilder("update Companies set ");
            sBuilder.append(Company.ACCOUNT_ID+EQUAL+Company.COMPANY_NAME+EQUAL+Company.PARTNER_TYPE+EQUAL+Company.PARTNER_TIER+"=? ");
            sBuilder.append("where companyID="+companyObj.getCompanyid());
            sbJdbcTemplate.update(sBuilder.toString(), new Object[] {
                companyObj.getAccountid(),
                companyObj.getCompanyname(),
                companyObj.getPartnertype(),
                companyObj.getPartnertier(),
                companyObj.getLdapId()
                });
        }
        return updatedCount;
    }
    
    public int getCompanyID(String companyName) {
        if(Company.RED_HAT_COMPANY_NAME.equals(companyName) && rht_company_id != 0) {
            // RHT CompanyID will rarely (if ever) change.  Safe to return what's in temp cache.
            return rht_company_id;
        }
        int companyId = sbJdbcTemplate.queryForInt("select CompanyID from Companies where CompanyName = \""+companyName+"\"");
        if(Company.RED_HAT_COMPANY_NAME.equals(companyName))
            rht_company_id = companyId;
        return companyId;
    }

    public Company getCompanyGivenLdapId(String ldapId) {
        StringBuilder sBuilder = new StringBuilder("select * from Companies c where ldapId = \""+ldapId+"\"");
        Company companyObj = sbJdbcTemplate.queryForObject(sBuilder.toString(), new CompanyRowMapper());
        return companyObj;
    }
   
   
    /* ****************************************************************************** */
    

    /* *****************        Students            **********************************/
    
    public Student getStudentByEmail(String email) {
        StringBuilder sBuilder = new StringBuilder("select * from Students s where Email = \""+email+"\"");
        Student studentObj = sbJdbcTemplate.queryForObject(sBuilder.toString(), new StudentRowMapper());
        return studentObj;
    }
    
    public List<Student> selectRHTStudentsWithMissingAttributes() {
        String sql = "SELECT * FROM Students WHERE Region is NULL or Roles is NULL and email like \"%redhat.com\" order by email";
        List<Student> students = sbJdbcTemplate.query(sql, new StudentRowMapper());
        return students;
    }
    
    
    public List<DenormalizedStudent> selectStudentsByIpaStatus(int status) {
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(Student.FROM_CLAUSE+", "+Company.FROM_CLAUSE);
        sql.append("FROM Students s, Companies c ");
        sql.append("WHERE s.ipaStatus="+status);
        sql.append(" AND s.CompanyId = c.CompanyID");
        List<DenormalizedStudent> students = sbJdbcTemplate.query(sql.toString(), new DenormalizedStudentRowMapper());
        return students;
    }
    
    public void updateStudent(Student studentObj) {
        if(studentObj == null)
            throw new RuntimeException("updateStudent() passed null student");
        
        if(studentObj.getStudentid() == 0) {
            StringBuilder sBuilder = new StringBuilder("insert into Students values (null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            sbJdbcTemplate.update(sBuilder.toString(), 
                    studentObj.getEmail().toLowerCase(), 
                    studentObj.getFirstname(),
                    studentObj.getLastname(), 
                    studentObj.getCompanyid(),
                    studentObj.getRegion(),
                    studentObj.getSubregion(),
                    studentObj.getCountry(),
                    studentObj.getRoles(),
                    studentObj.getSalesforcecontactid(),
                    studentObj.getSalesforceactive(),
                    studentObj.getSumtotalid(),
                    studentObj.getSumtotalactive(),
                    studentObj.getSkillsbaseStatus(),
                    studentObj.getIpaStatus(),
                    studentObj.getActivationDate(),
                    studentObj.getDeActivationDate()
                    );
        } else {
            StringBuilder sBuilder = new StringBuilder("update Students set ");
            sBuilder.append(Student.EMAIL+EQUAL+Student.FIRST_NAME+EQUAL+Student.LAST_NAME+EQUAL);
            sBuilder.append(Student.COMPANY_ID+EQUAL+Student.REGION+EQUAL+Student.SUBREGION+EQUAL+Student.COUNTRY+EQUAL+Student.ROLES+EQUAL);
            sBuilder.append(Student.SALES_FORCE_CONTACT_ID+EQUAL+Student.SALES_FORCE_ACTIVE+EQUAL+Student.SUMTOTAL_ID+EQUAL+Student.SUMTOTAL_ACTIVE+EQUAL);
            sBuilder.append(Student.SKILLSBASE_STATUS+EQUAL+Student.IPA_STATUS+EQUAL);
            sBuilder.append(Student.ACTIVATION_DATE+EQUAL+Student.DEACTIVATION_DATE+"=? ");
            sBuilder.append("where studentID="+studentObj.getStudentid());
            sbJdbcTemplate.update(sBuilder.toString(),new Object[]{
                    studentObj.getEmail(),
                    studentObj.getFirstname(),
                    studentObj.getLastname(), 
                    studentObj.getCompanyid(),
                    studentObj.getRegion(),
                    studentObj.getSubregion(),
                    studentObj.getCountry(),
                    studentObj.getRoles(),
                    studentObj.getSalesforcecontactid(),
                    studentObj.getSalesforceactive(),
                    studentObj.getSumtotalid(),
                    studentObj.getSumtotalactive(),
                    studentObj.getSkillsbaseStatus(),
                    studentObj.getIpaStatus(),
                    studentObj.getActivationDate(),
                    studentObj.getDeActivationDate()
                });
        }
    }

    public boolean doesStudentExist(String email) {
        String sql = "select count(*) from Students where email=?";
        Integer count = sbJdbcTemplate.queryForObject(sql, Integer.class, email.toLowerCase());
        return count > 0;        
    }


    public boolean hasThisStudentBeenEmailedBefore(String email) {
        String sql = "select count(*) from Students_status where email=? and status_code=?";
        Integer count = sbJdbcTemplate.queryForObject(sql, Integer.class, email.toLowerCase(), DAOConstants.USER_NOT_FOUND_IN_SKILLS_BASE_STATUS_CODE);
        boolean result = count > 0;
        return result;                
    }

    public void updateStudentStatusForEmailedAlready(String email) {
        updateStudentStatus(email, DAOConstants.USER_NOT_FOUND_IN_SKILLS_BASE_STATUS_CODE, EMP_NOT_FOUND_IN_SB);    
    }

    public void updateStudentStatusForOk(String email) {
        
        updateStudentStatus(email, DAOConstants.OK_STATUS_CODE, OK);        
    }

    private void updateStudentStatus(String email, int statusCode, String details) {
        // insert or update
        //String sql = "insert into students_status (email, status_code, details) values (?, ?, ?) on duplicate key update email=values(email), status_code=values(status_code), details=values(details)";
        
        //sbJdbcTemplate.update(sql, email.toLowerCase(), statusCode, details);
    }
/* ******************************************************************************* */

    
    
/* ***************            Courses            *********************** */
    
    
    /*
     * Returns a canonical Course given the courseName and the mapping source type (ie:  dokeos, sumtotal, etc)
     * If sourceName == null, then its assumed that the given courseName is the canonical name and subsequenty queries the Courses table directly
     * If sourceName != null, then joins on CourseMapping.Source field
     */
    public Course getCourseByCourseName(String courseName, String sourceName) {
        StringBuilder sBuilder = new StringBuilder("select c.CourseID, c.CourseName from Courses c ");
        if(StringUtils.isEmpty(sourceName)) {
            sBuilder.append("where c.CourseName = \""+courseName+"\"");
        }else {
            sBuilder.append(", CourseMappings cm ");
            sBuilder.append("where c.CourseID = cm.CourseID and cm.Source=\"");
            sBuilder.append(sourceName);
            sBuilder.append("\" and cm.OldCourseCode=\"");
            sBuilder.append(courseName);
            sBuilder.append("\"");
        }
        logger.debug("getCourseByCourseName() query = "+sBuilder.toString());
        Course courseObj = sbJdbcTemplate.queryForObject(sBuilder.toString(), new CourseRowMapper());
        return courseObj;
        
    }
    
    // Given a courseId, retrieves a Course object
    // Initially checks Courses tables.  If not found then checks CourseMappings table
    public Course getCourseByCourseId(String courseId) {
        Course courseObj = null;
        StringBuilder sBuilder = null;
        try {
            // 1)  First attempt: retrieve from Courses table  
            sBuilder = new StringBuilder("select c.CourseID, c.CourseName from Courses c ");
            sBuilder.append("where c.CourseId = \""+courseId+"\"");
            courseObj = sbJdbcTemplate.queryForObject(sBuilder.toString(), new CourseRowMapper());
        } catch(org.springframework.dao.EmptyResultDataAccessException x){
            
            // 2) Second attempt:  retreive from CourseMappings table
            sBuilder = new StringBuilder("select c.CourseID, c.CourseName from Courses c, CourseMappings cm ");
            sBuilder.append("where cm.CourseId = c.CourseId ");
            sBuilder.append("and (cm.oldCourseCode=\""+courseId+"\" or cm.newCourseCode=\""+courseId+"\")");
            courseObj = sbJdbcTemplate.queryForObject(sBuilder.toString(), new CourseRowMapper());
        }
        return courseObj;
    }
    
    public List<Course> listCanonicalCourses() {
        StringBuilder sBuilder = new StringBuilder("select ");
        sBuilder.append(Course.FROM_CLAUSE);
        sBuilder.append(" from Courses c");
        List<Course> courses = sbJdbcTemplate.query(sBuilder.toString(), new CourseRowMapper());
        return courses;
    }
    
/* ******************************************************************************* */
    
    
    
/* *****************        Student Course        **********************************/
    

    public void addStudentCourse(StudentCourse cCompletion) {
        StringBuilder sBuilder = new StringBuilder("insert into StudentCourses values (NULL,?,?,?,?,?,?,?)");
        
        sbJdbcTemplate.update(sBuilder.toString(),
            cCompletion.getStudentid(),
            cCompletion.getCourseid(),
            cCompletion.getLanguageid(),
            cCompletion.getAssessmentdate(),
            cCompletion.getAssessmentresult(),
            cCompletion.getAssessmentscore(),
            StudentCourse.UNPROCESSED
        );
    }
    
    public int updateStudentCourseProcessedByStudent(Student studentObj, int processedValue) {
        return this.updateStudentCourseProcessedByStudent(studentObj.getStudentid(), processedValue);
    }

    public int updateStudentCourseProcessedByStudent(Integer studentId, int processedValue) {
    
        StringBuilder sql = new StringBuilder("update StudentCourses set Processed=");
        sql.append(processedValue);
        sql.append(" where StudentID=");
        sql.append(studentId);
        return sbJdbcTemplate.update(sql.toString());
    }
    
    public List<Integer> selectStudentIdsWithStudentCoursesByStatus(int processedStatus) {
        return this.selectStudentIdsWithStudentCoursesByStatus(processedStatus, 0, 0);
    }
    
    public List<Integer> selectStudentIdsWithStudentCoursesByStatus(int processedStatus, int lowStudentId, int highStudentId) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("SELECT sc.StudentID FROM StudentCourses sc, Students s WHERE sc.Processed=");
        sBuilder.append(processedStatus);
        if(lowStudentId > 0) {
            sBuilder.append(" and sc.studentId >= "+lowStudentId);
        }
        if(highStudentId > 0) {
            sBuilder.append(" and sc.studentId <= "+highStudentId);
        }
        sBuilder.append(" GROUP BY sc.StudentID");
        List<Integer> studentIds = sbJdbcTemplate.queryForList(sBuilder.toString(), Integer.class);
        return studentIds;
    }
    
    public List<Integer> selectStudentCourseIdsByStatus(int processedStatus) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("SELECT StudentCourseID FROM StudentCourses WHERE Processed=");
        sBuilder.append(processedStatus);
        logger.debug("selectStudentCourseIdsByStatus(), query = " + sBuilder.toString());
        List<Integer> sCourseIds = sbJdbcTemplate.queryForList(sBuilder.toString(), Integer.class);
        return sCourseIds;
    }

    public List<CourseCompletion> selectPassedStudentCoursesByStudent(int studentId) {
        StringBuilder sBuilder = new StringBuilder();
        
        // StudentCourse has both CourseID and CourseName
        sBuilder.append("SELECT "+StudentCourse.FROM_CLAUSE+", "+Course.FROM_CLAUSE+", "+Student.FROM_CLAUSE+", "+Language.FROM_CLAUSE+" ");
        
        sBuilder.append("FROM StudentCourses sc, Courses c, Students s, Languages l ");
        sBuilder.append("WHERE sc.CourseID = c.CourseId ");
        sBuilder.append("AND sc.StudentID = s.StudentID ");
        sBuilder.append("AND sc.LanguageID = l.LanguageID ");
        sBuilder.append("AND sc.StudentID="+studentId);
        sBuilder.append(" AND sc.AssessmentResult=\""+StudentCourse.ResultTypes.Pass+"\" ");
        sBuilder.append("ORDER By sc.AssessmentDate DESC");
        logger.debug("selectStudentCoursesByStudent() query = " + sBuilder.toString());
        List<CourseCompletion> sCourses = sbJdbcTemplate.query(sBuilder.toString(), new DenormalizedStudentCourseRowMapper());
        return sCourses;
    }

    public boolean isNewStudentCourseForStudent(StudentCourse theStudent) {
        
        String sql = "select count(*) from students_assessment where email=? and assessment_id=?";
        int assessmentId = 0; //getAssessmentId(theStudent.getAssessment());
        Integer count = 0; //sbJdbcTemplate.queryForObject(sql, Integer.class, theStudent.getEmail().toLowerCase(), assessmentId);
        return count == 0;
    }
/* ******************************************************************************* */
    
    
    
    
/* *******************    AccreditationDefinition    ************************************************ */
    
    public int getAccreditationIdGivenName(String accredName){
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("SELECT AccreditationID from AccreditationDefinitions where AccreditationName = \"");
        sBuilder.append(accredName);
        sBuilder.append("\"");
        try {
            int accredId = sbJdbcTemplate.queryForObject(sBuilder.toString(), null, Integer.class);
            return accredId;
        } catch(org.springframework.dao.EmptyResultDataAccessException x) {
            logger.error("getAccreditationIdGivenName() unable to locate an AccreditationDefinition with name = "+accredName);
            throw x;
        }
    }
/* ******************************************************************************* */
    
    
    
    
    
/* *******************    StudentAccreditation    ************************************************ */
    
    public List<Accreditation> selectUnprocessedStudentAccreditationsByProcessStatus(int processedStatus) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("SELECT "+Student.FROM_CLAUSE+","+AccreditationDefinition.FROM_CLAUSE+","+StudentAccreditation.FROM_CLAUSE+","+Course.FROM_CLAUSE+" "); 
        sBuilder.append("FROM Students s, AccreditationDefinitions a, StudentAccreditations sa, Courses c ");
        sBuilder.append("WHERE sa.StudentID = s.StudentID ");
        sBuilder.append("AND sa.AccreditationID = a.AccreditationID ");
        sBuilder.append("AND sa.CourseID = c.CourseID");
        List<Accreditation> sAccreds = sbJdbcTemplate.query(sBuilder.toString(), new DenormalizedStudentAccreditationRowMapper());
        return sAccreds;
    }
    
    public int changeStatusOnExpiredStudentAccreditations() {
        
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("update StudentAccreditations set accreditationType=\"");
        sBuilder.append(StudentAccreditation.Types.Expired);
        sBuilder.append("\" where AccreditationDate < DATE_SUB(NOW(),INTERVAL 2 YEAR)");
        int expiredCount = sbJdbcTemplate.update(sBuilder.toString());
        return expiredCount;
    }
    
    // Allows for insert or update
    public void addStudentAccreditation(StudentAccreditation sAccredObj) {
        
        StringBuilder sBuilder = new StringBuilder("insert into StudentAccreditations values (?,?,?,?,?,?,?,null) ");
        sBuilder.append("on duplicate key update AccreditationDate=values(AccreditationDate), AccreditationType=values(AccreditationType), CourseID=values(CourseID), Processed=values(Processed), RuleFired=values(RuleFired)");
                
        int updateCount = sbJdbcTemplate.update(sBuilder.toString(),
                sAccredObj.getStudentid(), 
                sAccredObj.getAccreditationid(), 
                sAccredObj.getAccreditationdate(),
                sAccredObj.getAccreditationtype(),
                sAccredObj.getCourseid(),
                sAccredObj.getProcessed(),
                sAccredObj.getRuleFired()
                );
        logger.debug("addStudentAccreditation() added the following # of records = "+updateCount);
    }
/* ******************************************************************************* */
    
    
    
/* ************			Language		*************  */
    public List<Language> getLanguages() {
    	StringBuilder sBuilder = new StringBuilder("select "+Language.FROM_CLAUSE+ " from Languages l");
    	List<Language> langs = sbJdbcTemplate.query(sBuilder.toString(), new LanguagesRowMapper());    	
    	return langs;
    }
/*******************************************************/
    
    
    
/* **************        Reporting  ***************** */
    
    public void triggerStoredProcedure(String storedProcCall) {
        logger.info("triggerStoredProcedure simpleJdbcCall = "+this.simpleJdbcCall+" : storedProcCall = "+storedProcCall);
        sbJdbcTemplate.update(storedProcCall);
    }

    
    
/*****************************************************/
    
}
