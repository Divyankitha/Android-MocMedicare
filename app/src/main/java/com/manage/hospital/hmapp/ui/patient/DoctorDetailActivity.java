package com.manage.hospital.hmapp.ui.patient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.data.DoctorData;




public class DoctorDetailActivity extends AppCompatActivity {

    int position;
    TextView doctorNameTitle;
    TextView doctorDetailSpec;
    TextView doctorDetailContact;
    TextView doctorDetailEmail;
    TextView doctorDetailAddress;
    TextView doctorDetailGender;
    TextView doctorDetailLicenseNumber;
    TextView doctorDetailDOB;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.patient_doctor_detail_title));
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        position=getIntent().getExtras().getInt("position");

        doctorNameTitle=(TextView)findViewById(R.id.card_detail_doctor_title);
        doctorDetailSpec=(TextView)findViewById(R.id.card_doc_detail_spec);
        doctorDetailContact=(TextView)findViewById(R.id.card_doc_detail_contact);
        doctorDetailEmail=(TextView)findViewById(R.id.card_doc_detail_email);
        doctorDetailAddress=(TextView)findViewById(R.id.card_doc_detail_address);
        doctorDetailGender=(TextView)findViewById(R.id.card_doc_detail_gender);
        doctorDetailLicenseNumber=(TextView)findViewById(R.id.card_doc_detail_licenseno);
        doctorDetailDOB=(TextView)findViewById(R.id.card_doc_detail_dob);

        setValues();
    }

    public void setValues(){
        doctorNameTitle.setText(DoctorData.getInstance().get(position).getDoctor_fname()+" "+DoctorData.getInstance().get(position).getDoctor_lname());
        doctorDetailSpec.setText(DoctorData.getInstance().get(position).getSpectialization());
        doctorDetailContact.setText(DoctorData.getInstance().get(position).getContact_num());
        doctorDetailEmail.setText(DoctorData.getInstance().get(position).getEmail());
        doctorDetailAddress.setText(DoctorData.getInstance().get(position).getAddress());
        doctorDetailGender.setText(DoctorData.getInstance().get(position).getGender());
        doctorDetailLicenseNumber.setText(DoctorData.getInstance().get(position).getLicense_num());
        doctorDetailDOB.setText(DoctorData.getInstance().get(position).getDob());

    }

    @Override
    public void onBackPressed() {
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
}
