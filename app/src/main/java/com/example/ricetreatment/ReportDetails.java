package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReportDetails extends AppCompatActivity {

    private static final int STORAGE_CODE = 1000;
    TextView singleDiagnosis, singleField, singleLocation, singleTreatment, singleComment;
    Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Report Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        singleDiagnosis = findViewById(R.id.singleDiagnosis);
        singleField = findViewById(R.id.singleField);
        singleLocation = findViewById(R.id.singleLocation);
        singleTreatment = findViewById(R.id.singleTreatment);
        singleComment = findViewById(R.id.singleComment);

        mSaveBtn = findViewById(R.id.saveBtn);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        singleDiagnosis.setText(getIntent().getStringExtra("singleDiagnosis"));
        singleField.setText(getIntent().getStringExtra("singleField"));
        singleLocation.setText(getIntent().getStringExtra("singleLocation"));
        singleTreatment.setText(getIntent().getStringExtra("singleTreatment"));
        singleComment.setText(getIntent().getStringExtra("singleComment"));

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String diagnose = singleDiagnosis.getText().toString();
                String field = singleField.getText().toString();
                String location = singleLocation.getText().toString();
                String treatment = singleTreatment.getText().toString();
                String comment = singleComment.getText().toString();

                String stringFilepath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + diagnose + "LeafcrossReport.pdf";
                File file = new File(stringFilepath);

                Document document = new Document(PageSize.A4);
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                }

                document.open();
                Font myFont = new Font(Font.FontFamily.HELVETICA, 24, Font.NORMAL);
                Font myHeader = new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD);

                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph("LEAFCROSS REPORT DETAILS", myHeader));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Diagnosis: " + diagnose, myFont));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Field Type: " + field, myFont));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Location of the Problem: " + location, myFont));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Treatment Plan: " + treatment, myFont));
                paragraph.add(new Paragraph("\n"));
                paragraph.add(new Paragraph("Comment: " + comment, myFont));

                try {
                    document.add(paragraph);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                document.close();
                Toast.makeText(ReportDetails.this, "PDF created successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
            }
        });
    }
}