package com.redhat.gpe.dao;

import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpe.domain.canonical.Course;
import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.StudentAccreditation;
import com.redhat.gpe.domain.helper.Accreditation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DenormalizedStudentAccreditationRowMapper extends BaseRowMapper implements RowMapper<Accreditation> {

    public Accreditation mapRow(ResultSet rs, int rowIndex) throws SQLException {

        AccreditationDefinition accredObj = super.getAccreditation(rs);
        Student studentObj = super.getStudent(rs);
        Course courseObj = super.getCourse(rs);
        StudentAccreditation sAccredObj = super.getStudentAccreditation(rs);
        
        Accreditation dsAccredObj = new Accreditation();

        dsAccredObj.setAccreditation(accredObj);
        dsAccredObj.setStudent(studentObj);
        dsAccredObj.setCourse(courseObj);
        dsAccredObj.setStudentAccred(sAccredObj);
        return dsAccredObj;
    }

}
