package com.redhat.gpe.coursecompletion.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class TotaraShadowDAOImpl implements TotaraShadowDAO {

    private static final String TOTARA_SHADOW_DB_TEST_SQL = "totara_shadow_db_test_sql";

    @Autowired
    private JdbcTemplate tsJdbcTemplate;

    public int countNewCourseCompletions(int lastCC) {

        String testSql = System.getProperty(TOTARA_SHADOW_DB_TEST_SQL);
        int ccCount = tsJdbcTemplate.queryForInt(testSql);
        return ccCount;
    }

}
