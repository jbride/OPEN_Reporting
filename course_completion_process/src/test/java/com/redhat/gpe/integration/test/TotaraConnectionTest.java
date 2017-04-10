package com.redhat.gpe.integration.test;

import java.sql.DriverManager;
import java.net.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.redhat.gpte.util.PropertiesSupport;

public class TotaraConnectionTest {

    private static final String TOTARA_SHADOW_DB_DRIVER_CLASS_NAME = "totara_shadow_db_driverClassName";
    private static final String TOTARA_SHADOW_DB_URL = "totara_shadow_db_url";
    private static final String TOTARA_SHADOW_DB_USERNAME = "totara_shadow_db_username";
    private static final String TOTARA_SHADOW_DB_PASSWORD = "totara_shadow_db_password";
    private static final String TOTARA_SHADOW_DB_CONNECTION_PARAMS = "totara_shadow_db_connection_params";
    private static final String TOTARA_SHADOW_DB_TEST_SQL = "totara_shadow_db_test_sql";

    private String driverClassName;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
    private String connectionParams;
    private String testSql;

    @Before
    public void setup() throws java.io.IOException {
        PropertiesSupport.setupProps();
        driverClassName = System.getProperty(TOTARA_SHADOW_DB_DRIVER_CLASS_NAME);

        dbUrl = System.getProperty(TOTARA_SHADOW_DB_URL);
        dbUserName = System.getProperty(TOTARA_SHADOW_DB_USERNAME);
        dbPassword = System.getProperty(TOTARA_SHADOW_DB_PASSWORD);
        connectionParams = System.getProperty(TOTARA_SHADOW_DB_CONNECTION_PARAMS);
        testSql = System.getProperty(TOTARA_SHADOW_DB_TEST_SQL);

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("\n\ndriverClassName = "+driverClassName);
        sBuilder.append("\ndbUrl = "+dbUrl);
        sBuilder.append("\ndbUserName = "+dbUserName);
        sBuilder.append("\ndbPassword = "+dbPassword);
        sBuilder.append("\nconnectionParams = "+connectionParams);
        sBuilder.append("\ntestSql = "+testSql);
        sBuilder.append("\n\n");
        System.out.println(sBuilder.toString());
    }

    @Ignore
    @Test
    public void testTotaraConnectionsTest() {
        java.sql.Connection conn = null;
        try {
            Class.forName(driverClassName).newInstance();
            conn = DriverManager.getConnection(dbUrl+"?"+connectionParams, dbUserName, dbPassword);
    
            System.out.println("connection = " + conn);
            conn.setAutoCommit(false);
            java.sql.Statement statement = conn.createStatement();
            java.sql.ResultSet rsObj = statement.executeQuery(testSql);
            rsObj.next();
            System.out.println("testTotaraConnectionsTest() resultSet = "+rsObj.getInt(1));
            conn.commit();
        }catch(Exception x) {
            x.printStackTrace();
        }finally {
            if(conn != null) {
                try { conn.close(); }catch(Exception e) { }
            }
        }
    }

}
