package com.redhat.gpe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Language;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentCourse;
import com.redhat.gpe.domain.helper.CourseCompletion;

public class DenormalizedStudentCourseRowMapper extends BaseRowMapper implements RowMapper<CourseCompletion> {

    public CourseCompletion mapRow(ResultSet rs, int rowIndex) throws SQLException {

        Student student = super.getStudent(rs);
        Course course = super.getCourse(rs);
        Language language = super.getLanguage(rs);
        StudentCourse sCourse = super.getStudentCourse(rs);
        CourseCompletion courseCompletion = new CourseCompletion(student, course, language, sCourse);
        
        return courseCompletion;
    }

}
