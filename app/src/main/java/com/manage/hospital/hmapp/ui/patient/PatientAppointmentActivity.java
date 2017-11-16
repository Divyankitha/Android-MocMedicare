package com.manage.hospital.hmapp.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.ui.doctor.AppointmentDetailActivity;
import com.manage.hospital.hmapp.ui.doctor.AppointmentFragment;


public class PatientAppointmentActivity extends AppCompatActivity implements PatientAppointmentFragment.OnFragmentInteractionListener{


    private static final int PAT_APPOINTMENT_RESULT_CODE=12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.pat_appointment_title));
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState==null){
            Fragment fragment=new PatientAppointmentFragment();
            FragmentManager fragmentManager=getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_pat_appointments,fragment).commit();
        }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==PAT_APPOINTMENT_RESULT_CODE){
            if(resultCode==RESULT_OK){
                PatientAppointmentFragment appointmentFragment=(PatientAppointmentFragment)getSupportFragmentManager().findFragmentById(R.id.frame_pat_appointments);
                appointmentFragment.updatePatientAppList();
            }else if(resultCode==RESULT_CANCELED){

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction() {
        Intent intent=new Intent(PatientAppointmentActivity.this,PatientAddAppointmentActivity.class);
        startActivityForResult(intent,PAT_APPOINTMENT_RESULT_CODE);
    }
}
