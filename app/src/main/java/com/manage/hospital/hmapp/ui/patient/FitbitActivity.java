package com.manage.hospital.hmapp.ui.patient;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.ui.SessionManager;

public class FitbitActivity extends AppCompatActivity {

    private SharedPreferences fitbitSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit);

        //fitbitSharedPref= PreferenceManager.getDefaultSharedPreferences(this);
        SessionManager sessionManager=new SessionManager(this);

        //boolean tokenAvail=fitbitSharedPref.getBoolean(FitbitReferences.HAS_ACCESS_TOKEN,false);

        boolean tokenAvail=sessionManager.hasFitbitToken();

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle("Fitbit");
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /*if(!tokenAvail){
            Log.d("No token",fitbitSharedPref.getString(FitbitReferences.FITBIT_TOKEN,""));

        }else{
            Log.d("Token","I have token");
            Log.d("Token",fitbitSharedPref.getString(FitbitReferences.FITBIT_TOKEN,""));
            Log.d("UID",fitbitSharedPref.getString(FitbitReferences.FITBIT_UID,""));
            Log.d("Token type",fitbitSharedPref.getString(FitbitReferences.FITBIT_TOKEN_TYPE,""));
        }*/

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content_fitbit,new FitBitDetailsFragment()).commit();

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
