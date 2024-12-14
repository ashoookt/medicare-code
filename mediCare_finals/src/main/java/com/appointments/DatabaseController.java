package com.appointments;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController {
    private final String url = "jdbc:mysql://localhost:3306/medicare_dp?autoReconnect=true";
    private final String user = "medic";
    private final String password = "1234";
    private Connection connection;

    public DatabaseController() {
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initializeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
