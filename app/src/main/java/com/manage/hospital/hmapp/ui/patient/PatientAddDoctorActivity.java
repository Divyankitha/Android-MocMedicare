package com.manage.hospital.hmapp.ui.patient;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.adapter.DoctorSpecialityListAdapter;
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


public class PatientAddDoctorActivity extends AppCompatActivity {


    DoctorSpecialityListAdapter doctorSpecialityListAdapter;
    Spinner specialitySpinner;
    Spinner doctorListSpinner;
    Button btnAddDoctor;
    ArrayAdapter<String> docSpinArrayAdapter;
    SessionManager sessionManager;
    String patient_id;
    String doc_id;
    HashMap<String,String> docHashMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.patient_add_doctor_title));
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sessionManager=new SessionManager(PatientAddDoctorActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        patient_id = user.get(SessionManager.KEY_ID);

        specialitySpinner=(Spinner)findViewById(R.id.spinner_speciality);
        doctorListSpinner=(Spinner)findViewById(R.id.spinner_doctor);
        btnAddDoctor=(Button)findViewById(R.id.btn_patient_add_doc);

        btnAddDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(doc_id!=null) {
                    AddDoctorTask addDoctorTask = new AddDoctorTask();
                    addDoctorTask.execute(doc_id,patient_id);
                }
            }
        });


        specialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String spec=adapterView.getItemAtPosition(i).toString();
                FetchDoctorTask fetchDoctorTask=new FetchDoctorTask();
                fetchDoctorTask.execute(spec);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        doctorListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String doc_name=adapterView.getItemAtPosition(i).toString();
                doc_id=docHashMap.get(doc_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FetchDocSpecialityTask fetchDocSpecialityTask=new FetchDocSpecialityTask();
        fetchDocSpecialityTask.execute();

    }

    public class FetchDocSpecialityTask extends AsyncTask<Void,Void,List<String>> {

        private final String LOG_TAG=FetchDocSpecialityTask.class.getSimpleName();

        private List<String> getSpecialityListFromJson(String docSpecialityJsonStr) throws JSONException {

            List<String> specialityList=new ArrayList<>();
            JSONArray jsonArray=new JSONArray(docSpecialityJsonStr);


            for(int i=0;i<jsonArray.length();i++){

                String speciality=jsonArray.getString(i);
                specialityList.add(speciality);
            }

            return specialityList;

        }


        @Override
        protected List<String> doInBackground(Void... voids) {


            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String specListJson=null;

            try{
                String baseUrl= ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.DOCTOR_SPECIALITY;

                Uri specUri=Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).build();

                URL url=new URL(specUri.toString());

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

                specListJson=buffer.toString();


                Log.v(LOG_TAG,"SpecListStr: "+specListJson);

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
                return getSpecialityListFromJson(specListJson);
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(List<String> result){
            if(result!=null){
                doctorSpecialityListAdapter=new DoctorSpecialityListAdapter(PatientAddDoctorActivity.this, R.layout.custom_spinner, result);
                specialitySpinner.setAdapter(doctorSpecialityListAdapter);
            }
        }
    }

    public class FetchDoctorTask extends AsyncTask<String,Void,HashMap<String,String>> {

        private final String LOG_TAG=FetchDoctorTask.class.getSimpleName();

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
                final String PATH_PARAM = ConfigConstant.DOCTOR_SPECIALITY;
                String doc_speciality=params[0];

                Uri specUri=Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).appendEncodedPath(doc_speciality).build();

                URL url=new URL(specUri.toString());

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


                Log.v(LOG_TAG,"DocspecListStr: "+docListJson);

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
                    docSpinArrayAdapter=new ArrayAdapter<String>(PatientAddDoctorActivity.this,R.layout.spinner_custom_list_item,finalDocArr);
                    doctorListSpinner.setAdapter(docSpinArrayAdapter);

                    docHashMap=new HashMap<>();
                    docHashMap=result;

                }

            }
        }
    }

    public class AddDoctorTask extends AsyncTask<String,Void,Boolean>{

        private final String LOG_TAG=AddDoctorTask.class.getSimpleName();

        @Override
        protected Boolean doInBackground(String... param) {

            HttpURLConnection urlConnection=null;

            StringBuilder sb=new StringBuilder();
            try {
                String base_url = ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.PATIENT_ADD_DOCTOR;
                String doctor_id=param[0];
                String patient_id=param[1];


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
                    donationObj.put("doctorId", doctor_id);
                    donationObj.put("patientId",patient_id);

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
                new AlertDialog.Builder(PatientAddDoctorActivity.this).setTitle("New Association")
                        .setMessage(getResources().getString(R.string.pat_doc_dialog_success))
                        .setPositiveButton(R.string.pat_add_doc_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                closeActivity();
                            }
                        })
                        .show();


            }else{
                new AlertDialog.Builder(PatientAddDoctorActivity.this).setTitle("New Donation")
                        .setMessage(getResources().getString(R.string.pat_doc_dialog_failure))
                        .setPositiveButton(R.string.pat_add_doc_dialog_ok, new DialogInterface.OnClickListener() {
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
            PatientAddDoctorActivity.this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackButtonPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void onBackButtonPressed(){

        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        onBackButtonPressed();
        super.onBackPressed();
    }


}
