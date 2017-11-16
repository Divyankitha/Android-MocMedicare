package com.manage.hospital.hmapp.data;

import org.json.JSONException;
import org.json.JSONObject;


public class HealthData {

    public String patient_id;
    public String sleep_duration;
    public String heart_rate;
    public String calories_burnt;
    public String no_of_steps;

    public HealthData(JSONObject jsonObject){
        try {
            patient_id = jsonObject.getString("Patient_Id");
            sleep_duration=jsonObject.getString("Sleep Duration");
            heart_rate=jsonObject.getString("Heart Rate");
            calories_burnt=jsonObject.getString("Calories Burnt");
            no_of_steps=jsonObject.getString("No of Steps");

        }catch (JSONException e){

        }
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getSleep_duration() {
        return sleep_duration;
    }

    public String getHeart_rate() {
        return heart_rate;
    }

    public String getCalories_burnt() {
        return calories_burnt;
    }

    public String getNo_of_steps() {
        return no_of_steps;
    }
}
