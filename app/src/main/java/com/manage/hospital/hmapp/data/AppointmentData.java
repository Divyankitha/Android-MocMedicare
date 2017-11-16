package com.manage.hospital.hmapp.data;

import java.util.ArrayList;
import java.util.List;


public class AppointmentData {

    public List<AppointmentStructure> data=null;
    private static AppointmentData instance=null;

    public AppointmentData(){
        data=new ArrayList<>();
    }

    public static AppointmentData getInstance(){
        if(instance==null){
            instance=new AppointmentData();
        }

        return instance;
    }

    public void add(AppointmentStructure appObj){
        data.add(appObj);
    }

    public AppointmentStructure get(int pos){
        if(data!=null && data.size()>pos){
            return data.get(pos);
        }
        return null;
    }

    public int getSize(){
        return data.size();
    }

    public void clear(){
        data.clear();
    }
}
