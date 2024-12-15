package com.appointments.controller;

import com.appointments.DatabaseController;
import com.appointments.model.medicalrecords_db;
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

    //fxml for the patient page
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
    @FXML
    private Button logoutButton;

    //fxml for the medical records page
    @FXML
    private Pane mc_medrecs;
    @FXML
    private Pane medrecordsPane_button;
    @FXML
    private TextField docNameText;
    @FXML
    private TextField wardText;
    @FXML
    private TextArea medicText;
    @FXML
    private TextArea diagnoseText;
    @FXML
    private TableView<medicalrecords_db> mrTable;
    @FXML
    private TableColumn<medicalrecords_db, String> colPatientName;
    @FXML
    private TableColumn<medicalrecords_db, String> colDoctorName;
    @FXML
    private TableColumn<medicalrecords_db, String> colWard;
    @FXML
    private TableColumn<medicalrecords_db, String> colMedication;
    @FXML
    private TableColumn<medicalrecords_db, String> colDiagnosis;

    ToggleGroup genderGroup;

    private DatabaseController con;

    private ObservableList<medicare_db> patients = FXCollections.observableArrayList();
    private ObservableList<medicalrecords_db> patientsRecord = FXCollections.observableArrayList();

    public void initialize() {
        con = new DatabaseController();

        //toggle group for gender
        genderGroup = new ToggleGroup();
        pMale.setToggleGroup(genderGroup);
        pFemale.setToggleGroup(genderGroup);
        pOthers.setToggleGroup(genderGroup);

        //table for patient page
        pNamecol.setCellValueFactory(new PropertyValueFactory<>("patient_name"));
        pAgecol.setCellValueFactory(new PropertyValueFactory<>("patient_age"));
        pAddresscol.setCellValueFactory(new PropertyValueFactory<>("patient_address"));
        pContactcol.setCellValueFactory(new PropertyValueFactory<>("patient_contacts"));
        pMedicalcol.setCellValueFactory(new PropertyValueFactory<>("meds_history"));
        pGendercol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        //table for medical records page
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patient_name"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctor_name"));
        colWard.setCellValueFactory(new PropertyValueFactory<>("ward"));
        colMedication.setCellValueFactory(new PropertyValueFactory<>("medication"));
        colDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        //hiding the fist page first
        mc_patients.setVisible(false);
        mc_patients.setManaged(false);

        //hiding the second page
        mc_medrecs.setVisible(false);
        mc_medrecs.setManaged(false);

        // Toggle button for the medical history page
        medrecordsPane_button.setOnMouseClicked(event -> {
            // If mc_patients is visible, hide it first
            if (mc_patients.isVisible()) {
                mc_patients.setVisible(false);
                mc_patients.setManaged(false);
            }

            // Toggle visibility for mc_medrecs
            if (mc_medrecs.isVisible()) {
                mc_medrecs.setVisible(false);
                mc_medrecs.setManaged(false);
            } else {
                mc_medrecs.setVisible(true);
                mc_medrecs.setManaged(true);
            }
        });

        // Toggle button for the patient page
        patientsPane_button.setOnMouseClicked(event -> {
            // If mc_medrecs is visible, hide it first
            if (mc_medrecs.isVisible()) {
                mc_medrecs.setVisible(false);
                mc_medrecs.setManaged(false);
            }

            // Toggle visibility for mc_patients
            if (mc_patients.isVisible()) {
                mc_patients.setVisible(false);
                mc_patients.setManaged(false);
            } else {
                mc_patients.setVisible(true);
                mc_patients.setManaged(true);
            }
        });

        //zoom button effect for medical records button
        medrecordsPane_button.setOnMouseEntered(event -> {
           medrecordsPane_button.setScaleX(1.1);
           medrecordsPane_button.setScaleY(1.1);
        });
        medrecordsPane_button.setOnMouseExited(event -> {
            medrecordsPane_button.setScaleX(1.0);
            medrecordsPane_button.setScaleY(1.0);
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

        //zoom effect for the logout button
        logoutButton.setOnMouseEntered(event -> {
            logoutButton.setScaleX(1.1); // Increase scale on hover
            logoutButton.setScaleY(1.1);
        });
        logoutButton.setOnMouseExited(event -> {
            logoutButton.setScaleX(1.0); // Reset scale on mouse exit
            logoutButton.setScaleY(1.0);
        });

        try {
            loadPatients();
            loadPatientRecords();
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

    //table to load from sql to table of patients
    public void loadPatients() throws SQLException {
        String sql = "select * from patientTabledb";
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

    //table to load from sql to table of medical records
    public void loadPatientRecords() throws SQLException {
        // Clear the existing records to prevent duplicates
        patientsRecord.clear();

        String sql = "SELECT patient_name, doctor_name, ward, medication, diagnosis FROM patientsRecord";
        Statement stmt = con.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            medicalrecords_db mrd = new medicalrecords_db(
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getString("ward"),
                    rs.getString("medication"),
                    rs.getString("diagnosis")
            );
            patientsRecord.add(mrd);
        }
        mrTable.setItems(patientsRecord);
    }


    @FXML
    private void save_button() throws SQLException {
        try {
            // Check if a patient is selected for editing
            medicare_db selectedPatient = pTable.getSelectionModel().getSelectedItem();

            // If a patient is selected, update the existing patient
            if (selectedPatient != null) {
                // SQL to update the selected patient's information in the database
                String sql = "UPDATE patientTabledb SET patient_name = ?, patient_age = ?, patient_address = ?, patient_contacts = ?, meds_history = ?, gender = ? WHERE patient_name = ? AND patient_age = ?";
                PreparedStatement stmt = con.getConnection().prepareStatement(sql);

                // Set the values from the text fields
                stmt.setString(1, pName.getText());
                stmt.setString(2, pAge.getText());
                stmt.setString(3, pAddress.getText());
                stmt.setString(4, pContacts.getText());
                stmt.setString(5, pMedicals.getText());
                stmt.setString(6, getSelectedGender());
                stmt.setString(7, selectedPatient.getPatient_name());
                stmt.setString(8, selectedPatient.getPatient_age());

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

                // Load the updated medical records from the database
                loadPatientRecords();

                // Deselect from the table
                pTable.getSelectionModel().clearSelection();

                // Clear the text fields
                pName.clear();
                pAge.clear();
                pAddress.clear();
                pContacts.clear();
                pMedicals.clear();
                genderGroup.selectToggle(null);

                JOptionPane.showMessageDialog(null, "Patient updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // If no patient is selected, add a new patient
                String sql = "INSERT INTO patientTabledb(patient_name, patient_age, patient_address, patient_contacts, meds_history, gender) VALUES(?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = con.getConnection().prepareStatement(sql);

                stmt.setString(1, pName.getText());
                stmt.setString(2, pAge.getText());
                stmt.setString(3, pAddress.getText());
                stmt.setString(4, pContacts.getText());
                stmt.setString(5, pMedicals.getText());
                stmt.setString(6, getSelectedGender());
                stmt.executeUpdate();

                // Insert data into the medical records table
                String sql2 = "INSERT INTO patientsrecord(patient_name) VALUES(?)";
                PreparedStatement stmt2 = con.getConnection().prepareStatement(sql2);
                stmt2.setString(1, pName.getText());
                stmt2.executeUpdate();

                // Add to ObservableList for patients table
                medicare_db md = new medicare_db(
                        pName.getText(),
                        pAge.getText(),
                        pAddress.getText(),
                        pContacts.getText(),
                        pMedicals.getText(),
                        getSelectedGender()
                );
                patients.add(md);

                // Refresh the patient table view
                pTable.refresh();

                // Load the updated medical records from the database
                loadPatientRecords();

                // Deselect from the table
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
        // Confirm logout
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
                // Delete from patientTabledb
                String sql = "DELETE FROM patientTabledb WHERE patient_name = ? AND patient_age = ?";
                PreparedStatement stmt = con.getConnection().prepareStatement(sql);
                stmt.setString(1, selectedPatient.getPatient_name());
                stmt.setString(2, selectedPatient.getPatient_age());

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Remove the patient from the ObservableList for the first page
                    patients.remove(selectedPatient);
                    pTable.refresh();

                    // Also delete from patientsRecord table
                    String sql2 = "DELETE FROM patientsRecord WHERE patient_name = ?";
                    PreparedStatement stmt2 = con.getConnection().prepareStatement(sql2);
                    stmt2.setString(1, selectedPatient.getPatient_name());
                    stmt2.executeUpdate();

                    // Remove the patient from the ObservableList for the second page
                    patientsRecord.removeIf(record -> record.getPatient_name().equals(selectedPatient.getPatient_name()));
                    mrTable.refresh();

                    // Clear the selection
                    pTable.getSelectionModel().clearSelection();
                    mrTable.getSelectionModel().clearSelection();

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
            JOptionPane.showMessageDialog(null, "Please select a patient to edit", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    @FXML
    private void mrSaveButton() {
        try {
            // Get the selected patient record from the table
            medicalrecords_db selectedRecord = mrTable.getSelectionModel().getSelectedItem();

            if (selectedRecord != null) {
                // SQL to update the selected record
                String sql = "UPDATE patientsRecord SET doctor_name = ?, ward = ?, medication = ?, diagnosis = ? WHERE patient_name = ?";
                PreparedStatement stmt = con.getConnection().prepareStatement(sql);

                // Set the values from the text fields
                stmt.setString(1, docNameText.getText());
                stmt.setString(2, wardText.getText());
                stmt.setString(3, medicText.getText());
                stmt.setString(4, diagnoseText.getText());
                stmt.setString(5, selectedRecord.getPatient_name());

                // Execute the update
                stmt.executeUpdate();

                // Update the existing record in the ObservableList
                selectedRecord.setDoctor_name(docNameText.getText());
                selectedRecord.setWard(wardText.getText());
                selectedRecord.setMedication(medicText.getText());
                selectedRecord.setDiagnosis(diagnoseText.getText());

                // Refresh the table to reflect the updated record
                mrTable.refresh();

                // Clear the text fields
                docNameText.clear();
                wardText.clear();
                medicText.clear();
                diagnoseText.clear();

                // Deselect the table
                mrTable.getSelectionModel().clearSelection();

                JOptionPane.showMessageDialog(null, "Record updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a record to update", "Warning", JOptionPane.WARNING_MESSAGE);
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
    private void mrDeleteButton() {
        try {
            medicalrecords_db selectedRecord = mrTable.getSelectionModel().getSelectedItem();

            if (selectedRecord != null) {
                int confirmation = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete this record?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirmation == JOptionPane.YES_OPTION) {
                    // SQL to delete the selected record's associated details, excluding patient_name
                    String sql = "UPDATE patientsRecord SET doctor_name = NULL, ward = NULL, medication = NULL, diagnosis = NULL WHERE patient_name = ?";
                    PreparedStatement stmt = con.getConnection().prepareStatement(sql);

                    stmt.setString(1, selectedRecord.getPatient_name());

                    stmt.executeUpdate();

                    selectedRecord.setDoctor_name(null);
                    selectedRecord.setWard(null);
                    selectedRecord.setMedication(null);
                    selectedRecord.setDiagnosis(null);

                    // Refresh the table to reflect changes
                    mrTable.refresh();

                    JOptionPane.showMessageDialog(null, "Record cleared successfully (patient_name retained)", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a record to clear", "Warning", JOptionPane.WARNING_MESSAGE);
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
    private void mrEditButton() {
        medicalrecords_db selectedRecord = mrTable.getSelectionModel().getSelectedItem();

        if (selectedRecord != null) {
            docNameText.setText(selectedRecord.getDoctor_name());
            wardText.setText(selectedRecord.getWard());
            medicText.setText(selectedRecord.getMedication());
            diagnoseText.setText(selectedRecord.getDiagnosis());
        } else {
            JOptionPane.showMessageDialog(null, "Please select a record to edit", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }


}


