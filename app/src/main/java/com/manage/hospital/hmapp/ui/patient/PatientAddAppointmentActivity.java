package com.manage.hospital.hmapp.ui.patient;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.data.DoctorStructure;
import com.manage.hospital.hmapp.ui.SessionManager;
import com.manage.hospital.hmapp.utility.ConfigConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PatientAddAppointmentActivity extends AppCompatActivity implements View.OnClickListener {


    EditText editTextAppDesc;
    EditText editTextAppDate;
    Spinner spinnerDoc;
    ArrayAdapter<String> docSpinArrayAdapter;
    Button btnAddAppointment;
    HashMap<String,String> docHashMap;
    String patient_id;
    String doctor_id;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_add_apmt);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.new_apmt_title));
        }

        sessionManager=new SessionManager(this);
        HashMap<String,String> user=sessionManager.getUserDetails();
        patient_id=user.get(SessionManager.KEY_ID);


        editTextAppDesc=(EditText) findViewById(R.id.edit_apmt_desc);
        editTextAppDate=(EditText)findViewById(R.id.edit_apmt_date);
        spinnerDoc=(Spinner)findViewById(R.id.spinner_apmt_doctor);

        spinnerDoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String doc_name=adapterView.getItemAtPosition(i).toString();
                doctor_id=docHashMap.get(doc_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnAddAppointment=(Button)findViewById(R.id.btn_add_apmt);
        btnAddAppointment.setOnClickListener(this);

        FetchDoctorListTask fetchDoctorListTask=new FetchDoctorListTask();
        fetchDoctorListTask.execute(patient_id);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_apmt_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.action_close){
            setResult(RESULT_CANCELED);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();

        if(id==R.id.btn_add_apmt){

            AddAppointmentTask apmtTask=new AddAppointmentTask();
            apmtTask.execute(doctor_id,patient_id);
        }
    }

    public String getDataFromEditText(EditText editText){
        return editText.getText().toString();
    }



    public class AddAppointmentTask extends AsyncTask<String,Void,Boolean>{

        private final String LOG_TAG=AddAppointmentTask.class.getSimpleName();

        @Override
        protected Boolean doInBackground(String... param) {

            HttpURLConnection urlConnection=null;

            StringBuilder sb=new StringBuilder();
            try {
                String base_url = ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.ADD_APPOINTMENT_ENDPOINT;
                final String doc_id=param[0];
                final String pat_id=param[1];

                Uri donationUri = Uri.parse(base_url).buildUpon().appendEncodedPath(PATH_PARAM).build();

                URL url = new URL(donationUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                try {

                    JSONObject donationObj = new JSONObject();
                    donationObj.put("description", getDataFromEditText(editTextAppDesc));
                    donationObj.put("P_ID",pat_id);
                    donationObj.put("D_ID",doc_id);
                    donationObj.put("status","Requested");
                    donationObj.put("Reminder_DateTime",getDataFromEditText(editTextAppDate));

                    OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream());
                    os.write(donationObj.toString());
                    os.close();

                    int HttpResult =urlConnection.getResponseCode();
                    if(HttpResult ==HttpURLConnection.HTTP_OK){
                        return true;
                    }

                }catch (JSONException e){
                    Log.e(LOG_TAG,e.getMessage());
                }

            }catch (IOException e){
                Log.e(LOG_TAG,e.getMessage());
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            if(res) {
                new AlertDialog.Builder(PatientAddAppointmentActivity.this).setTitle("New Appointment")
                        .setMessage(getResources().getString(R.string.new_apmt_success_msg))
                        .setPositiveButton(R.string.apmt_dialog_btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                closeActivity();
                            }
                        })
                        .show();


            }else{
                new AlertDialog.Builder(PatientAddAppointmentActivity.this).setTitle("New Appointment")
                        .setMessage(getResources().getString(R.string.new_apmt_failure_msg))
                        .setPositiveButton(R.string.apmt_dialog_btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        }

        public void closeActivity(){
            setResult(RESULT_OK);
            PatientAddAppointmentActivity.this.finish();
        }
    }



    public class FetchDoctorListTask extends AsyncTask<String,Void,HashMap<String,String>> {

        private final String LOG_TAG=FetchDoctorListTask.class.getSimpleName();

        private HashMap<String,String> getDoctorFromJson(String docJsonStr) throws JSONException {


            HashMap<String,String> docList=new HashMap<>();
            JSONArray jsonArray=new JSONArray(docJsonStr);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject=jsonArray.getJSONObject(i);
                DoctorStructure doctorStructure=new DoctorStructure(jsonObject);
                String doctor_id=doctorStructure.getDocId();
                String doctor_name=doctorStructure.getDoctor_fname()+" "+doctorStructure.getDoctor_lname();

                docList.put(doctor_name,doctor_id);
            }

            return docList;

        }


        @Override
        protected HashMap<String,String> doInBackground(String... params) {


            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String docListJson=null;

            try{
                String baseUrl= ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.DOCTOR_LIST_ENDPOINT;
                String pat_id=params[0];

                Uri docUri=Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).appendEncodedPath(pat_id).build();

                URL url=new URL(docUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream=urlConnection.getInputStream();
                StringBuffer buffer=new StringBuffer();
                if(inputStream==null){
                    return null;
                }

                reader=new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line=reader.readLine())!=null){
                    buffer.append(line+"\n");
                }

                if(buffer.length()==0){
                    return null;
                }

                docListJson=buffer.toString();


                Log.v(LOG_TAG,"DocListStr: "+docListJson);

            }catch (IOException e){

                Log.e(LOG_TAG,e.getMessage(),e);
                return null;

            }
            finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG,"Error closing stream",e);
                    }
                }

            }

            try{
                return getDoctorFromJson(docListJson);
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(final HashMap<String,String> result){
            if(result!=null){

                final List<String> docList;
                if(result.size()!=0){
                    docList=new ArrayList<>();
                    for(String key:result.keySet()){
                        docList.add(key);
                    }
                    String finalDocArr[]=docList.toArray(new String[docList.size()]);
                    docSpinArrayAdapter=new ArrayAdapter<String>(PatientAddAppointmentActivity.this,R.layout.spinner_custom_list_item,finalDocArr);
                    spinnerDoc.setAdapter(docSpinArrayAdapter);

                    docHashMap=new HashMap<>();
                    docHashMap=result;

                }

            }
        }
    }



}
