package com.manage.hospital.hmapp.data;

import java.util.ArrayList;
import java.util.List;


public class PatientData {

    public List<PatientStructure> data=null;
    private static PatientData instance=null;

    public PatientData(){
        data=new ArrayList<>();
    }

    public static PatientData getInstance(){
        if(instance==null){
            instance=new PatientData();
        }

        return instance;
    }

    public void add(PatientStructure patObj){
        data.add(patObj);
    }

    public PatientStructure get(int pos){
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
