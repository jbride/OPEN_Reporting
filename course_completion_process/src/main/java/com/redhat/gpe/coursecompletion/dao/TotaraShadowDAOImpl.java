package com.redhat.gpe.coursecompletion.dao;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import org.apache.log4j.Logger;

import com.redhat.gpe.coursecompletion.domain.TotaraCourseCompletion;


public class TotaraShadowDAOImpl implements TotaraShadowDAO {

    private static final String TOTARA_SHADOW_DB_TEST_SQL = "totara_shadow_db_test_sql";

    private Logger logger = Logger.getLogger("TotaraShadowDAOImpl");

    @Autowired
    private JdbcTemplate tsJdbcTemplate;

    public int testTotaraJDBCConnection() {
        String testSql = System.getProperty(TOTARA_SHADOW_DB_TEST_SQL);
        int ccCount = tsJdbcTemplate.queryForInt(testSql);
        return ccCount;
    }

    public List<TotaraCourseCompletion> getCourseCompletionsByRange(long lowCCDate, long highCCDate) {
        String totaraCCSQL = "select cc.id, u.email, u.firstname, u.lastname, cc.course, c.fullname, c.shortname, cc.timecompleted from mdl_course_completions cc join mdl_course c on cc.course = c.id join mdl_course_info_data ci on ci.courseid = cc.course join mdl_user u on u.id = cc.userid where status in (50, 75) and ci.fieldid = 14 and cc.rpl is null and (ci.data = 'GPTE' or shortname in ('RHU-TC01-DO407R','RHU-TC01-DO409R')) "; 
        StringBuilder totaraCCSQLBuilder = new StringBuilder(totaraCCSQL);
        totaraCCSQLBuilder.append(" and cc.timecompleted >= "+lowCCDate);
        totaraCCSQLBuilder.append(" and cc.timecompleted <= "+highCCDate);
        totaraCCSQLBuilder.append(" order by cc.id desc");
        logger.info("getCourseCompletionsByRange() sql = "+totaraCCSQLBuilder.toString());
        return queryAndReturnCourseCompletions(totaraCCSQLBuilder.toString());
    }

    public List<TotaraCourseCompletion> getLatestCourseCompletions(long lastCCDate, int totaraCCLimit) {

        String totaraCCSQL = "select cc.id, u.email, u.firstname, u.lastname, cc.course, c.fullname, c.shortname, cc.timecompleted from mdl_course_completions cc join mdl_course c on cc.course = c.id join mdl_course_info_data ci on ci.courseid = cc.course join mdl_user u on u.id = cc.userid where status in (50, 75) and ci.fieldid = 14 and cc.rpl is null and (ci.data = 'GPTE' or shortname in ('RHU-TC01-DO407R','RHU-TC01-DO409R')) and cc.timecompleted > ";
        StringBuilder totaraCCSQLBuilder = new StringBuilder(totaraCCSQL);
        totaraCCSQLBuilder.append(lastCCDate);
        totaraCCSQLBuilder.append(" order by cc.timecompleted desc");

        if(totaraCCLimit > 0)
            totaraCCSQLBuilder.append(" limit "+totaraCCLimit);
        logger.info("getLatestCourseCompletions() sql = "+totaraCCSQLBuilder.toString());
        return queryAndReturnCourseCompletions(totaraCCSQLBuilder.toString());
    }

    private List<TotaraCourseCompletion> queryAndReturnCourseCompletions(String totaraCCSQL) {
        List<TotaraCourseCompletion> sCourses = new ArrayList<TotaraCourseCompletion>();
        SqlRowSet rowSet = tsJdbcTemplate.queryForRowSet(totaraCCSQL);
        if(!rowSet.isLast()) {
            while(rowSet.next()) {
                int totaraCCId = rowSet.getInt(1);
                String email = rowSet.getString(2);
                String firstName = rowSet.getString(3);
                String lastName = rowSet.getString(4);
                String totaraCourseId = rowSet.getString(5);
                String courseFullName = rowSet.getString(6);
                String courseShortName = rowSet.getString(7);
                
                /* Totara shadow database appears to return a long that is missing the last three trailing zeros. ie; 1484165145
                 * This value is different than:  1484165145000
                 */
                long timeCompleted = rowSet.getLong(8);
                timeCompleted = this.addTrailingZerosToATotaraLong(timeCompleted);
                
                logger.debug("queryAndReturnCourseCompletions() "+totaraCCId+" :" +email+" : "+totaraCourseId+" : "+courseFullName+" : "+courseShortName+" : "+timeCompleted);
                TotaraCourseCompletion tCC = new TotaraCourseCompletion(totaraCCId, email, firstName, lastName, totaraCourseId, courseFullName, courseShortName, timeCompleted);
                sCourses.add(tCC);
            }
        }
        return sCourses;
    }
    
    private long addTrailingZerosToATotaraLong(long tLong) {
    	String sString = Long.toString(tLong);
    	return Long.parseLong(sString + "000");
    }

}
