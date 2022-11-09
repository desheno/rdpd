package com.example.ricetreatment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ViewHolder>  {

    //ArrayList<DiseaseModel> list;
    List<DiseaseModel> diseaseList = new ArrayList<>();
    Context context;

    public DiseaseAdapter(ArrayList<DiseaseModel> list, Context context) {
        this.diseaseList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_custom_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiseaseModel model = diseaseList.get(position);

        Picasso.get().load(model.getDiseaseImage()).placeholder(R.drawable.img_loading).into(holder.myImage);

        holder.diseaseName.setText(model.getDiseaseName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GalleryDescription.class);
                intent.putExtra("singleImage", model.getDiseaseImage());
                intent.putExtra("singleDisease", model.getDiseaseName());
                intent.putExtra("singleDescription", model.getDiseaseDescription());
                intent.putExtra("singleOccurs", model.getWhereOccurs());
                intent.putExtra("singleIdentify", model.getHowIdentify());
                intent.putExtra("singleManage", model.getTreatmentPlan());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView diseaseName;
        ImageView myImage;
        RelativeLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            diseaseName = itemView.findViewById(R.id.diseaseName);
            myImage = itemView.findViewById(R.id.imageDisease);
            mainLayout = itemView.findViewById(R.id.mainLayout);

        }
    }
}
