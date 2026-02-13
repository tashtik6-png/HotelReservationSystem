package com.astana.dao;
import java.sql.*;

public class DatabaseConfig {
    private static Connection connection = null;
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_astana";
    private static final String USER = "root";
    private static final String PASS = "Gadzelkin123";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }
}
