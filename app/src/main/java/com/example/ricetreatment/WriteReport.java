package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricetreatment.databinding.ActivityWriteReportBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeUrl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;


public class WriteReport extends AppCompatActivity {

    ActivityWriteReportBinding binding;
    Button btnSendAsPDF;
    EditText editDiseaseName, editLocationArea, editTreatmentPlan, editComments;

    DatabaseReference databaseReports;

    private FirebaseUser user;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    private String userEmail;
    String[] spinnerList = {"Select Field Type", "Rainfed", "Irrigated"};
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_report);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        binding = ActivityWriteReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Write A Report");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSendAsPDF = findViewById(R.id.btnSend);

        editDiseaseName = (EditText) findViewById(R.id.txtdiseaseName);
        spinner = (Spinner) findViewById(R.id.txtfieldType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        editLocationArea = (EditText) findViewById(R.id.txtlocationArea);
        editTreatmentPlan = (EditText) findViewById(R.id.txttreatment);
        editComments = (EditText) findViewById(R.id.txtcomments);

        databaseReports = FirebaseDatabase.getInstance().getReference();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View v) {

                if (!validateDisease() | !validateType() | !validateLocation() | !validateTreatment() | !validateComment()) {
                    Toast.makeText(WriteReport.this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    InsertData();
                    convertPDF();
                    onSharePDF();
                    editDiseaseName.setText("");
                    spinner.setSelection(0);
                    editLocationArea.setText("");
                    editTreatmentPlan.setText("");
                    editComments.setText("");
                    /*
                    String uEmail = binding.textEmail.getText().toString();
                    String diagnosis = binding.txtdiseaseName.getText().toString();
                    String fieldType = binding.txtfieldType.getText().toString();
                    String location = binding.txtlocationArea.getText().toString();
                    String treatment = binding.txttreatment.getText().toString();
                    String comments = binding.txtcomments.getText().toString();

                    String [] address = uEmail.split(",");

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, address);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Leafcross Report");
                    intent.putExtra(Intent.EXTRA_TEXT, "Diagnosis:\n " + diagnosis + "\n\n\nField Type:\n "+ fieldType + "\n\n\nLocation of The Problem:\n " + location + "\n\n\nTreatment Plan:\n " + treatment + "\n\n\nComments:\n " + comments);

                    startActivity(intent);
                     */
                }

            }
        });

        //Getting Email from Firebase
        auth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userEmail = user.getUid();

        final TextView emailTextView = findViewById(R.id.textEmail);
        //final TextView emailTextView = findViewById(R.id.fullEmail);

        reference.child(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if (userProfile != null) {
                    String email = userProfile.email;

                    emailTextView.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WriteReport.this, "Something wrong happened!", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }

    public void convertPDF() {
        String diagnose = editDiseaseName.getText().toString();
        String field = spinner.getSelectedItem().toString();
        String location = editLocationArea.getText().toString();
        String treatment = editTreatmentPlan.getText().toString();
        String comment = editComments.getText().toString();

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
        Toast.makeText(WriteReport.this, "PDF created successfully", Toast.LENGTH_SHORT).show();
    }

    public void onSharePDF() {
        String diagnose = editDiseaseName.getText().toString();

        String stringFilepath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + diagnose + "LeafcrossReport.pdf";
        File file = new File(stringFilepath);
        Log.d("File Path", "" + file.getAbsolutePath());
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = mimeTypeMap.getExtensionFromMimeType(ext);

        if (type == null) {
            type = "*/*";
        }
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Report File is attached in PDF format.");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Leafcross Report");

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri path = FileProvider.getUriForFile(WriteReport.this, "com.example.ricetreatment", file);
                intent.putExtra(Intent.EXTRA_STREAM, path);
            }
            else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            intent.setType("*/*");
            startActivity(intent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //Intent intent = getIntent();
        /* if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.equalsIgnoreCase("application/pdf")) {
                    handlePDFFile(intent);
                }
            }
            else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {

            }
        }
        if (file.exists()) {
            FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", file);

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            share.setType("application/pdf");
            startActivity(share);
        }
        */
    }

    private void InsertData() {
        String diagnosisName = editDiseaseName.getText().toString().trim();
        String fieldType = spinner.getSelectedItem().toString().trim();
        String locationArea = editLocationArea.getText().toString().trim();
        String treatmentPlan = editTreatmentPlan.getText().toString().trim();
        String comments = editComments.getText().toString().trim();
        String id = databaseReports.push().getKey();

        ReportClass reportClass = new ReportClass(diagnosisName, fieldType, locationArea, treatmentPlan, comments);
        databaseReports.child("Reports").child(id).setValue(reportClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(WriteReport.this, "Report is saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private Boolean validateDisease() {
        String val = editDiseaseName.getText().toString();
        if (val.isEmpty()) {
            editDiseaseName.setError("Diagnosis Name cannot be empty");
            return false;
        }
        else {
            editDiseaseName.setError(null);
            editDiseaseName.setError(null);
            editDiseaseName.setEnabled(true);
            return true;
        }
    }
    @SuppressLint("SetTextI18n")
    private Boolean validateType() {
        String val = spinner.getSelectedItem().toString();
        if (val.equals("Select Field Type")) {
            TextView errorText = (TextView)spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select Field Type");
            return false;
        }
        else {
            spinner.setEnabled(true);
            return true;
        }
    }
    private Boolean validateLocation() {
        String val = editLocationArea.getText().toString();
        if (val.isEmpty()) {
            editLocationArea.setError("Location of the problem cannot be empty");
            return false;
        }
        else {
            editLocationArea.setError(null);
            editLocationArea.setError(null);
            editLocationArea.setEnabled(true);
            return true;
        }
    }
    private Boolean validateTreatment() {
        String val = editTreatmentPlan.getText().toString();
        if (val.isEmpty()) {
            editTreatmentPlan.setError("Treatment Plan cannot be empty");
            return false;
        }
        else {
            editTreatmentPlan.setError(null);
            editTreatmentPlan.setError(null);
            editTreatmentPlan.setEnabled(true);
            return true;
        }
    }
    private Boolean validateComment() {
        String val = editComments.getText().toString();
        if (val.isEmpty()) {
            editComments.setError("Comment cannot be empty");
            return false;
        }
        else {
            editComments.setError(null);
            editComments.setError(null);
            editComments.setEnabled(true);
            return true;
        }
    }
}