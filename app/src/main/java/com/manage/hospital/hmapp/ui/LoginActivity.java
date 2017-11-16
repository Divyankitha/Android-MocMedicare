package com.manage.hospital.hmapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.data.LoginInfo;
import com.manage.hospital.hmapp.utility.ConfigConstant;
import com.manage.hospital.hmapp.utility.encryptPasscode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends Activity
{

    EditText txtUsername, txtPassword;

    Button btnLogin;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        System.out.println("Inside login activity");
        //System.setProperty("http.keepAlive", "false");

        session = new SessionManager(getApplicationContext());

        txtUsername = (EditText) findViewById(R.id.uname_login);
        txtPassword = (EditText) findViewById(R.id.password_login);
        RadioButton rd1 = (RadioButton) findViewById(R.id.doc_rd);
        RadioButton rd2 = (RadioButton) findViewById(R.id.patient_rd);

        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        btnLogin = (Button) findViewById(R.id.login);


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                Bundle bundle = getIntent().getExtras();
                RadioButton rd1 = (RadioButton) findViewById(R.id.doc_rd);
                RadioButton rd2 = (RadioButton) findViewById(R.id.patient_rd);


                LoginInfo L = new LoginInfo();

                if (username.trim().length() > 0 && password.trim().length() > 0)
                {
                    L.setUsername(username);
                    L.setPassword(password);
                    if (rd1.isChecked())
                        new AsyncTaskLoginDoc().execute(L);
                    else
                        new AsyncTaskLoginPatient().execute(L);
                }

                else
                {

                    alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
                }

            }

        });
    }

    public class AsyncTaskLoginDoc extends AsyncTask<LoginInfo, String, LoginInfo> {

        HttpResponse response;
        SessionManager session;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LoginInfo doInBackground(LoginInfo... params)
        {

            String password = encryptPasscode.encryptPassword(params[0].Password);

            try
            {
                JSONObject requestBody = new JSONObject();
                requestBody.put("username", params[0].Username);
                requestBody.put("password",password); //ToDo hashcode of password
                String request = requestBody.toString();
                StringEntity request_param = new StringEntity(request);

                String Url= ConfigConstant.BASE_URL+ConfigConstant.authenticateDoctor;
                HttpPost post = new HttpPost(Url);
                post.setHeader("Content-Type","application/json");
                post.setEntity(request_param);
                HttpClient httpClient = new DefaultHttpClient();
                response = httpClient.execute(post);
                System.out.println("Reached after coming back from Backend API");
                if (response.getStatusLine().getStatusCode() != 200)
                {
                    if(response.getStatusLine().getStatusCode() == 401)
                    {
                        System.out.println("Verification failed");
                    }
                    else if(response.getStatusLine().getStatusCode() == 400)
                    {
                        System.out.println("Verification failed");
                    }
                    else
                      throw new RuntimeException("Failed: HTTP error code :" + response.getStatusLine().getStatusCode());
                }
                else
                {
                    HttpEntity e = response.getEntity();
                    String i = EntityUtils.toString(e);
                    JSONObject j = new JSONObject(i);
                    if(!i.equals("Request Failed"))
                    {
                        //int userID = Integer.parseInt(i);
                        int userID = j.getInt("Doctor ID");
                        params[0].setID(userID);
                    }
                }
            }
            catch(Exception x)
            {
                throw new RuntimeException("Error from authenticate doc api",x);
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(LoginInfo L)
        {
            super.onPostExecute(L);
            String userID;
            userID = String.valueOf(L.ID);
            if(L.ID != 0)
            {
                session = new SessionManager(getApplicationContext());
                session.createLoginSession(L.Username, userID, "Doctor");
                Intent i = new Intent(getApplicationContext(), LauncherActivity.class); //ToDo doctor dashboard
                startActivity(i);
                finish();
            }
            else
            {
                alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
            }
        }

    }

    public class AsyncTaskLoginPatient extends AsyncTask<LoginInfo, Void, LoginInfo> {

        HttpResponse response;
        SessionManager session;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LoginInfo doInBackground(LoginInfo... params)
        {

            LoginInfo loginInfo=params[0];

            String password = encryptPasscode.encryptPassword(loginInfo.Password);

            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String responseJson;
            try
            {

                String baseUrl= ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.authenticatePatient;

                Uri loginUri=Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).build();

                URL url=new URL(loginUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                try {
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("username", params[0].Username);
                    requestBody.put("password", password);

                    OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream());
                    os.write(requestBody.toString());
                    os.close();


                    int HttpResult = urlConnection.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream=urlConnection.getInputStream();
                        StringBuffer buffer=new StringBuffer();
                        if(inputStream==null){
                            return null;
                        }

                        reader=new BufferedReader(new InputStreamReader(inputStream));

                        String line;

                        while((line=reader.readLine())!=null){
                            buffer.append(line+"\n");
                        }
                        if(buffer.length()==0){
                            return null;
                        }
                        responseJson=buffer.toString();

                        JSONObject jsonObject=new JSONObject(responseJson);
                        int uid=jsonObject.getInt("Patient ID");
                        loginInfo.setID(uid);
                    }
                }catch (JSONException e){
                    Log.e("Error",e.getMessage());
                }
                finally {
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch (final IOException e){
                            Log.e("Error","Error closing stream",e);
                        }
                    }

                }
                /*if (response.getStatusLine().getStatusCode() != 200)
                {
                    if(response.getStatusLine().getStatusCode() == 401)
                    {
                        System.out.println("Verification failed");
                    }
                    else if(response.getStatusLine().getStatusCode() == 400)
                    {
                        System.out.println("Verification failed");
                    }
                    else
                    {
                        throw new RuntimeException("Failed: HTTP error code :" + response.getStatusLine().getStatusCode());
                    }
                }
                else
                {
                    HttpEntity e = response.getEntity();
                    String i = EntityUtils.toString(e);
                    JSONObject j = new JSONObject(i);
                    if(!i.equals("Request Failed"))
                    {
                        //int userID = Integer.parseInt(i);
                        int userID = j.getInt("Patient ID");
                        params[0].setID(userID);
                    }
                }*/
            } catch (IOException e){

                Log.e("Error",e.getMessage(),e);
                return null;

            }
            return loginInfo;
        }

        @Override
        protected void onPostExecute(LoginInfo L)
        {
            super.onPostExecute(L);
            String userID;
            userID = String.valueOf(L.ID);
            if(L.ID != 0)
            {
                session = new SessionManager(getApplicationContext());
                session.createLoginSession(L.Username, userID,"Patient");
                Intent i = new Intent(getApplicationContext(), LauncherActivity.class); //ToDo Patient dashboard
                startActivity(i);
                finish();
            }
            else
            {
                alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
            }
        }

    }
    public void gotoHome(View V)
    {
        Intent intent = new Intent(LoginActivity.this,LauncherActivity.class);
        startActivity(intent);
    }

    public void finishLoginActivity(View V)
    {
        LoginActivity.this.finish();;
    }

}

