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

    public List<TotaraCourseCompletion> getCourseCompletionsByRange(int lowCCId, int highCCId) {
        String totaraCCSQL = "select cc.id, u.email, u.firstname, u.lastname, cc.course, c.fullname, c.shortname from mdl_course_info_data ci, mdl_course c, mdl_course_completions cc, mdl_user u where cc.course=ci.courseid and cc.course=c.id and u.id = cc.userid and status=50 and ci.data='GPTE' ";
        StringBuilder totaraCCSQLBuilder = new StringBuilder(totaraCCSQL);
        totaraCCSQLBuilder.append(" and cc.course => "+lowCCId);
        totaraCCSQLBuilder.append(" and cc.course <= "+highCCId);
        totaraCCSQLBuilder.append(" order by cc.id desc");
        logger.debug("getCourseCompletionsByRange() sql = "+totaraCCSQLBuilder.toString());
        return queryAndReturnCourseCompletions(totaraCCSQLBuilder.toString());
    }

    public List<TotaraCourseCompletion> getLatestCourseCompletions(int lastCC, int totaraCCLimit) {

        String totaraCCSQL = "select cc.id, u.email, u.firstname, u.lastname, cc.course, c.fullname, c.shortname from mdl_course_info_data ci, mdl_course c, mdl_course_completions cc, mdl_user u where cc.course=ci.courseid and cc.course=c.id and u.id = cc.userid and status=50 and ci.data='GPTE' and cc.id > ";
        StringBuilder totaraCCSQLBuilder = new StringBuilder(totaraCCSQL);
        totaraCCSQLBuilder.append(lastCC);
        totaraCCSQLBuilder.append(" order by cc.id desc");

        if(totaraCCLimit > 0)
            totaraCCSQLBuilder.append(" limit "+totaraCCLimit);
        logger.debug("getLatestCourseCompletions() sql = "+totaraCCSQLBuilder.toString());
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
                logger.info("getLatestCourseCompletions() "+totaraCCId+" :" +email+" : "+totaraCourseId+" : "+courseFullName+" : "+courseShortName);
                TotaraCourseCompletion tCC = new TotaraCourseCompletion(totaraCCId, email, firstName, lastName, totaraCourseId, courseFullName, courseShortName);
                sCourses.add(tCC);
            }
        }
        return sCourses;
    }

}
