package com.manage.hospital.hmapp.ui;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.HashMap;

import android.content.SharedPreferences.Editor;

public class SessionManager
{

    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AndroidHivePref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "userId";
    public static final String KEY_TYPE = "type";

    public static final String HAS_ACCESS_TOKEN = "hasAuthToken";
    public static final String FITBIT_UID = "fitbitUserID";
    public static final String FITBIT_TOKEN = "fitbit_token";
    public static final String FITBIT_TOKEN_TYPE = "fitbitTokenType";
    public static final String FITBIT_FULL_AUTH = "fitbitFullAuth";

    public static final String EMERGENCY_CONTACT="emergencyContact";
    public static final String EMERGENCY_ID="emergencyID";

    public static final String FITBIT_SLEEP_DATA="fitbitSleep";
    public static final String FITBIT_CALORIES_DATA="fitbitCalories";
    public static final String FITBIT_HEART_RATE_DATA="fitbitHeartRate";
    public static final String FITBIT_STEPS_DATA="fitbitSteps";


    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String Username, String ID, String type)
    {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, Username);
        editor.putString(KEY_ID,ID);
        editor.putString(KEY_TYPE,type);
        editor.commit();
    }

    public void createFitbitSession(Boolean f1, String f2, String f3, String f4, String f5)
    {
        editor.putBoolean(HAS_ACCESS_TOKEN,f1);
        editor.putString(FITBIT_UID, f2);
        editor.putString(FITBIT_TOKEN,f3);
        editor.putString(FITBIT_TOKEN_TYPE,f4);
        editor.putString(FITBIT_FULL_AUTH,f5);
        editor.commit();

    }

    public void setFitbitData(String sleep_data,String calories,String heart_rate,String steps){

        editor.putString(FITBIT_SLEEP_DATA,sleep_data);
        editor.putString(FITBIT_CALORIES_DATA,calories);
        editor.putString(FITBIT_HEART_RATE_DATA,heart_rate);
        editor.putString(FITBIT_STEPS_DATA,steps);
        editor.commit();
    }


    public HashMap<String, Boolean>getFitbit()
    {
        HashMap<String, Boolean> fitbit = new HashMap<String, Boolean>();
        fitbit.put(HAS_ACCESS_TOKEN, pref.getBoolean(HAS_ACCESS_TOKEN,false));

        return fitbit;
    }

    public String getFitbitSleepData(){
        return pref.getString(FITBIT_SLEEP_DATA,"");
    }

    public String getFitbitCaloriesData(){
        return pref.getString(FITBIT_CALORIES_DATA,"");
    }

    public String getFitbitHeartRateData(){
        return pref.getString(FITBIT_HEART_RATE_DATA,"");
    }

    public String getFitbitStepsData(){
        return pref.getString(FITBIT_STEPS_DATA,"");
    }

    public Boolean hasFitbitToken(){
        boolean istoken=pref.getBoolean(HAS_ACCESS_TOKEN,false);
        return istoken;
    }

    public String getFitbitToken(){

        return pref.getString(FITBIT_TOKEN,"");
    }

    public String getFitbitUid(){

        return pref.getString(FITBIT_UID,"");
    }


    public void checkLogin()
    {
        System.out.println("Inside checklogin");
        if(!this.isLoggedIn())
        {
            System.out.println("Inside checklogin if statement");
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }


    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_ID, pref.getString(KEY_ID, "000"));
        user.put(KEY_TYPE, pref.getString(KEY_TYPE, null));

        return user;
    }

    public void createEmergencyContact(String contact, String ID)
    {
        editor.putString(EMERGENCY_ID, ID);
        editor.putString(EMERGENCY_CONTACT,contact);
    }

    public HashMap<String, String> getEmergencyContact()
    {
        HashMap<String, String> E = new HashMap<>();
        E.put(EMERGENCY_CONTACT,pref.getString(EMERGENCY_CONTACT,null));
        E.put(EMERGENCY_ID,pref.getString(EMERGENCY_ID,null));

        return E;
    }

    public void logoutUser()
    {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }


    public boolean isLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN, false);
    }

}