package com.manage.hospital.hmapp.ui.doctor;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manage.hospital.hmapp.Extras.Interface.AppointmentFragmentToAppointmentActivity;
import com.manage.hospital.hmapp.Extras.Interface.PatientAdapterToPatientFragment;
import com.manage.hospital.hmapp.Extras.Interface.PatientFragmentToPatientActivity;
import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.adapter.AppointmentListAdapter;
import com.manage.hospital.hmapp.adapter.PatientListAdapter;
import com.manage.hospital.hmapp.data.AppointmentData;
import com.manage.hospital.hmapp.data.AppointmentStructure;
import com.manage.hospital.hmapp.data.PatientData;
import com.manage.hospital.hmapp.data.PatientStructure;
import com.manage.hospital.hmapp.ui.SessionManager;
import com.manage.hospital.hmapp.utility.ConfigConstant;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class PatientFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewPatient;
    PatientListAdapter patientListAdapter;
    SessionManager sessionManager;
    private String doc_id;
    PatientFragmentToPatientActivity patientItemListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root_view=inflater.inflate(R.layout.fragment_doctor_patients,container,false);
        swipeRefreshLayout=(SwipeRefreshLayout)root_view.findViewById(R.id.patients_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        sessionManager=new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        doc_id = user.get(SessionManager.KEY_ID);


        recyclerViewPatient=(RecyclerView) root_view.findViewById(R.id.patient_list_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager=linearLayoutManager;

        recyclerViewPatient.setLayoutManager(layoutManager);
        recyclerViewPatient.setItemAnimator(new DefaultItemAnimator());

        getPatientList();

        return root_view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PatientFragmentToPatientActivity) {
            patientItemListener = (PatientFragmentToPatientActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement volunteer listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        patientItemListener = null;
    }

    public void getPatientList(){

        FetchPatientListTask fetchPatientListTask=new FetchPatientListTask();
        fetchPatientListTask.execute(doc_id);
    }

    public void patientItemClick(int position){
        if(patientItemListener!=null){
            patientItemListener.onPatientItemClick(position);
        }
    }

    @Override
    public void onRefresh() {

        getPatientList();
        swipeRefreshLayout.setRefreshing(false);
    }

    public class FetchPatientListTask extends AsyncTask<String,Void,PatientData> {

        private final String LOG_TAG=FetchPatientListTask.class.getSimpleName();

        private PatientData getPatientListFromJson(String appJsonStr) throws JSONException {

            PatientData patientData=PatientData.getInstance();
            patientData.clear();
            PatientStructure patObj;
            JSONArray jsonArray=new JSONArray(appJsonStr);

            for(int i=0;i<jsonArray.length();i++){

                patObj=new PatientStructure(jsonArray.getJSONObject(i));
                patientData.add(patObj);

            }

            return patientData;

        }


        @Override
        protected PatientData doInBackground(String... params) {


            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String patListJson=null;

            try{
                String baseUrl= ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.DOC_PATIENT_LIST_ENDPOINT;
                final String DOC_ID=params[0];



                Uri patUri=Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).appendEncodedPath(DOC_ID).build();

                URL url=new URL(patUri.toString());

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

                patListJson=buffer.toString();


                Log.v(LOG_TAG,"PatientListStr: "+patListJson);

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
                return getPatientListFromJson(patListJson);
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(PatientData result){
            if(result!=null){

                if(patientListAdapter==null) {
                    patientListAdapter = new PatientListAdapter(getContext(), result.data);
                    recyclerViewPatient.setAdapter(patientListAdapter);
                }else{
                    patientListAdapter.notifyDataSetChanged();
                }
                patientListAdapter.setOnItemClickListener(new PatientAdapterToPatientFragment() {
                    @Override
                    public void onPatientItemClick(int position) {
                        patientItemClick(position);
                    }
                });
            }
        }
    }



}
