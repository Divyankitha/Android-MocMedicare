package com.manage.hospital.hmapp.ui.doctor;

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
import android.widget.Button;
import android.widget.TextView;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.data.AppointmentData;
import com.manage.hospital.hmapp.utility.ConfigConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class AppointmentDetailActivity extends AppCompatActivity implements View.OnClickListener {


    int position;
    TextView appStatusTitle;
    TextView appDateTitle;
    TextView appStatus;
    TextView appId;
    TextView patientName;
    TextView appDate;
    TextView appDesc;
    Button btnAccept,btnCancel;
    int status_flag=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appointment_detail);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.appointment_details_title));
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        position=getIntent().getExtras().getInt("position");
        appStatusTitle=(TextView)findViewById(R.id.card_detail_appointment_title_status);
        appDateTitle=(TextView)findViewById(R.id.card_detail_appointment_title_date);
        appStatus=(TextView)findViewById(R.id.card_details_appointment_status);
        appId=(TextView)findViewById(R.id.card_details_appointment_id);
        patientName=(TextView)findViewById(R.id.card_detail_patient_name);
        appDate=(TextView)findViewById(R.id.card_detail_app_date);
        appDesc=(TextView)findViewById(R.id.card_detail_app_desc);
        btnAccept=(Button)findViewById(R.id.btn_app_accept);
        btnAccept.setOnClickListener(this);
        btnCancel=(Button)findViewById(R.id.btn_app_cancel);
        btnCancel.setOnClickListener(this);
        setValues();
    }


    private void setValues(){

        String status=AppointmentData.getInstance().get(position).getAppointment_status();
        appStatusTitle.setText(status);
        if(status.equals(getResources().getString(R.string.appt_status_declined))){
            btnAccept.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }else if(status.equals(getResources().getString(R.string.appt_status_accepted))){
            btnAccept.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }

        //appStatusTitle.setText(AppointmentData.getInstance().get(position).getAppointment_status());
        appDateTitle.setText(AppointmentData.getInstance().get(position).getAppointment_date_time());
        appStatus.setText(AppointmentData.getInstance().get(position).getAppointment_status());
        appId.setText("#"+AppointmentData.getInstance().get(position).getAppointment_id());
        patientName.setText(AppointmentData.getInstance().get(position).getPatient_name());
        appDate.setText(AppointmentData.getInstance().get(position).getAppointment_date_time());
        appDesc.setText(AppointmentData.getInstance().get(position).getAppointment_desc());
    }

    @Override
    public void onClick(View view) {

        int id=view.getId();
        String app_id=AppointmentData.getInstance().get(position).getAppointment_id();
        if(id==R.id.btn_app_accept){

            AppointmentStatusUpdateTask statusUpdateTask=new AppointmentStatusUpdateTask();
            statusUpdateTask.execute(app_id,"Accepted");

        }else if(id==R.id.btn_app_cancel){

            AppointmentStatusUpdateTask statusUpdateTask=new AppointmentStatusUpdateTask();
            statusUpdateTask.execute(app_id,"Declined");
        }

    }

    public class AppointmentStatusUpdateTask extends AsyncTask<String,Void,Boolean> {

        private final String LOG_TAG=AppointmentDetailActivity.class.getSimpleName();

        String apmt_id,new_status;


        @Override
        protected Boolean doInBackground(String... param) {

            HttpURLConnection urlConnection=null;

            apmt_id=param[0];
            new_status=param[1];

            try {
                String base_url = ConfigConstant.BASE_URL;
                final String APPT_PATH_PARAM = ConfigConstant.APPOINTMENT_STATUS_UPDATE;


                Uri apptUpdateUri = Uri.parse(base_url).buildUpon().appendEncodedPath(APPT_PATH_PARAM).build();

                URL url = new URL(apptUpdateUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                try {

                    JSONObject apmtObj = new JSONObject();
                    apmtObj.put("appointment_id",apmt_id);
                    apmtObj.put("new_status",new_status);

                    OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream());
                    os.write(apmtObj.toString());
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
                new AlertDialog.Builder(AppointmentDetailActivity.this).setTitle(getResources().getString(R.string.apmt_dialog_title))
                        .setMessage(getResources().getString(R.string.apmt_success_msg))
                        .setPositiveButton(R.string.apmt_dialog_btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                updateValues();
                            }
                        })
                        .show();
            }else{
                new AlertDialog.Builder(AppointmentDetailActivity.this).setTitle(getResources().getString(R.string.apmt_dialog_title))
                        .setMessage(getResources().getString(R.string.apmt_failure_msg))
                        .setPositiveButton(R.string.apmt_dialog_btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        }

        public void updateValues(){
            //setResult(RESULT_OK);
            //finish();

            appStatusTitle.setText(new_status);
            appStatus.setText(new_status);
            btnAccept.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

            status_flag=1;
        }
    }

    @Override
    public void onBackPressed() {
        onBackButtonPressed(status_flag);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void onBackButtonPressed(int status_flag){
        if(status_flag==0){
            setResult(RESULT_CANCELED);
            finish();
        }else if(status_flag==1){
            setResult(RESULT_OK);
            finish();
        }
    }


}
