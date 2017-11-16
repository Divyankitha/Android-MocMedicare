package com.manage.hospital.hmapp.ui.patient;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.manage.hospital.hmapp.Extras.Interface.PatientDashboardFragmentToActivity;
import com.manage.hospital.hmapp.Extras.broadcast_receiver.FallDetectService;
import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.adapter.NavigationListAdapter;
import com.manage.hospital.hmapp.data.NavDrawerItem;
import com.manage.hospital.hmapp.ui.SessionManager;
import com.manage.hospital.hmapp.utility.ConfigConstant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PatientMainActivity extends AppCompatActivity implements PatientDashboardFragmentToActivity {

    private String[] drawerTitleArray;
    private TypedArray drawerIconsArray;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationListAdapter menuListAdapter;
    private ListView drawerList;
    private DrawerLayout drawerMenuLayout;
    private Boolean isMenuItemClicked = false;
    SessionManager sessionManager;

    Toolbar toolbar;
    TextView textViewToolbarTitle;
    TextView textUserName;
    Intent intent;
    String patient_id, patient_name;
    public static String contactNo;


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    long mStartTimestamp;
    boolean mShouldLog;
    int mCountAccelUpdates;
    LocationManager locationMangaer = null;

    String fitbitToken, fitbitUid, currentDate;
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);

        setListeners();

        locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sessionManager = new SessionManager(PatientMainActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        //System.out.println("Emer Contact from shared pref:"+emergencyContact.get(SessionManager.EMERGENCY_CONTACT));
        //contactNo=emergencyContact.get(SessionManager.EMERGENCY_CONTACT);
        patient_id = user.get(SessionManager.KEY_ID);
        patient_name = user.get(SessionManager.KEY_NAME);

        TextView name = (TextView) findViewById(R.id.username);
        name.setText(patient_name);

        fitbitToken = sessionManager.getFitbitToken();
        fitbitUid = sessionManager.getFitbitUid();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDateTime[] = sdf.format(calendar.getTime()).split(" ");
        currentDate = strDateTime[0];

        if (savedInstanceState == null) {
            Fragment fragment = new PatientDashboardFragment();
            Bundle bundle = new Bundle();
            bundle.putString("token", fitbitToken);
            bundle.putString("user_id", fitbitUid);
            bundle.putString("date", currentDate);
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.patient_content_frame, fragment).commit();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {

            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED))
            {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }

        }

        mShouldLog = false;
        mCountAccelUpdates = 0;
        mStartTimestamp = System.currentTimeMillis();

        new AsyncTaskCheckEmergency().execute(Integer.parseInt(patient_id));

        Intent intent = new Intent(this, FallDetectService.class);
        startService(intent);

        registerReceiver(accelDataReceiver, new IntentFilter(FallDetectService.ACCEL_DATA_NOTIFICATION));
        registerReceiver(fallDetectionReceiver, new IntentFilter(FallDetectService.FALL_NOTIFICATION));

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void setListeners() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        textViewToolbarTitle.setText(getResources().getString(R.string.home_activity_title));
        textUserName = (TextView) findViewById(R.id.username);

        drawerMenuLayout = (DrawerLayout) findViewById(R.id.drawer_menu_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerTitleArray = getResources().getStringArray(R.array.pat_nav_drawer_items);
        drawerIconsArray = getResources().obtainTypedArray(R.array.pat_nav_drawer_icons);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[0], drawerIconsArray.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[1], drawerIconsArray.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[2], drawerIconsArray.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[3], drawerIconsArray.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[4], drawerIconsArray.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[5], drawerIconsArray.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[6], drawerIconsArray.getResourceId(6, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[7], drawerIconsArray.getResourceId(7, -1)));

        menuListAdapter = new NavigationListAdapter(getApplicationContext(), navDrawerItems);
        drawerList.setAdapter(menuListAdapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawerMenuLayout, toolbar, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {

                if (isMenuItemClicked) {
                    int position = drawerList.getCheckedItemPosition();
                    displayActivity(position);
                    isMenuItemClicked = false;
                }
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerMenuLayout.addDrawerListener(drawerToggle);
        drawerList.setOnItemClickListener(new MenuItemClickListener());
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    private class MenuItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            isMenuItemClicked = true;
            //drawerList.setItemChecked(position,true);
            //drawerList.setSelection(position);
            drawerMenuLayout.closeDrawer(GravityCompat.START);
            //displayActivity(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //PatientDashboardFragment patientFragment=(PatientDashboardFragment) getSupportFragmentManager().findFragmentById(R.id.patient_content_frame);
        //patientFragment.updatePatientDashboard(fitbitToken,fitbitUid,currentDate);


        /*Fragment frag=getSupportFragmentManager().findFragmentById(R.id.patient_content_frame);
        if(frag instanceof PatientDashboardFragment){
            PatientDashboardFragment patientFragment=(PatientDashboardFragment)frag;
            patientFragment.updatePatientDashboard(fitbitToken,fitbitUid,currentDate);
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(accelDataReceiver);
        //unregisterReceiver(fallDetectionReceiver);
    }


    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, FallDetectService.class);
        stopService(intent);
        super.onDestroy();
        unregisterReceiver(accelDataReceiver);
        unregisterReceiver(fallDetectionReceiver);
    }

    public void displayActivity(int position) {
        switch (position) {
            case 1:
                intent = new Intent(this, PatientAppointmentActivity.class);
                intent.putExtra("PatientId", patient_id);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(PatientMainActivity.this, DoctorActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(PatientMainActivity.this, ReminderMainActivity.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(PatientMainActivity.this, ActivityHealthDataRequests.class);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(this, ManageEmergencyContactActivity.class);
                intent.putExtra("PatientId", patient_id);
                startActivity(intent);
                break;
            case 6:
                Intent intent = new Intent(PatientMainActivity.this, PatientSourceActivity.class);
                startActivity(intent);
                break;
            case 7:
                sessionManager.logoutUser();
                break;
        }

    }


    private BroadcastReceiver fallDetectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            long currentTime = System.currentTimeMillis();

            sendSMS();

        }
    };

    private BroadcastReceiver accelDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Double accelData = intent.getDoubleExtra(FallDetectService.ACCEL_DATA_KEY, 0);

            if (mShouldLog) {

                long currentTime = System.currentTimeMillis();
                if (mCountAccelUpdates < 100) {
                    mCountAccelUpdates++;
                } else {
                    mCountAccelUpdates = 1;

                }

            }
        }
    };


    public void sendSMS() {
        ArrayList<String> location = getLocation();
        try {
            System.out.println("Em contact: " + contactNo);
            SmsManager.getDefault().sendTextMessage(contactNo, null, "Alert:Patient has fallen, attention needed!" + "\n Location:http://maps.google.com/?q=" + location.get(0)+"," +location.get(1), null, null);
            Toast.makeText(getApplicationContext(), "Alert message sent to emergency contact", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setMessage(e.getMessage());
            dialog.show();
        }

    }

    public ArrayList<String> getLocation() {


        Location currentlocation = locationMangaer.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Double lat = currentlocation.getLatitude();
        Double lng = currentlocation.getLongitude();
        String lat_str = lat.toString();
        String lng_str = lng.toString();

        ArrayList<String> location = new ArrayList();
        location.add(0, lat_str);
        location.add(1, lng_str);

        return location;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onFragmentInteraction() {

    }

    public class AsyncTaskCheckEmergency extends AsyncTask<Integer, String, ArrayList> {

        HttpResponse response;
        SessionManager session;
        ArrayList<String> emergency = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Integer... params) {
            try {


                String Url = ConfigConstant.BASE_URL + ConfigConstant.PATIENT_EMERGENCY + params[0];
                HttpGet get = new HttpGet(Url);
                HttpClient httpClient = new DefaultHttpClient();
                response = httpClient.execute(get);
                System.out.println("Reached after coming back from Backend API");
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed: HTTP error code :" + response.getStatusLine().getStatusCode());
                } else {
                    HttpEntity e = response.getEntity();
                    String i = EntityUtils.toString(e);
                    System.out.println(i);
                    JSONObject j = new JSONObject(i);
                    if (j.length() == 0) {
                        System.out.println("No emergency contact");
                    } else {
                        emergency.add(j.getString("Contact"));
                        emergency.add(j.getString("Dependent_Id"));
                    }
                }
            } catch (Exception x) {
                throw new RuntimeException("Error from get emergency contact api", x);
            }

            return emergency;
        }

        @Override
        protected void onPostExecute(ArrayList E) {
            super.onPostExecute(E);
            if (!E.isEmpty()) {
                session = new SessionManager(getApplicationContext());
                System.out.println(E.get(0).toString() + E.get(1).toString());
                session.createEmergencyContact(E.get(0).toString(), E.get(1).toString());
                HashMap<String, String> emergencyContact = sessionManager.getEmergencyContact();
                //contactNo = emergencyContact.get(SessionManager.EMERGENCY_CONTACT);
                contactNo = E.get(0).toString();
            } else {
                //AlertDialogManager alert = new AlertDialogManager();
                //alert.showAlertDialog(PatientMainActivity.this, "Kindly Add Emergency Contact!", "Navigate to Emergency Contact on the Side Menu", false);

                new android.support.v7.app.AlertDialog.Builder(PatientMainActivity.this).setTitle("Add Emergency Contact")
                        .setMessage("Navigate to Emergency Contact on the Side Menu")
                        .setPositiveButton(R.string.apmt_dialog_btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                Intent intent = new Intent(PatientMainActivity.this, ManageEmergencyContactActivity.class);
                                intent.putExtra("PatientId", patient_id);
                                startActivity(intent);
                            }
                        })
                        .show();
                //TODO goto ManageEmergencyContactActivity
            }
        }
    }

}
