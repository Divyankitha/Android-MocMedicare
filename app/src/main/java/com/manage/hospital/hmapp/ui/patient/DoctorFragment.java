package com.manage.hospital.hmapp.ui.patient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manage.hospital.hmapp.Extras.Interface.DoctorFragmentToDoctorActivity;
import com.manage.hospital.hmapp.Extras.Interface.DoctorAdapterToDoctorFragment;
import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.adapter.DocListAdapter;
import com.manage.hospital.hmapp.data.DoctorData;
import com.manage.hospital.hmapp.data.DoctorStructure;
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



public class DoctorFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private DocFragmentInteractionListener fragListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewDoctor;
    DocListAdapter DoctorListAdapter;
    SessionManager sessionManager;
    private String pt_id;
    DoctorFragmentToDoctorActivity doctorItemListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root_view=inflater.inflate(R.layout.fragment_patient_doctors,container,false);
        swipeRefreshLayout=(SwipeRefreshLayout)root_view.findViewById(R.id.doctors_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        sessionManager=new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        pt_id = user.get(SessionManager.KEY_ID);

        recyclerViewDoctor=(RecyclerView) root_view.findViewById(R.id.doctor_list_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager=linearLayoutManager;

        recyclerViewDoctor.setLayoutManager(layoutManager);
        recyclerViewDoctor.setItemAnimator(new DefaultItemAnimator());

        getDoctorList();

        FloatingActionButton fab = (FloatingActionButton) root_view.findViewById(R.id.addDoctor);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed();
            }
        });

        return root_view;

    }

    public void updateDoctorList(){
        getDoctorList();
    }

    public void onButtonPressed() {
        if (fragListener != null) {
            fragListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DoctorFragmentToDoctorActivity) {
            doctorItemListener = (DoctorFragmentToDoctorActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement volunteer listener");
        }

        if (context instanceof DocFragmentInteractionListener) {
            fragListener = (DocFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DocFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        doctorItemListener = null;
        fragListener=null;
    }

    public void getDoctorList(){
        FetchDoctorListTask fetchDoctorListTask=new FetchDoctorListTask();
        fetchDoctorListTask.execute(pt_id);
    }

    public void doctorItemClick(int position){
        if(doctorItemListener!=null){
            doctorItemListener.onDoctorItemClick(position);
        }
    }

    @Override
    public void onRefresh() {

        getDoctorList();
        swipeRefreshLayout.setRefreshing(false);
    }

    public class FetchDoctorListTask extends AsyncTask<String,Void,DoctorData> {

        private final String LOG_TAG=FetchDoctorListTask.class.getSimpleName();

        private DoctorData getDoctorListFromJson(String appJsonStr) throws JSONException {

            DoctorData doctorData=DoctorData.getInstance();
            doctorData.clear();
            DoctorStructure docObj;
            JSONArray jsonArray=new JSONArray(appJsonStr);

            for(int i=0;i<jsonArray.length();i++){

                docObj=new DoctorStructure(jsonArray.getJSONObject(i));
                doctorData.add(docObj);

            }

            return doctorData;

        }


        @Override
        protected DoctorData doInBackground(String... params) {


            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String docListJson=null;

            try{
                String baseUrl= ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.PATIENT_DOC_LIST_ENDPOINT;
                final String PAT_ID=params[0];



                Uri docUri= Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).appendEncodedPath(PAT_ID).build();

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


                Log.v(LOG_TAG,"DoctorListStr: "+docListJson);

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
                return getDoctorListFromJson(docListJson);
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(DoctorData result){
            if(result!=null){

                if(DoctorListAdapter==null) {
                DoctorListAdapter=new DocListAdapter(getContext(),result.data);
                recyclerViewDoctor.setAdapter(DoctorListAdapter);
                }else {
                    DoctorListAdapter.notifyDataSetChanged();
                }
                DoctorListAdapter.setOnItemClickListener(new DoctorAdapterToDoctorFragment() {
                    @Override
                    public void onDoctorItemClick(int position) {
                        doctorItemClick(position);
                    }
                });

            }
        }
    }


    public interface DocFragmentInteractionListener {
        void onFragmentInteraction();
    }

}
