package com.manage.hospital.hmapp.data;

import org.json.JSONException;
import org.json.JSONObject;


public class HealthDataRequestStructure {

    public String request_id;
    public String doctor_id;
    public String patient_id;
    public String desc;
    public String status;
    public String doc_name;

    public HealthDataRequestStructure(JSONObject jsonObject) {

        try {

            request_id=jsonObject.getString("Request_Id");
            doctor_id=jsonObject.getString("Doctor_Id");
            patient_id=jsonObject.getString("Patient_Id");
            desc=jsonObject.getString("Description");
            status=jsonObject.getString("Status");
            doc_name=jsonObject.getString("Doctor_FullName");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getRequest_id() {
        return request_id;
    }

    public String getDesc() {
        return desc;
    }

    public String getStatus() {
        return status;
    }

    public String getDoc_name() {
        return doc_name;
    }
}
