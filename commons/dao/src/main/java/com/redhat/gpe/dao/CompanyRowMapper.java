package com.redhat.gpe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.redhat.gpe.domain.canonical.Company;

public class CompanyRowMapper extends BaseRowMapper implements RowMapper<Company> {

    public Company mapRow(ResultSet rs, int rowIndex) throws SQLException {

        Company company = super.getCompany(rs);
        return company;
    }

}
