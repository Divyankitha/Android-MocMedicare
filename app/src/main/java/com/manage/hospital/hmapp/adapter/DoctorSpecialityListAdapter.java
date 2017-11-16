package com.manage.hospital.hmapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.manage.hospital.hmapp.R;

import java.util.List;



public class DoctorSpecialityListAdapter extends ArrayAdapter {
    private Context context;
    int textViewResourceId = 0;
    private int resource;
    private static LayoutInflater inflater = null;
    List<String> specialityList;

    public DoctorSpecialityListAdapter(Context context, int textViewResourceId, List<String> list) {
        super(context, textViewResourceId, list);
        this.context = context;
        this.specialityList = list;
        this.resource = textViewResourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.custom_spinner, parent, false);
        TextView textview = (TextView) row.findViewById(R.id.text_spinner);
        textview.setText(specialityList.get(position));
        return row;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.custom_spinner, parent, false);
        TextView textview = (TextView) row.findViewById(R.id.text_spinner);
        textview.setText(specialityList.get(position));
        return row;
    }
}

