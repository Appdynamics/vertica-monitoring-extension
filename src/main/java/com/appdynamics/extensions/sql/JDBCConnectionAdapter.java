package com.appdynamics.extensions.sql;


import java.sql.*;
import java.util.Properties;
import java.util.PropertyPermission;
import com.google.common.base.Strings;


public class JDBCConnectionAdapter {

    private final String connUrl;
    private final String user;
    private final String password;



    private JDBCConnectionAdapter(String connStr,String user, String password ){
        this.connUrl = connStr;
        this.user = user;
        this.password = password;

    }

    static JDBCConnectionAdapter create(String connUrl, String user, String password){
        return new JDBCConnectionAdapter(connUrl, user, password);
    }

    Connection open(String driver) throws SQLException, ClassNotFoundException {
        Connection connection;
        //System.out.println()
        Class.forName(driver);

        Properties properties = new Properties();
        properties.put("ReadOnly", "true");
        if (!Strings.isNullOrEmpty(user)) {
            properties.put("user", user);
        }
        if (!Strings.isNullOrEmpty(password)) {
            properties.put("password", password);
        }

        connection = DriverManager.getConnection(connUrl,properties);
        return connection;
    }

    ResultSet queryDatabase(Connection connection, String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    void closeStatement(Statement statement) throws Exception{
        statement.close();
    }

    void closeConnection(Connection connection) throws Exception {
        connection.close();
    }
}
