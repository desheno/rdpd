package com.example.ricetreatment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.fieldType.setText(reportClass.getFieldType());
        holder.locationProblem.setText(reportClass.getLocationArea());
        holder.treatmentPlan.setText(reportClass.getTreatmentPlan());
        holder.comments.setText(reportClass.getComments());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView diseaseName, fieldType, locationProblem, treatmentPlan, comments;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            diseaseName = itemView.findViewById(R.id.textDiseaseName);
            fieldType = itemView.findViewById(R.id.textFieldType);
            locationProblem = itemView.findViewById(R.id.textLocationProblem);
            treatmentPlan = itemView.findViewById(R.id.textTreatmentPlan);
            comments = itemView.findViewById(R.id.textComments);
        }
    }
}
