package com.manage.hospital.hmapp.data;

import org.json.JSONException;
import org.json.JSONObject;


public class PatientAppointmentStructure {

    public String appointment_date_time;
    public String appointment_status;
    public String appointment_desc;
    public String appointment_id;

    public PatientAppointmentStructure(JSONObject jsonObject){

        try {
            appointment_date_time = jsonObject.getString("Reminder_Date");
            appointment_status = jsonObject.getString("Appointment_Status");
            appointment_desc=jsonObject.getString("Description");
            appointment_id=jsonObject.getString("Appointment_Id");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }



    public String getAppointment_date_time() {
        return appointment_date_time;
    }

    public String getAppointment_status() {
        return appointment_status;
    }

    public String getAppointment_desc() {
        return appointment_desc;
    }

    public String getAppointment_id() {
        return appointment_id;
    }
}
