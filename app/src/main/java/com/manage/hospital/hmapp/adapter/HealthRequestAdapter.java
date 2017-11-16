package com.manage.hospital.hmapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manage.hospital.hmapp.Extras.Interface.PatientAdapterToPatientFragment;
import com.manage.hospital.hmapp.Extras.Interface.RequestsAdapterToRequestFragment;
import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.data.HealthDataRequestStructure;
import com.manage.hospital.hmapp.data.PatientStructure;

import java.util.List;


public class HealthRequestAdapter extends RecyclerView.Adapter<HealthRequestAdapter.HealthRequestViewHolder>{

    private Context context;
    private List<HealthDataRequestStructure> requestList;
    RequestsAdapterToRequestFragment itemClickListener;

    public HealthRequestAdapter(Context mContext, List<HealthDataRequestStructure> request_list) {
        this.context = mContext;
        this.requestList = request_list;
    }

    @Override
    public HealthRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_list_item, parent, false);

        return new HealthRequestViewHolder(itemView);
    }

    public void refreshList(List<HealthDataRequestStructure> request_list){
        this.requestList=request_list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(HealthRequestViewHolder holder, int position) {

        HealthDataRequestStructure appObj = requestList.get(position);
        holder.docName.setText(appObj.getDoc_name());
        holder.reqDesc.setText(appObj.getDesc());

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class HealthRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView docName,reqDesc;
        ImageView imgAccept,imgDecline;

        public HealthRequestViewHolder(View itemView) {
            super(itemView);

            docName = (TextView) itemView.findViewById(R.id.card_req_doctor_name);
            reqDesc = (TextView) itemView.findViewById(R.id.card_req_desc);
            imgAccept=(ImageView) itemView.findViewById(R.id.img_req_accept);
            imgAccept.setOnClickListener(this);
            imgDecline=(ImageView) itemView.findViewById(R.id.img_req_decline);
            imgDecline.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            if(view.getId()==R.id.img_req_accept){
                itemClickListener.onRequestItemClick(requestList.get(getAdapterPosition()).request_id,"accept");
            }else if(view.getId()==R.id.img_req_decline){
                itemClickListener.onRequestItemClick(requestList.get(getAdapterPosition()).request_id,"decline");
            }
        }
    }

    public void setOnItemClickListener(final RequestsAdapterToRequestFragment reqItemClickListener){
        this.itemClickListener=reqItemClickListener;
    }

}
