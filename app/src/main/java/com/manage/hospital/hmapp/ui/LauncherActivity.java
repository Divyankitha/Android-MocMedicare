package com.manage.hospital.hmapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.manage.hospital.hmapp.ui.doctor.DoctorMainActivity;
import com.manage.hospital.hmapp.ui.patient.PatientMainActivity;

import java.util.HashMap;


public class LauncherActivity extends Activity
{


    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences("prefs",0);
        boolean firstRun = settings.getBoolean("firstRun",false);



        if(firstRun == false)//if running for first time
        {
            SharedPreferences.Editor editor=settings.edit();
            editor.putBoolean("firstRun",true);
            editor.commit();
            Intent i = new Intent(LauncherActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            //setContentView(R.layout.home);
            // Session class instance
            session = new SessionManager(getApplicationContext());
            //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
            session.checkLogin();

            HashMap<String, String> user = session.getUserDetails();
            String Username = user.get(SessionManager.KEY_NAME);

            System.out.println("Session ID: "+SessionManager.KEY_ID);
            String userID = user.get(SessionManager.KEY_ID);
            int ID = 0;
            ID = Integer.parseInt(userID);


            String type;
            type = user.get(SessionManager.KEY_TYPE);


            System.out.println("username:" +Username);
            System.out.println("userID string:" +userID);
            System.out.println("userID int:" +ID);

            if(type != null && type.equals("Doctor"))
            {
                //ToDo goto doctor dashboard
                System.out.println("Goto doc dashboard");
                Intent doc_intent=new Intent(LauncherActivity.this,DoctorMainActivity.class);
                startActivity(doc_intent);
            }
            else if (type != null && type.equals("Patient"))
            {
                //ToDo goto patient dashboard
                System.out.println("Goto patient dashboard");
                Intent pat_intent=new Intent(LauncherActivity.this,PatientMainActivity.class);
                startActivity(pat_intent);
            }
            else
            {
                Intent no_intent=new Intent(LauncherActivity.this,LoginActivity.class);
                startActivity(no_intent);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClicklogout(View arg0)
    {
        // Clear the session data
        // This will clear all session data and
        // redirect user to LoginActivity
        session.logoutUser();
    }


}
