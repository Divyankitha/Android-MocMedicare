package com.manage.hospital.hmapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manage.hospital.hmapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FitbitListAdapter extends RecyclerView.Adapter<FitbitListAdapter.FitbitViewHolder>{

    private Context context;
    private LinkedHashMap<String,Double> fitbitMap;


    public FitbitListAdapter(Context mContext, LinkedHashMap<String,Double> fitbit_map){
        this.context=mContext;
        this.fitbitMap=fitbit_map;
    }

    @Override
    public FitbitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fitbit_list_item, parent, false);

        return new FitbitViewHolder(itemView);
    }

    public void refreshList(LinkedHashMap<String,Double> fitbit_map){
        this.fitbitMap=fitbit_map;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(FitbitViewHolder holder, int position) {

        Double value = (new ArrayList<Double>(fitbitMap.values())).get(position);

        String key = (new ArrayList<String>(fitbitMap.keySet())).get(position);

        if(key.equals("totalMinutesAsleep")){
            holder.textTitle.setText(context.getResources().getString(R.string.fitbit_sleep_title));
            holder.imgIcon.setImageResource(R.drawable.ic_sleep);
            Double hours=value/60;
            Double mins=value%60;
            holder.textValue.setText(String.valueOf(hours.intValue())+" hours "+String.valueOf(mins.intValue())+" mins");
        }else if(key.equals("caloriesBurnt")){
            holder.textTitle.setText(context.getResources().getString(R.string.fitbit_calories_title));
            holder.imgIcon.setImageResource(R.drawable.ic_calories);
            holder.textValue.setText(String.valueOf(value.intValue())+" "+"cals");
        }else if(key.equals("restingHeartRate")){
            holder.textTitle.setText(context.getResources().getString(R.string.fitbit_heart_rate_title));
            holder.imgIcon.setImageResource(R.drawable.ic_heart_rate);
            holder.textValue.setText(String.valueOf(value)+" "+"bpm");
        }else if(key.equals("steps")){
            holder.textTitle.setText(context.getResources().getString(R.string.fitbit_steps_title));
            holder.imgIcon.setImageResource(R.drawable.ic_steps);
            Integer final_val=value.intValue();
            holder.textValue.setText(final_val+" "+"steps");
        }


        //holder.imgIcon.setText(fitbitStructure.getDonationStatus());

    }

    @Override
    public int getItemCount() {
        return fitbitMap.size();
    }

    public class FitbitViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle,textValue;
        ImageView imgIcon;

        public FitbitViewHolder(View itemView) {
            super(itemView);

            textTitle=(TextView) itemView.findViewById(R.id.text_fitbit_title);
            textValue=(TextView)itemView.findViewById(R.id.text_fitbit_val);
            imgIcon=(ImageView) itemView.findViewById(R.id.img_fitbit);

        }
    }
}
