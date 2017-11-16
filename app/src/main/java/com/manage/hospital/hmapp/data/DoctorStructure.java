package com.manage.hospital.hmapp.data;

import org.json.JSONException;
import org.json.JSONObject;



public class DoctorStructure {

    public String doctor_fname;
    public String doctor_lname;
    public String doctor_spec;
    public String license_number;
    public String dob;
    public String doc_id;
    public String gender;
    public String email;
    public String contact_num;
    public String address;



    public String getDoctor_fname() {
        return doctor_fname;
    }

    public String getDoctor_lname() { return doctor_lname; }

    public String getEmail() {
        return email;
    }

    public String getContact_num() {
        return contact_num;
    }

    public String getSpectialization() {
        return doctor_spec;
    }

    public String getAddress() {
        return address;
    }

    public String getLicense_num() {
        return license_number;
    }

    public String getDob() { return dob;}

    public String getGender() {return gender;}

    public String getDocId() {return doc_id;}

    public DoctorStructure(JSONObject jsonObject){

        try {
            doc_id=jsonObject.getString("Doctor_Id");
            doctor_fname=jsonObject.getString("Firstname");
            doctor_lname=jsonObject.getString("Lastname");
            doctor_spec=jsonObject.getString("Speciality");
            email=jsonObject.getString("Email_Id");
            dob=jsonObject.getString("DOB");
            license_number=jsonObject.getString("LicenseNumber");
            contact_num=jsonObject.getString("ContactNo");
            address=jsonObject.getString("Address");
            gender=jsonObject.getString("Gender");


        }catch (JSONException e){
            e.printStackTrace();
        }




    }
}
