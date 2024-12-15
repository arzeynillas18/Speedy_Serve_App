package com.example.speedyserve;
public class Customer {

    private String city, FirstName, LastName, Password, ConfirmPassword, EmailId, MobileNo, State, Area, LocalAddress, Suburban;

    public Customer() {
    }

    // Constructor
    public Customer(String city, String firstName, String lastName, String password, String confirmPassword, String emailId, String mobileNo, String state, String area, String localAddress, String suburban) {
        this.city = city;
        FirstName = firstName;
        LastName = lastName;
        Password = password;
        ConfirmPassword = confirmPassword;
        EmailId = emailId;
        MobileNo = mobileNo;
        State = state;
        Area = area;
        LocalAddress = localAddress;
        Suburban = suburban;  // Set suburban in constructor
    }

    // Getter and Setter methods for all fields

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        ConfirmPassword = confirmPassword;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getLocalAddress() {
        return LocalAddress;
    }

    public void setLocalAddress(String localAddress) {
        LocalAddress = localAddress;
    }

    // Add the getter and setter for Suburban
    public String getSuburban() {
        return Suburban;
    }

    public void setSuburban(String suburban) {
        Suburban = suburban;
    }
}
