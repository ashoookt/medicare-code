package com.appointments.model;

public class medicare_db {
    private String patient_name;
    private String patient_age;
    private String patient_address;
    private String patient_contacts;
    private String meds_history;
    private String gender;

    public medicare_db(String patient_name, String patient_age, String patient_address, String patient_contacts, String meds_history, String gender) {
        this.patient_name = patient_name;
        this.patient_age = patient_age;
        this.patient_address = patient_address;
        this.patient_contacts = patient_contacts;
        this.meds_history = meds_history;
        this.gender = gender;
    }
    public String getPatient_name() {
        return patient_name;
    }
    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }
    public String getPatient_age() {
        return patient_age;
    }
    public void setPatient_age(String patient_age) {
        this.patient_age = patient_age;
    }
    public String getPatient_address() {
        return patient_address;
    }
    public void setPatient_address(String patient_address) {
        this.patient_address = patient_address;
    }
    public String getPatient_contacts() {
        return patient_contacts;
    }
    public void setPatient_contacts(String patient_contacts) {
        this.patient_contacts = patient_contacts;
    }
    public String getMeds_history() {
        return meds_history;
    }
    public void setMeds_history(String meds_history) {
        this.meds_history = meds_history;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
}
