package com.example.ricetreatment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {
    Context context;
    ArrayList<ReportClass>list;

    public ReportAdapter(Context context, ArrayList<ReportClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reports_entry, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.MyViewHolder holder, int position) {
        ReportClass reportClass = list.get(position);

        holder.diseaseName.setText(reportClass.getDiseaseName());
        holder.comments.setText(reportClass.getComments());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportDetails.class);
                intent.putExtra("singleDiagnosis", reportClass.getDiseaseName());
                intent.putExtra("singleField", reportClass.getFieldType());
                intent.putExtra("singleLocation", reportClass.getLocationArea());
                intent.putExtra("singleTreatment", reportClass.getTreatmentPlan());
                intent.putExtra("singleComment", reportClass.getComments());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView diseaseName, comments;
        RelativeLayout mainLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            diseaseName = itemView.findViewById(R.id.diseaseName);
            comments = itemView.findViewById(R.id.viewReport);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
