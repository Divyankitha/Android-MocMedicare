package com.manage.hospital.hmapp.ui.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.manage.hospital.hmapp.Extras.Interface.AppointmentFragmentToAppointmentActivity;
import com.manage.hospital.hmapp.R;


public class AppointmentActivity extends AppCompatActivity implements AppointmentFragmentToAppointmentActivity{


    private static final int APPOINTMENT_RESULT_CODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.appointment_title));
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState==null){
            Fragment fragment=new AppointmentFragment();
            FragmentManager fragmentManager=getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_appointments,fragment).commit();
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
    public void onAppointmentItemClick(int position) {
        Intent intent=new Intent(AppointmentActivity.this,AppointmentDetailActivity.class);
        intent.putExtra("position",position);
        startActivityForResult(intent,APPOINTMENT_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==APPOINTMENT_RESULT_CODE){
            if(resultCode==RESULT_OK){
                AppointmentFragment appointmentFragment=(AppointmentFragment)getSupportFragmentManager().findFragmentById(R.id.frame_appointments);
                appointmentFragment.updateAppointmentList();
            }else if(resultCode==RESULT_CANCELED){

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
