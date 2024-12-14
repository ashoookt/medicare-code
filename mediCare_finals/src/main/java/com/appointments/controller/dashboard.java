package com.appointments.controller;

import com.appointments.DatabaseController;
import com.appointments.model.medicare_db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class dashboard {
    @FXML
    private TextField pName;
    @FXML
    private TextField pAge;
    @FXML
    private TextField pAddress;
    @FXML
    private TextField pContacts;
    @FXML
    private TextArea pMedicals;
    @FXML
    private RadioButton pFemale;
    @FXML
    private RadioButton pMale;
    @FXML
    private RadioButton pOthers;
    @FXML
    private TableView<medicare_db> pTable;
    @FXML
    private TableColumn<medicare_db, String> pNamecol;
    @FXML
    private TableColumn<medicare_db, String> pAgecol;
    @FXML
    private TableColumn<medicare_db, String> pAddresscol;
    @FXML
    private TableColumn<medicare_db, String> pContactcol;
    @FXML
    private TableColumn<medicare_db, String> pMedicalcol;
    @FXML
    private TableColumn<medicare_db, String> pGendercol;
    @FXML
    private Pane mc_patients;
    @FXML
    private Pane patientsPane_button;

    ToggleGroup genderGroup;

    private DatabaseController con;

    private ObservableList<medicare_db> patients = FXCollections.observableArrayList();

    public void initialize() {
        con = new DatabaseController();

        //toggle group for gender
        genderGroup = new ToggleGroup();
        pMale.setToggleGroup(genderGroup);
        pFemale.setToggleGroup(genderGroup);
        pOthers.setToggleGroup(genderGroup);

        //table
        pNamecol.setCellValueFactory(new PropertyValueFactory<>("patient_name"));
        pAgecol.setCellValueFactory(new PropertyValueFactory<>("patient_age"));
        pAddresscol.setCellValueFactory(new PropertyValueFactory<>("patient_address"));
        pContactcol.setCellValueFactory(new PropertyValueFactory<>("patient_contacts"));
        pMedicalcol.setCellValueFactory(new PropertyValueFactory<>("meds_history"));
        pGendercol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        //hiding the fist page first
        mc_patients.setVisible(false);
        mc_patients.setManaged(false);

        //toggle button for the patient page
        patientsPane_button.setOnMouseClicked(event -> {
            if(mc_patients.isVisible()){
                mc_patients.setVisible(false);
                mc_patients.setManaged(false);
            }else {
                mc_patients.setVisible(true);
                mc_patients.setManaged(true);
            }
        });

        //zoom button effect for patients pane button
        patientsPane_button.setOnMouseEntered(event -> {
            patientsPane_button.setScaleX(1.1);
            patientsPane_button.setScaleY(1.1);
        });
        patientsPane_button.setOnMouseExited(event -> {
            patientsPane_button.setScaleX(1.0);
            patientsPane_button.setScaleY(1.0);
        });

        try {
            loadPatients();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String getSelectedGender() {
        if (pMale.isSelected()) {
            return "Male";
        } else if (pFemale.isSelected()) {
            return "Female";
        } else if (pOthers.isSelected()) {
            return "Others";
        } else {
            return "Unspecified";
        }
    }

    public void loadPatients() throws SQLException {
        String sql = "select * from patients";
        Statement stmt = con.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            medicare_db md = new medicare_db(
                    rs.getString("patient_name"),
                    rs.getString("patient_age"),
                    rs.getString("patient_address"),
                    rs.getString("patient_contacts"),
                    rs.getString("meds_history"),
                    rs.getString("gender")
            );
            patients.add(md);
        }
        pTable.setItems(patients);
    }

    @FXML
    private void save_button() throws SQLException {
        try {
            // Check if a patient is selected for editing
            medicare_db selectedPatient = pTable.getSelectionModel().getSelectedItem();

            // If a patient is selected, update the existing patient
            if (selectedPatient != null) {
                // SQL to update the selected patient's information in the database
                String sql = "UPDATE patients SET patient_name = ?, patient_age = ?, patient_address = ?, patient_contacts = ?, meds_history = ?, gender = ? WHERE patient_name = ? AND patient_age = ?";
                PreparedStatement stmt = con.getConnection().prepareStatement(sql);

                // Set the values from the text fields
                stmt.setString(1, pName.getText());
                stmt.setString(2, pAge.getText());
                stmt.setString(3, pAddress.getText());
                stmt.setString(4, pContacts.getText());
                stmt.setString(5, pMedicals.getText());
                stmt.setString(6, getSelectedGender());
                stmt.setString(7, selectedPatient.getPatient_name()); // Use the original name as identifier
                stmt.setString(8, selectedPatient.getPatient_age()); // Use the original age as identifier

                // Execute the update
                stmt.executeUpdate();

                // Update the existing patient object in the ObservableList
                selectedPatient.setPatient_name(pName.getText());
                selectedPatient.setPatient_age(pAge.getText());
                selectedPatient.setPatient_address(pAddress.getText());
                selectedPatient.setPatient_contacts(pContacts.getText());
                selectedPatient.setMeds_history(pMedicals.getText());
                selectedPatient.setGender(getSelectedGender());

                // Refresh the table to reflect the updated patient information
                pTable.refresh();

                //deselect from the table
                pTable.getSelectionModel().clearSelection();

                //clear of texts
                pName.clear();
                pAge.clear();
                pAddress.clear();
                pContacts.clear();
                pMedicals.clear();
                genderGroup.selectToggle(null);

                // Show a success message
                JOptionPane.showMessageDialog(null, "Patient updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // If no patient is selected, add a new patient
                String sql = "INSERT INTO patients(patient_name, patient_age, patient_address, patient_contacts, meds_history, gender) VALUES(?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = con.getConnection().prepareStatement(sql);

                stmt.setString(1, pName.getText());
                stmt.setString(2, pAge.getText());
                stmt.setString(3, pAddress.getText());
                stmt.setString(4, pContacts.getText());
                stmt.setString(5, pMedicals.getText());
                stmt.setString(6, getSelectedGender());

                stmt.executeUpdate();

                // Add to ObservableList
                medicare_db md = new medicare_db(
                        pName.getText(),
                        pAge.getText(),
                        pAddress.getText(),
                        pContacts.getText(),
                        pMedicals.getText(),
                        getSelectedGender()
                );
                patients.add(md);

                // Refresh table view
                pTable.refresh();

                //deselect from the table
                pTable.getSelectionModel().clearSelection();

                // Clear the fields after saving the new patient
                pName.clear();
                pAge.clear();
                pAddress.clear();
                pContacts.clear();
                pMedicals.clear();
                genderGroup.selectToggle(null);

                JOptionPane.showMessageDialog(null, "Data saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    @FXML
    private void logout_button() throws SQLException {
        // Confirm logout (optional)
        int confirmation = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to log out? This will close the application.",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            // Close database connection (optional but recommended)
            if (con != null && con.getConnection() != null) {
                try {
                    con.getConnection().close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.exit(0);
        }
    }

    @FXML
    private void delete_button() {
        try {
            // Get the selected item
            medicare_db selectedPatient = pTable.getSelectionModel().getSelectedItem();

            if (selectedPatient == null) {
                JOptionPane.showMessageDialog(null, "Please select a patient to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Confirmation dialog
            int confirmation = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to delete the selected patient?",
                    "Delete Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM patients WHERE patient_name = ? AND patient_age = ?";
                PreparedStatement stmt = con.getConnection().prepareStatement(sql);

                stmt.setString(1, selectedPatient.getPatient_name());
                stmt.setString(2, selectedPatient.getPatient_age());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    patients.remove(selectedPatient);
                    pTable.refresh();
                    JOptionPane.showMessageDialog(null, "Patient deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Patient not found in the database", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @FXML
    private void edit_button() {
        // Get the selected patient from the table
        medicare_db selectedPatient = pTable.getSelectionModel().getSelectedItem();

        if (selectedPatient != null) {
            // Populate the text fields with the selected patient's information
            pName.setText(selectedPatient.getPatient_name());
            pAge.setText(selectedPatient.getPatient_age());
            pAddress.setText(selectedPatient.getPatient_address());
            pContacts.setText(selectedPatient.getPatient_contacts());
            pMedicals.setText(selectedPatient.getMeds_history());

            // Set the gender radio button
            switch (selectedPatient.getGender()) {
                case "Male":
                    pMale.setSelected(true);
                    break;
                case "Female":
                    pFemale.setSelected(true);
                    break;
                case "Others":
                    pOthers.setSelected(true);
                    break;
            }
        } else {
            // Show a warning if no patient is selected
            JOptionPane.showMessageDialog(null, "Please select a patient to edit", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}


