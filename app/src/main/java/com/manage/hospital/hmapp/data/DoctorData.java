package com.manage.hospital.hmapp.data;

import java.util.ArrayList;
import java.util.List;



public class DoctorData {

    public List<DoctorStructure> data=null;
    private static DoctorData instance=null;

    public DoctorData(){
        data=new ArrayList<>();
    }

    public static DoctorData getInstance(){
        if(instance==null){
            instance=new DoctorData();
        }

        return instance;
    }

    public void add(DoctorStructure patObj){
        data.add(patObj);
    }

    public DoctorStructure get(int pos){
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
