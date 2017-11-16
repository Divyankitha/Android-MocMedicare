package com.manage.hospital.hmapp.ui.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.manage.hospital.hmapp.Extras.Interface.RestCallbackHandler;
import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.model.EmergencyContact;
import com.manage.hospital.hmapp.util.JSONUtil;
import com.manage.hospital.hmapp.utility.ConfigConstant;

import org.json.JSONObject;


public class ManageEmergencyContactActivity extends Activity implements RestCallbackHandler {
    String mPatientId;
    //final String baseURL = "http://ec2-34-201-144-36.compute-1.amazonaws.com:8080/MoC_Medicare_Backend/";
    final String baseURL = "http://192.168.56.1:8080/MoC_Medicare_Backend/";
    final String getECRestCallId = "GET EMERGENCY CONTACT";
    final String createECRestCallId = "POST EMERGENCY CONTACT";
    final String updateECRestCallId = "PUT EMERGENCY CONTACT";
    final String deleteECRestCallId = "DELETE EMERGENCY CONTACT";

    private EmergencyContact mEmergencyContact;
    private EmergencyContact mEmergencyContactUnsaved;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = this.getIntent();
        mPatientId = intent.getStringExtra("PatientId");
        String url = ConfigConstant.BASE_URL + "/patient/emergencyContact/" + mPatientId;
        System.out.println(url);
        MocRestClient client = new MocRestClient(getECRestCallId, url, MocRestClient.GET, "", this);
        System.out.println("Before Starting client connection");
        client.execute();
    }

    public void addInitialEmergencyContact(View v){
        setContentView(R.layout.activity_addemergencycontact);
    }

    public void backFromECinitial(View v){
        this.finish();
    }
    public void backFromEC (View v){
        this.finish();
    }
    public void backFromAddEC(View V){
        //TODO return to main patient activity
        //setContentView(R.layout.activity_sidemenu);
    }

    private void populateECLayoutEditTextValues(EmergencyContact ec){
        EditText firstnameEditText = (EditText) findViewById(R.id.firstnameEditText);
        EditText lastnameEditText = (EditText) findViewById(R.id.lastnameEditText);
        EditText emailIdEditText = (EditText) findViewById(R.id.emailIdEditText);
        EditText contactEditText = (EditText) findViewById(R.id.contactEditText);
        EditText addressEditText = (EditText) findViewById(R.id.addressEditText);
        EditText relationEditText = (EditText) findViewById(R.id.relationEditText);

        firstnameEditText.setText(ec.getFirstname());
        lastnameEditText.setText(ec.getLastname());
        emailIdEditText.setText(ec.getEmailId());
        contactEditText.setText(ec.getContact());
        addressEditText.setText(ec.getAddress());
        relationEditText.setText(ec.getRelation());
    }

    public void saveEmergencyContact(View v){
        EditText firstnameEditText = (EditText) findViewById(R.id.firstnameEditText);
        EditText lastnameEditText = (EditText) findViewById(R.id.lastnameEditText);
        EditText emailIdEditText = (EditText) findViewById(R.id.emailIdEditText);
        EditText contactEditText = (EditText) findViewById(R.id.contactEditText);
        EditText addressEditText = (EditText) findViewById(R.id.addressEditText);
        EditText relationEditText = (EditText) findViewById(R.id.relationEditText);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setBackgroundColor(getResources().getColor(R.color.colorGreyDark));
        saveButton.setEnabled(false);

        String firstname = firstnameEditText.getText().toString();
        String lastname = lastnameEditText.getText().toString();
        String emailId = emailIdEditText.getText().toString();
        String contact = contactEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String relation = relationEditText.getText().toString();

        mEmergencyContactUnsaved = new EmergencyContact(mEmergencyContact.getId(),firstname, lastname, emailId, contact, address, relation);
        JSONObject body = JSONUtil.convertEmergencyContactToJSONUpdate(mEmergencyContactUnsaved, mPatientId);
        String url = ConfigConstant.BASE_URL + "/patient/emergencyContact/update" ;
        MocRestClient client = new MocRestClient(updateECRestCallId, url, MocRestClient.PUT, body.toString(), this);
        client.execute();
    }

    public void addEmergencyContact(View v){
        EditText firstnameEditText = (EditText) findViewById(R.id.firstnameET);
        EditText lastnameEditText = (EditText) findViewById(R.id.lastnameET);
        EditText emailIdEditText = (EditText) findViewById(R.id.emailIdET);
        EditText contactEditText = (EditText) findViewById(R.id.contactET);
        EditText addressEditText = (EditText) findViewById(R.id.addressET);
        EditText relationEditText = (EditText) findViewById(R.id.relationET);

        String firstname = firstnameEditText.getText().toString();
        String lastname = lastnameEditText.getText().toString();
        String emailId = emailIdEditText.getText().toString();
        String contact = contactEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String relation = relationEditText.getText().toString();

        mEmergencyContactUnsaved = new EmergencyContact(firstname, lastname, emailId, contact, address, relation);
        JSONObject body = JSONUtil.convertEmergencyContactToJSON(mEmergencyContactUnsaved, mPatientId);
        String url = ConfigConstant.BASE_URL + "/patient/emergencyContact/add" ;
        MocRestClient client = new MocRestClient(createECRestCallId, url, MocRestClient.POST, body.toString(), this);
        client.execute();
    }

    public void deleteEmergencyContact(View v){
        String url = ConfigConstant.BASE_URL + "/patient/emergencyContact/delete/" + mEmergencyContact.getId();
        String body = "";
        MocRestClient client = new MocRestClient(deleteECRestCallId, url, MocRestClient.DELETE, "", this);
        client.execute();
    }


    @Override
    public void handleResponse(MocRestClient client) {
        try {
            if(client.getId().equals(getECRestCallId)) {
                if (client.getResponseCode() == 200) {
                    JSONObject response = new JSONObject(client.getResponseBody());
                    if (response.length() == 0) {
                        System.out.println("no eC returned");
                        setContentView(R.layout.activity_emergencycontactinitial);
                    } else {
                        System.out.println("EC is returned" + response);
                        setContentView(R.layout.activity_emergencycontact);
                        mEmergencyContact = JSONUtil.parseEmergencyContactFromJSON(response);
                        populateECLayoutEditTextValues(mEmergencyContact);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again !", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(createECRestCallId)){
                if (client.getResponseCode() == 200) {
                    JSONObject body = new JSONObject(client.getResponseBody());
                    int dependentId = Integer.valueOf(body.getString("Dependent ID"));
                    mEmergencyContact = new EmergencyContact(dependentId, mEmergencyContactUnsaved.getFirstname(),
                            mEmergencyContactUnsaved.getLastname(), mEmergencyContactUnsaved.getContact(),
                            mEmergencyContactUnsaved.getEmailId(), mEmergencyContactUnsaved.getAddress(),
                            mEmergencyContactUnsaved.getRelation());
                    mEmergencyContactUnsaved = null;
                    // make toast about successful creation
                    Toast.makeText(getApplicationContext(), "Emergency Contact created successfully!", Toast.LENGTH_LONG).show();

                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(updateECRestCallId)) {
                if (client.getResponseCode() == 200) {
                    JSONObject body = new JSONObject(client.getResponseBody());
                    //this will be just like post except that we already have the dependent id;
                    mEmergencyContact = new EmergencyContact(mEmergencyContact.getId(), mEmergencyContactUnsaved.getFirstname(),
                            mEmergencyContactUnsaved.getLastname(), mEmergencyContactUnsaved.getContact(),
                            mEmergencyContactUnsaved.getEmailId(), mEmergencyContactUnsaved.getAddress(),
                            mEmergencyContactUnsaved.getRelation());
                    Toast.makeText(getApplicationContext(), "Emergency Contact updated successfully!", Toast.LENGTH_LONG).show();
                    mEmergencyContact = null;
                    Thread.sleep(1000);
                    backFromEC(null);
                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(deleteECRestCallId)) {
                if (client.getResponseCode() == 200) {
                    // make toast about successful deleteion
                    Toast.makeText(getApplicationContext(), "Emergency Contact deleted successfully!", Toast.LENGTH_LONG).show();
                    mEmergencyContact = null;
                    Thread.sleep(1000);
                    backFromAddEC(null);

                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
