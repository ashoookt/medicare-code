package com.appointments.controller;

import com.appointments.DatabaseController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class logIn {
    @FXML
    private TextField idnum_text;
    @FXML
    private PasswordField pass_text;

    private final DatabaseController dbc = new DatabaseController();

    @FXML
    public void initialize() {
    }

    @FXML
    private void logIN_button() {
        String username = idnum_text.getText();
        String password = pass_text.getText();

        if (validateUser(username, password)) {
            JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mc_dashboard.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) idnum_text.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to load the dashboard.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = dbc.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                return password.equals(storedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
