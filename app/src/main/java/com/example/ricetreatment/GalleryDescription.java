package com.example.ricetreatment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class GalleryDescription extends AppCompatActivity {

    ImageView singleImage;
    TextView singleDisease, singleDescription, singleOccurs, singleIdentify, singleManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_description);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Disease Information");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        singleImage = findViewById(R.id.singleImage);
        singleDisease = findViewById(R.id.singleDisease);
        singleDescription = findViewById(R.id.singleDescription);
        singleOccurs = findViewById(R.id.singleOccurs);
        singleIdentify = findViewById(R.id.singleIdentify);
        singleManage = findViewById(R.id.singleManage);

        Picasso.get().load(getIntent().getStringExtra("singleImage"))
                .placeholder(R.drawable.img_loading)
                .into(singleImage);

        singleDisease.setText(getIntent().getStringExtra("singleDisease"));
        singleDescription.setText(getIntent().getStringExtra("singleDescription"));
        singleOccurs.setText(getIntent().getStringExtra("singleOccurs"));
        singleIdentify.setText(getIntent().getStringExtra("singleIdentify"));
        singleManage.setText(getIntent().getStringExtra("singleManage"));

    }
}