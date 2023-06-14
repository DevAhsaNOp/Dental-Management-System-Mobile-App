package com.csharpui.myloginform;

import java.util.ArrayList;

public class PatientModel {
    private String PatientName, PatientPhone, PatientId, PatientEmail, PatientCity, PatientGender;
    private ArrayList<String> ImageUrls;

    // we need empty constructor
    public PatientModel() {
    }

    public PatientModel(String patientName, String patientPhone, String patientId, String patientEmail,
                        String patientCity, String patientGender, ArrayList<String> imageUrls) {
        PatientName = patientName;
        PatientPhone = patientPhone;
        PatientId = patientId;
        PatientEmail = patientEmail;
        PatientCity = patientCity;
        PatientGender = patientGender;
        ImageUrls = imageUrls;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }

    public String getPatientPhone() {
        return PatientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        PatientPhone = patientPhone;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }

    public ArrayList<String> getImageUrls() {
        return ImageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        ImageUrls = imageUrls;
    }

    public String getPatientEmail() { return PatientEmail; }

    public void setPatientEmail(String patientEmail) { PatientEmail = patientEmail; }

    public String getPatientGender() { return PatientGender; }

    public void setPatientGender(String patientGender) { PatientGender = patientGender; }

    public String getPatientCity() { return PatientCity; }

    public void setPatientCity(String patientCity) { PatientCity = patientCity; }
}
