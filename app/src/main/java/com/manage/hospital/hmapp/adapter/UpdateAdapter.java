package com.manage.hospital.hmapp.adapter;

import android.database.Cursor;

import com.manage.hospital.hmapp.model.Task;

import java.util.ArrayList;

public interface UpdateAdapter {
    public void updateTaskArrayAdapter(ArrayList<Task> tasks);
    public void updateTaskArrayAdapter(Cursor cursor);
    public void update();
}
