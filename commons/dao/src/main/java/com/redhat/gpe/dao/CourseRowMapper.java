package com.redhat.gpe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.redhat.gpe.domain.canonical.Course;

public class CourseRowMapper extends BaseRowMapper implements RowMapper<Course> {

    public Course mapRow(ResultSet rs, int rowIndex) throws SQLException {

        Course course = super.getCourse(rs);
        return course;
    }

}
