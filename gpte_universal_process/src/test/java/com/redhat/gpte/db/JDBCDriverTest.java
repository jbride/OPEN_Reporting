package com.redhat.gpte.db;

import java.net.*;
import java.sql.DriverManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.redhat.gpte.util.PropertiesSupport;


/*
 * Usage:       mvn clean test -Dprops_file_location=../properties/prod.properties
 */
public class JDBCDriverTest {

    @Before
    public void init() throws java.io.IOException {
        PropertiesSupport.setupProps();
    }

    @Ignore
    @Test
    public void jbdcTest() {
        String className = System.getProperty("lms_transactional_driverClassName");
        String url = System.getProperty("lms_transactional_url");
        String userId = System.getProperty("lms_transactional_username");
        String password = System.getProperty("lms_transactional_password");

        java.sql.Connection conn = null;
        try {

            Class.forName(className).newInstance();
            conn = DriverManager.getConnection(url, userId, password);
            System.out.println("connection = " + conn);
            conn.setAutoCommit(false);
            java.sql.Statement statement = conn.createStatement();
            java.sql.ResultSet resultSet = statement.executeQuery("show tables");
            while (resultSet.next()) {
                System.out.println("\t"+resultSet.getString(1));
            }

	} catch (Exception x) {
            x.printStackTrace();
        }

    }
}

