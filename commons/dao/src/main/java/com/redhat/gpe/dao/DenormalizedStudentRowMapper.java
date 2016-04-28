package com.redhat.gpe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Language;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentCourse;
import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpe.domain.helper.DenormalizedStudent;

public class DenormalizedStudentRowMapper extends BaseRowMapper implements RowMapper<DenormalizedStudent> {

    public DenormalizedStudent mapRow(ResultSet rs, int rowIndex) throws SQLException {

        Student student = super.getStudent(rs);
        Company company = super.getCompany(rs);
      
        DenormalizedStudent dStudent = new DenormalizedStudent();
        dStudent.setStudentObj(student);
        dStudent.setCompanyObj(company);
        
        return dStudent;
    }

}
