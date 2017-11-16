package com.manage.hospital.hmapp.util;



import com.manage.hospital.hmapp.model.Appointment;
import com.manage.hospital.hmapp.model.EmergencyContact;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;

public class JSONUtil {
    public static EmergencyContact parseEmergencyContactFromJSON(JSONObject response) {
        String dependentId,firstname,lastname,emailId,contact,address,relation;
        dependentId = firstname = lastname = emailId = contact = address =relation ="";
        try {
            dependentId = response.getString("Dependent_Id");
            firstname = response.getString("Firstname");
            lastname = response.getString("Lastname");
            emailId = response.getString("EmailId");
            contact = response.getString("Contact");
            address = response.getString("Address");
            relation = response.getString("Relation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EmergencyContact ec = new EmergencyContact(Integer.valueOf(dependentId),firstname,lastname,emailId,contact,address,relation);
        return ec;
    }

    public static JSONObject convertEmergencyContactToJSON(EmergencyContact ec, String patientId) {
        JSONObject json = new JSONObject();
        try {
            if (ec != null) {
                json.accumulate("patientId", patientId);
                json.accumulate("dependentId", ec.getId());
                json.accumulate("firstname", ec.getFirstname());
                json.accumulate("lastname", ec.getLastname());
                json.accumulate("emailId", ec.getEmailId());
                json.accumulate("contact", ec.getContact());
                json.accumulate("address", ec.getAddress());
                json.accumulate("relation", ec.getRelation());
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public static JSONObject convertEmergencyContactToJSONUpdate(EmergencyContact ec ,String patientId) {
        JSONObject json = new JSONObject();
        try {
            if (ec != null) {
                json.accumulate("patientId", patientId);
                json.accumulate("dependent_id", ec.getId());
                json.accumulate("new_firstname", ec.getFirstname());
                json.accumulate("new_lastname", ec.getLastname());
                json.accumulate("new_emailId", ec.getEmailId());
                json.accumulate("new_contact", ec.getContact());
                json.accumulate("new_address", ec.getAddress());
                json.accumulate("new_relation", ec.getRelation());
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public static Appointment parseAppointmentFromJSON(JSONObject response) {
        String appointmentId,description,appointmentDate,appointmentStatus,patientId,doctorId;
        appointmentId = description = appointmentDate = appointmentStatus = patientId = doctorId ="";
        try {
            appointmentId = response.getString("Appointment_Id");
            description = response.getString("Description");
            appointmentDate = response.getString("Reminder_Date");
            appointmentStatus = response.getString("Appointment_Status");
            patientId = response.getString("Patient_Id");
            doctorId = response.getString("Doctor_Id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Appointment a = new Appointment(Integer.valueOf(appointmentId),description, appointmentDate,appointmentStatus,Integer.valueOf(patientId),Integer.valueOf(doctorId));
        return a;
    }

    public static JSONObject convertAppointmentToJSON(Appointment a, String patientId) {
        JSONObject json = new JSONObject();
        try {
            if (a != null) {
                json.accumulate("description", a.getDescription());
                json.accumulate("P_ID", a.getPatientId());
                json.accumulate("D_ID", a.getDoctorId());
                json.accumulate("status", a.getAppointmentStatus());
                json.accumulate("Reminder_DateTime", a.getAppointmentDate());
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
