package com.manage.hospital.hmapp.ui;

import android.app.DatePickerDialog;
//import android.app.DialogFragment;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.ui.doctor.DoctorRegistration;
import com.manage.hospital.hmapp.ui.patient.PatientRegistration;



public class Registration extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_common);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void gotoNext(View v)
    {
        int str;
        EditText fn =(EditText)findViewById(R.id.fname);
        EditText ln = (EditText)findViewById(R.id.lname);
        EditText dob = (EditText)findViewById(R.id.DOB);
        EditText eid =(EditText)findViewById(R.id.email);
        EditText gen = (EditText)findViewById(R.id.gender);
        EditText ph = (EditText)findViewById(R.id.ph_no);
        RadioButton rd1 = (RadioButton) findViewById(R.id.rd_doctor);
        RadioButton rd2 = (RadioButton) findViewById(R.id.rd_patient);

        String fname= fn.getText().toString();
        String lname= ln.getText().toString();
        String DOB= dob.getText().toString();
        String email= eid.getText().toString();
        String gender= gen.getText().toString();
        String contactNo= ph.getText().toString();


        if(rd1.isChecked())
            gotoRegistrationDoc(fname,lname,DOB,email,gender,contactNo);
        else
            gotoRegistrationPatient(fname,lname,DOB,email,gender,contactNo);

        System.out.println("Fname = "+fname);


    }

    public  void gotoRegistrationDoc(String fname,String lname,String DOB,String email,String gender,String contactNo)
    {
        Intent intent = new Intent(Registration.this,DoctorRegistration.class);
        intent.putExtra("str",0);
        intent.putExtra("fname", fname);
        intent.putExtra("lname", lname);
        intent.putExtra("DOB", DOB);
        intent.putExtra("email", email);
        intent.putExtra("gender", gender);
        intent.putExtra("contactNo", contactNo);
        startActivity(intent);
    }

    public  void gotoRegistrationPatient(String fname,String lname,String DOB,String email,String gender,String contactNo)
    {
        Intent intent = new Intent(Registration.this,PatientRegistration.class);
        intent.putExtra("str",1);
        intent.putExtra("fname", fname);
        intent.putExtra("lname", lname);
        intent.putExtra("DOB", DOB);
        intent.putExtra("email", email);
        intent.putExtra("gender", gender);
        intent.putExtra("contactNo", contactNo);
        startActivity(intent);
    }
    public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void finishRegistration(View V)
    {
        Registration.this.finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        EditText dob = (EditText)findViewById(R.id.DOB);
        dob.setText(month+1 + "/" +dayOfMonth+ "/" +year);
    }
}
