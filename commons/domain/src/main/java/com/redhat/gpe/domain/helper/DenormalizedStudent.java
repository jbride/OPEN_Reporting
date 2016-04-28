package com.redhat.gpe.domain.helper;

import com.redhat.gpe.domain.canonical.Company;
import com.redhat.gpe.domain.canonical.Student;

public class DenormalizedStudent {
    
    private Student studentObj;
    private Company companyObj;
    
    
    public Student getStudentObj() {
        return studentObj;
    }
    public void setStudentObj(Student studentObj) {
        this.studentObj = studentObj;
    }
    public Company getCompanyObj() {
        return companyObj;
    }
    public void setCompanyObj(Company companyObj) {
        this.companyObj = companyObj;
    }

}
