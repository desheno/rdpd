package com.example.ricetreatment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class AdminMenu extends AppCompatActivity {

    LinearLayout addNew, viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        addNew = findViewById(R.id.btnAddNew);
        viewList = findViewById(R.id.btnViewList);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, AddForm.class));
            }
        });

        viewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, Gallery.class));
            }
        });

    }
}