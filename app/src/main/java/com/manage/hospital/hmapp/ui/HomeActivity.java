package com.manage.hospital.hmapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.manage.hospital.hmapp.R;



public class HomeActivity extends Activity
{
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstpage);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public  void gotoRegister(View v)
    {
        Intent intent = new Intent(HomeActivity.this,Registration.class);
        startActivity(intent);
    }

    public  void gotoLogin(View v)
    {
        Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
