package com.manage.hospital.hmapp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.data.AppointmentStructure;
import com.manage.hospital.hmapp.data.PatientAppointmentStructure;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PatientAppointmentListAdapter extends RecyclerView.Adapter<PatientAppointmentListAdapter.AppointmentViewHolder> {

    private Context context;
    private List<PatientAppointmentStructure> appmtList;

    public PatientAppointmentListAdapter(Context mContext, List<PatientAppointmentStructure> appmt_list){
        this.context=mContext;
        this.appmtList=appmt_list;
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_appointment_list_item, parent, false);

        return new AppointmentViewHolder(itemView);
    }

    public void refreshList(List<PatientAppointmentStructure> donation_list){
        this.appmtList=donation_list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, int position) {

        PatientAppointmentStructure appObj = appmtList.get(position);
        if(appObj.getAppointment_status().equals(context.getResources().getString(R.string.appt_status_requested))) {
            holder.status.setText("Request Pending");
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.yellowColor));
        }else if(appObj.getAppointment_status().equals(context.getResources().getString(R.string.appt_status_accepted))){
            holder.status.setText(appObj.getAppointment_status());
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.greenColor));
        }else if(appObj.getAppointment_status().equals(context.getResources().getString(R.string.appt_status_declined))){
            holder.status.setText(appObj.getAppointment_status());
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.redColor));
        }
        String date=convertDate(appObj.getAppointment_date_time());
        holder.appDate.setText(date);
        holder.appDesc.setText(appObj.getAppointment_desc());

    }

    private String convertDate(String input_date){
        SimpleDateFormat inputDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date p_date=null;
        try{
            p_date=inputDateFormat.parse(input_date);
        }catch (ParseException e){
            e.printStackTrace();
        }

        Format finalDateFormat=new SimpleDateFormat("MMM dd yyyy");
        String finalDate=finalDateFormat.format(p_date);
        return finalDate;
    }

    @Override
    public int getItemCount() {
        return appmtList.size();
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder{

        TextView status,appDate,appDesc;
        public AppointmentViewHolder(View itemView) {
            super(itemView);

            status = (TextView) itemView.findViewById(R.id.card_app_status);
            appDate = (TextView) itemView.findViewById(R.id.card_app_date);
            appDesc=(TextView)itemView.findViewById(R.id.card_pat_app_desc);

        }
    }



}
