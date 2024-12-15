package com.example.speedyserve;

public class Chef {

    private String Area, City, ConfirmPassword, Emailid, Fname, House, Lname, Mobile, Password, Postcode, State, Suburban;

    // Constructor with parameters
    public Chef(String area, String city, String confirmPassword, String emailid, String fname, String house, String lname, String mobile, String password, String postcode, String state, String suburban) {
        this.Area = area;
        City = city;
        ConfirmPassword = confirmPassword;
        Emailid = emailid;
        Fname = fname;
        House = house;
        Lname = lname;
        Mobile = mobile;
        Password = password;
        Postcode = postcode;
        State = state;
        Suburban = suburban; // Added suburban to constructor
    }

    // Default constructor
    public Chef() {
    }

    // Getters
    public String getArea() {
        return Area;
    }

    public String getCity() {
        return City;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public String getEmailid() {
        return Emailid;
    }

    public String getFname() {
        return Fname;
    }

    public String getHouse() {
        return House;
    }

    public String getLname() {
        return Lname;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getPassword() {
        return Password;
    }

    public String getPostcode() {
        return Postcode;
    }

    public String getState() {
        return State;
    }

    public String getSuburban() {  // Added the getter for suburban
        return Suburban;
    }

    // Setters
    public void setArea(String area) {
        this.Area = area;
    }

    public void setCity(String city) {
        this.City = city;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.ConfirmPassword = confirmPassword;
    }

    public void setEmailid(String emailid) {
        this.Emailid = emailid;
    }

    public void setFname(String fname) {
        this.Fname = fname;
    }

    public void setHouse(String house) {
        this.House = house;
    }

    public void setLname(String lname) {
        this.Lname = lname;
    }

    public void setMobile(String mobile) {
        this.Mobile = mobile;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public void setPostcode(String postcode) {
        this.Postcode = postcode;
    }

    public void setState(String state) {
        this.State = state;
    }

    public void setSuburban(String suburban) {  // Added setter for suburban
        this.Suburban = suburban;
    }
}
