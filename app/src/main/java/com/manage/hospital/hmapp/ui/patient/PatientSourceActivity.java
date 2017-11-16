package com.manage.hospital.hmapp.ui.patient;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.manage.hospital.hmapp.R;

public class PatientSourceActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout sourceFitbit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_source);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.source_activity_title));
        }

        sourceFitbit=(LinearLayout)findViewById(R.id.source_fitbit_item);
        sourceFitbit.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        int id=view.getId();

        if(id==R.id.source_fitbit_item){

            Dialog auth_dialog=new Dialog(PatientSourceActivity.this);
            auth_dialog.setContentView(R.layout.fragment_fitbit_login);

            Intent intent=new Intent(PatientSourceActivity.this,FitbitActivity.class);
            startActivity(intent);

        }
    }
}
