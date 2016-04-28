package com.redhat.gpe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.redhat.gpe.domain.canonical.Student;

public class StudentRowMapper extends BaseRowMapper implements RowMapper<Student> {

    public Student mapRow(ResultSet rs, int rowIndex) throws SQLException {

        Student sCourse = super.getStudent(rs);
        return sCourse;
    }

}
