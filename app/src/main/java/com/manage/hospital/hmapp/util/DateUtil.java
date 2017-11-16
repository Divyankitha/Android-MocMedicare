package com.manage.hospital.hmapp.util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {
    private SimpleDateFormat sdf;

    public DateUtil(Context context) {
        this.sdf = new SimpleDateFormat();
    }

    public String parse(Date date){
        if(date == null){
            return "";
        }
        return new SimpleDateFormat("EEE").format(date) + ", " + sdf.format(date);
    }
}
