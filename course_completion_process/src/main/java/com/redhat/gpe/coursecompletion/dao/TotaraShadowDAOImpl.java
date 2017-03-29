package com.redhat.gpe.coursecompletion.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class TotaraShadowDAOImpl implements TotaraShadowDAO {

    @Autowired
    private JdbcTemplate tsJdbcTemplate;

    public int countNewCourseCompletions(int lastCC) {
        return 0;
    }

}
