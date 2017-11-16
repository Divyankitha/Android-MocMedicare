package com.manage.hospital.hmapp.ui.patient;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manage.hospital.hmapp.Extras.Interface.PatientAdapterToPatientFragment;
import com.manage.hospital.hmapp.Extras.Interface.PatientFragmentToPatientActivity;
import com.manage.hospital.hmapp.Extras.Interface.RequestsAdapterToRequestFragment;
import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.adapter.HealthRequestAdapter;
import com.manage.hospital.hmapp.adapter.PatientListAdapter;
import com.manage.hospital.hmapp.data.HealthDataRequestStructure;
import com.manage.hospital.hmapp.data.PatientData;
import com.manage.hospital.hmapp.data.PatientStructure;
import com.manage.hospital.hmapp.ui.SessionManager;
import com.manage.hospital.hmapp.utility.ConfigConstant;
import com.manage.hospital.hmapp.utility.FitbitReferences;

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


public class PatientHealthRequestsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewHealthRequests;
    HealthRequestAdapter requestListAdapter;
    SessionManager sessionManager;
    private String pat_id;
    private SharedPreferences fitbitSharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root_view=inflater.inflate(R.layout.fragment_patient_health_requests,container,false);
        swipeRefreshLayout=(SwipeRefreshLayout)root_view.findViewById(R.id.health_req_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        sessionManager=new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        pat_id = user.get(SessionManager.KEY_ID);

        //fitbitSharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity());

        Log.d("sleep",sessionManager.getFitbitSleepData());
        Log.d("heart",sessionManager.getFitbitHeartRateData());
        Log.d("calories",sessionManager.getFitbitCaloriesData());
        Log.d("steps",sessionManager.getFitbitStepsData());

        recyclerViewHealthRequests=(RecyclerView) root_view.findViewById(R.id.health_req_list_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager=linearLayoutManager;

        recyclerViewHealthRequests.setLayoutManager(layoutManager);
        recyclerViewHealthRequests.setItemAnimator(new DefaultItemAnimator());

        getHealthRequests();

        return root_view;

    }

    public void getHealthRequests(){

        FetchPatientHealthReqListTask fetchHealthReqListTask=new FetchPatientHealthReqListTask();
        fetchHealthReqListTask.execute(pat_id);
    }

    @Override
    public void onRefresh() {
        getHealthRequests();
        swipeRefreshLayout.setRefreshing(false);
    }

    public class FetchPatientHealthReqListTask extends AsyncTask<String,Void,List<HealthDataRequestStructure>> {

        private final String LOG_TAG=FetchPatientHealthReqListTask.class.getSimpleName();

        private List<HealthDataRequestStructure> getRequestsListFromJson(String reqJsonStr) throws JSONException {

            List<HealthDataRequestStructure> healthRequestList=new ArrayList<>();

            HealthDataRequestStructure reqObj;
            JSONArray jsonArray=new JSONArray(reqJsonStr);

            for(int i=0;i<jsonArray.length();i++){

                reqObj=new HealthDataRequestStructure(jsonArray.getJSONObject(i));
                healthRequestList.add(reqObj);

            }

            return healthRequestList;

        }


        @Override
        protected List<HealthDataRequestStructure> doInBackground(String... params) {


            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String reqListJson=null;

            try{
                String baseUrl= ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.PATIENT_HEALTH_REQUEST;
                final String PAT_ID=params[0];



                Uri reqUri=Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).appendEncodedPath(PAT_ID).build();

                URL url=new URL(reqUri.toString());

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

                reqListJson=buffer.toString();


                Log.v(LOG_TAG,"RequestListStr: "+reqListJson);

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
                return getRequestsListFromJson(reqListJson);
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(List<HealthDataRequestStructure> result){
            if(result!=null){

                if(requestListAdapter==null) {
                    requestListAdapter = new HealthRequestAdapter(getContext(), result);
                    recyclerViewHealthRequests.setAdapter(requestListAdapter);
                }else{
                    requestListAdapter.refreshList(result);
                }
                requestListAdapter.setOnItemClickListener(new RequestsAdapterToRequestFragment() {
                    @Override
                    public void onRequestItemClick(String request_id, String new_status) {

                        if(new_status.equals("accept")) {
                            if (sessionManager.getFitbitHeartRateData().equals("")) {
                                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.dialog_request_title))
                                        .setMessage("Please sync fitbit with the application")
                                        .setPositiveButton(R.string.dialog_health_msg_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                //updateValues();
                                            }
                                        })
                                        .show();
                            }else{
                                RequestStatusUpdateTask requestStatusUpdateTask = new RequestStatusUpdateTask();
                                requestStatusUpdateTask.execute(request_id, new_status);
                            }
                        }else{

                            RequestStatusUpdateTask requestStatusUpdateTask = new RequestStatusUpdateTask();
                            requestStatusUpdateTask.execute(request_id, new_status);

                        }
                    }
                });
            }
        }
    }


    public class RequestStatusUpdateTask extends AsyncTask<String,Void,Boolean> {

        private final String LOG_TAG=RequestStatusUpdateTask.class.getSimpleName();

        String req_id,new_status;


        @Override
        protected Boolean doInBackground(String... param) {

            HttpURLConnection urlConnection=null;

            req_id=param[0];
            new_status=param[1];

            try {
                String base_url = ConfigConstant.BASE_URL;
                final String REQ_STATUS_PATH_PARAM = ConfigConstant.PATIENT_HEALTH_REQUEST_STATUS_UPDATE;


                Uri apptUpdateUri = Uri.parse(base_url).buildUpon().appendEncodedPath(REQ_STATUS_PATH_PARAM).build();

                URL url = new URL(apptUpdateUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                try {

                    JSONObject reqObj = new JSONObject();
                    reqObj.put("request_id",req_id);
                    if(new_status.equals("accept")) {
                        reqObj.put("new_status", "Accepted");
                    }
                    else if(new_status.equals("decline")) {
                        reqObj.put("new_status", "Declined");
                    }



                    OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream());
                    os.write(reqObj.toString());
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
            displayDialog(res,new_status);
        }
    }

    public void displayDialog(Boolean flag,String new_status){

        if(flag){

            if(new_status.equals("accept")){



                    PostHealthDataTask healthDataTask = new PostHealthDataTask();
                    healthDataTask.execute(pat_id, sessionManager.getFitbitSleepData(), sessionManager.getFitbitHeartRateData(), sessionManager.getFitbitCaloriesData(), sessionManager.getFitbitStepsData());


                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.dialog_request_title))
                        .setMessage("Health data sent to the doctor.")
                        .setPositiveButton(R.string.dialog_health_msg_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                //updateValues();
                            }
                        })
                        .show();
                getHealthRequests();


            }else if(new_status.equals("decline")){

                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.dialog_request_title))
                        .setMessage("Request declined")
                        .setPositiveButton(R.string.dialog_health_msg_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                //updateValues();
                            }
                        })
                        .show();
                getHealthRequests();
            }
        }
    }

    public class PostHealthDataTask extends AsyncTask<String,Void,Boolean> {

        private final String LOG_TAG=PostHealthDataTask.class.getSimpleName();

        String pat_id,sleep_data,heart_rate,calories,steps;


        @Override
        protected Boolean doInBackground(String... param) {

            HttpURLConnection urlConnection=null;

            pat_id=param[0];
            sleep_data=param[1];
            heart_rate=param[2];
            calories=param[3];
            steps=param[4];

            try {
                String base_url = ConfigConstant.BASE_URL;
                final String REQ_STATUS_PATH_PARAM = ConfigConstant.POST_PATIENT_HEALTH_DATA;


                Uri apptUpdateUri = Uri.parse(base_url).buildUpon().appendEncodedPath(REQ_STATUS_PATH_PARAM).build();

                URL url = new URL(apptUpdateUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                try {

                    JSONObject reqObj = new JSONObject();
                    reqObj.put("patientId",pat_id);
                    reqObj.put("sleep duration",Double.parseDouble(sleep_data));
                    reqObj.put("heart rate", Double.parseDouble(heart_rate));
                    reqObj.put("calories burnt",Double.parseDouble(calories));
                    reqObj.put("no of steps",Double.parseDouble(steps));


                    OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream());
                    os.write(reqObj.toString());
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

        }



    }



}
