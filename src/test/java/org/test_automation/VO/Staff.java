package org.test_automation.VO;


import java.util.List;

public class Staff {
    private List<String> doctorFirstNames;
    private List<String> doctorLastNames;
    private String salutation;
    private String gender;
    private String dob;
    private String phoneNumber;
    private String emailSuffix;
    private String designation;
    private String password;
    private String role;

    private String staffCode;
    // Getters and Setters


    public List<String> getDoctorFirstNames() {
        return doctorFirstNames;
    }

    public void setDoctorFirstNames(List<String> doctorFirstNames) {
        this.doctorFirstNames = doctorFirstNames;
    }

    public List<String> getDoctorLastNames() {
        return doctorLastNames;
    }

    public void setDoctorLastNames(List<String> doctorLastNames) {
        this.doctorLastNames = doctorLastNames;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailSuffix() {
        return emailSuffix;
    }

    public void setEmailSuffix(String emailSuffix) {
        this.emailSuffix = emailSuffix;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }
}
