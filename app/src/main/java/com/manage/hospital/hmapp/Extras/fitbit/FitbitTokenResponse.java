package com.manage.hospital.hmapp.Extras.fitbit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.ui.patient.FitbitActivity;
import com.manage.hospital.hmapp.ui.SessionManager;


public class FitbitTokenResponse extends AppCompatActivity {


    String respStrUrl;
    String LOG_TAG=FitbitTokenResponse.class.getSimpleName();

    @Override
    protected void onNewIntent(Intent intent){
        respStrUrl=intent.getDataString();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());

        Log.d(LOG_TAG,respStrUrl);
        String token=respStrUrl.substring(respStrUrl.indexOf(getResources().getString(R.string.fitbit_token))+14,respStrUrl.indexOf(getResources().getString(R.string.fitbit_uid)));
        Log.d(LOG_TAG,token);
        String user_id=respStrUrl.substring(respStrUrl.indexOf(getResources().getString(R.string.fitbit_uid))+9,respStrUrl.indexOf(getResources().getString(R.string.fibit_scope)));
        Log.d(LOG_TAG,user_id);
        String token_type=respStrUrl.substring(respStrUrl.indexOf(getResources().getString(R.string.fitbit_token_type))+12,respStrUrl.indexOf(getResources().getString(R.string.fitbit_token_expiry)));
        Log.d(LOG_TAG,token_type);

        SessionManager sessionManager=new SessionManager(FitbitTokenResponse.this);
        sessionManager.createFitbitSession(true,user_id,token,token_type,token_type+" "+token);

        /*SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(FitbitReferences.HAS_ACCESS_TOKEN,true).commit();
        sp.edit().putString(FitbitReferences.FITBIT_TOKEN,token).commit();
        sp.edit().putString(FitbitReferences.FITBIT_UID,user_id).commit();
        sp.edit().putString(FitbitReferences.FITBIT_TOKEN_TYPE,token_type).commit();
        sp.edit().putString(FitbitReferences.FITBIT_FULL_AUTH,token_type+" "+token).commit();*/

        Intent intent=new Intent(FitbitTokenResponse.this,FitbitActivity.class);
        startActivity(intent);


    }
}
