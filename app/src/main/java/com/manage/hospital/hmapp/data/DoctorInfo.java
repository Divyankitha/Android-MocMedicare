package com.manage.hospital.hmapp.data;



public class DoctorInfo
{
    public String fname,lname,DOB,email,gender,contact,license,speciality,address,Username,Password;
    public int ID;

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setFname(String fname)
    {
        this.fname = fname;
    }

    public void setLname(String lname)
    {
        this.lname = lname;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
