package com.manage.hospital.hmapp.data;

import org.json.JSONException;
import org.json.JSONObject;


public class PatientStructure {

    public String patient_id;
    public String patient_fname;
    public String patient_lname;
    public String dob;
    public String gender;
    public String weight;
    public String age;
    public String email;
    public String contact_num;
    public String address;

    public String getPatient_id() {
        return patient_id;
    }

    public String getPatient_fname() {
        return patient_fname;
    }

    public String getPatient_lname() {
        return patient_lname;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getWeight() {
        return weight;
    }

    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getContact_num() {
        return contact_num;
    }

    public String getAddress() {
        return address;
    }

    public PatientStructure(JSONObject jsonObject){

        try {
            patient_id=jsonObject.getString("Patient_Id");
            patient_fname=jsonObject.getString("Firstname");
            patient_lname=jsonObject.getString("Lastname");
            dob=jsonObject.getString("DOB");
            gender=jsonObject.getString("Gender");
            weight=jsonObject.getString("Weight");
            age=jsonObject.getString("Age");
            email=jsonObject.getString("Email_Id");
            contact_num=jsonObject.getString("ContactNo");
            address=jsonObject.getString("Address");


        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
