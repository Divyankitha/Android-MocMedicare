package com.manage.hospital.hmapp.ui.patient;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.ui.SessionManager;
import com.manage.hospital.hmapp.utility.ConfigConstant;
import com.manage.hospital.hmapp.utility.FitbitReferences;


public class FitBitDetailsFragment extends Fragment {


    SharedPreferences sharedPref;
    Button btnLogin;
    Button btnLogout;
    //LinearLayout layoutFitbitControls;

    private CustomTabsClient customTabsClient;
    private CustomTabsIntent customTabsIntent;
    private CustomTabsSession customTabsSession;
    private CustomTabsServiceConnection customTabsServiceConnection;

    Boolean isAuthorized;
    SessionManager sessionManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_fit_bit_details,container,false);
        btnLogin=(Button)view.findViewById(R.id.fitbit_login_button);
        btnLogout=(Button)view.findViewById(R.id.fitbit_logout_button);
        //layoutFitbitControls=(LinearLayout)view.findViewById(R.id.fitbit_controls_layout);

        sessionManager=new SessionManager(getActivity());

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity());

        customTabsServiceConnection=new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {

                customTabsClient=client;
                customTabsClient.warmup(0L);
                customTabsSession=customTabsClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

                customTabsClient=null;
            }
        };



        CustomTabsClient.bindCustomTabsService(getActivity(), ConfigConstant.PACKAGE_CUSTOM_TAB,customTabsServiceConnection);

        customTabsIntent=new CustomTabsIntent.Builder(customTabsSession)
                .setToolbarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary))
                .setShowTitle(true)
                .build();

        isAuthorized=sessionManager.hasFitbitToken();

        if(!isAuthorized){
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.INVISIBLE);
        }else{
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            //layoutFitbitControls.setVisibility(View.VISIBLE);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAuthorized){
                    String fitbit_auth_url=ConfigConstant.FITBIT_AUTH_URL;
                    customTabsIntent.launchUrl(getActivity(),Uri.parse(fitbit_auth_url));
                }else{
                    Log.d("Log","Already logged in");
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

}
