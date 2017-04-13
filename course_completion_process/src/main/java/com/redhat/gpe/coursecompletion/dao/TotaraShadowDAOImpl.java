package com.redhat.gpe.coursecompletion.dao;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import org.apache.log4j.Logger;

import com.redhat.gpe.domain.canonical.StudentCourse;
import com.redhat.gpe.domain.helper.CourseCompletion;


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

    public List<CourseCompletion> getLatestCourseCompletions(int lastCC) {

        List<CourseCompletion> sCourses = null;
        String totaraCCSQL = "select cc.id, u.email, cc.course, c.fullname, c.shortname from mdl_course_info_data ci, mdl_course c, mdl_course_completions cc, mdl_user u where cc.course=ci.courseid and cc.course=c.id and u.id = cc.userid and status=50 and ci.data='GPTE' order by cc.id desc;";
        SqlRowSet rowSet = tsJdbcTemplate.queryForRowSet(totaraCCSQL);
        if(!rowSet.last()) {
            while(rowSet.next()) {
                int totaraCCId = rowSet.getInt(1);
                String email = rowSet.getString(2);
                String totaraCourseId = rowSet.getString(3);
                String courseFullName = rowSet.getString(4);
                String courseShortName = rowSet.getString(5);
                logger.info("getLatestCourseCompletions() "+totaraCCId+" :" +email+" : "+totaraCourseId+" : "+courseFullName+" : "+courseShortName);
            }
            sCourses = new ArrayList<CourseCompletion>();
        }else{
            sCourses = new ArrayList<CourseCompletion>();
        }
        return sCourses;

    }

}
