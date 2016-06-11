package com.redhat.gpe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.redhat.gpe.domain.canonical.Language;

public class LanguagesRowMapper extends BaseRowMapper implements RowMapper<Language> {

    public Language mapRow(ResultSet rs, int rowIndex) throws SQLException {

        Language langObj = super.getLanguage(rs);
        return langObj;
    }

}
