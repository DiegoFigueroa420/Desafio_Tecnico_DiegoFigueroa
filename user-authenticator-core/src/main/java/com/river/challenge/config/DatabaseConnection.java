package com.river.challenge.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/user_management";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin123";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de PostgreSQL no encontrado", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
