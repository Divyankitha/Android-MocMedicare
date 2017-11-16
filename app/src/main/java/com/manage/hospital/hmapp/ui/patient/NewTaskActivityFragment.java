package com.manage.hospital.hmapp.ui.patient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manage.hospital.hmapp.R;


public class NewTaskActivityFragment extends Fragment {

    public NewTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_task, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
