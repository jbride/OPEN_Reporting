package com.redhat.gpe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.redhat.gpe.domain.canonical.StudentCourse;
import com.redhat.gpe.domain.canonical.Student;

public class StudentCourseRowMapper extends BaseRowMapper implements RowMapper<StudentCourse> {

    public StudentCourse mapRow(ResultSet rs, int rowIndex) throws SQLException {

        StudentCourse sCourse = super.getStudentCourse(rs);
        return sCourse;
    }

}
