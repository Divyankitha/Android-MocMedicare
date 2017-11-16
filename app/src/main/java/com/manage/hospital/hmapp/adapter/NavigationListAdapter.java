package com.manage.hospital.hmapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.data.NavDrawerItem;

import java.util.ArrayList;


public class NavigationListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItem;

    public NavigationListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItem){
        this.context=context;
        this.navDrawerItem=navDrawerItem;
    }

    @Override
    public int getCount() {
        return navDrawerItem.size();
    }

    @Override
    public Object getItem(int position){
        return navDrawerItem.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            LayoutInflater mInflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView=mInflater.inflate(R.layout.drawer_list_item,null);
        }

        ImageView imageIcon=(ImageView)convertView.findViewById(R.id.drawer_menu_icon);
        TextView textTitle=(TextView)convertView.findViewById(R.id.drawer_menu_title);

        imageIcon.setImageResource(navDrawerItem.get(position).getIcon());
        textTitle.setText(navDrawerItem.get(position).getTitle());

        return convertView;
    }
}
