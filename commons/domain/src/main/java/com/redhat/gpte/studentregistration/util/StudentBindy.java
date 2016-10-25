package com.redhat.gpte.studentregistration.util;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import com.redhat.gpe.domain.canonical.Student;
import com.redhat.gpe.domain.canonical.Company;

@CsvRecord(separator=",", skipFirstLine=true)
public class StudentBindy {
    
    private static final String REGION_DELIMITER = "|";
    private static final String NAME_DELIMITER = " ";
    private static final String PARTNER_DELIMITER = ".";
    private static final String SFDC_DELIMITER = ":";

    @DataField(pos=1, required=true)
    private String email;

    @DataField(pos=2, required=true)
    private String firstName;

    @DataField(pos=3, required=true)
    private String lastName;

    @DataField(pos=4, required=true)
    private String company;

    @DataField(pos=5, required=true)
    private String regionCode;
    
    @DataField(pos=6, required=true)
    private String country;

    @DataField(pos=7, required=true)
    private String role;
    
    public StudentBindy() {  
    }

    public Student convertToCanonicalStudent() {
        Student sObj = new Student();
        return sObj;
    }
    
}
