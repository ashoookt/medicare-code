package com.appointments.model;

public class medicalrecords_db {
    private String patient_name;
    private String doctor_name;
    private String ward;
    private String medication;
    private String diagnosis;

    public medicalrecords_db(String patient_name, String doctor_name, String ward, String medication, String diagnosis) {
        this.patient_name = patient_name;
        this.doctor_name = doctor_name;
        this.ward = ward;
        this.medication = medication;
        this.diagnosis = diagnosis;
    }
    public String getPatient_name() {
        return patient_name;
    }
    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }
    public String getDoctor_name() {
        return doctor_name;
    }
    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }
    public String getWard() {
        return ward;
    }
    public void setWard(String ward) {
        this.ward = ward;
    }
    public String getMedication() {
        return medication;
    }
    public void setMedication(String medication) {
        this.medication = medication;
    }
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
