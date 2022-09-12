package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ricetreatment.databinding.ActivityWriteReportBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeUrl;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class WriteReport extends AppCompatActivity {

    ActivityWriteReportBinding binding;
    Button btnSubmit;
    EditText editDiseaseName, editFieldType, editLocationArea, editTreatmentPlan, editComments;

    DatabaseReference databaseReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_report);

        binding = ActivityWriteReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnSubmit = findViewById(R.id.btnSubmit);
        editDiseaseName = (EditText) findViewById(R.id.txtdiseaseName);
        editFieldType = (EditText) findViewById(R.id.txtfieldType);
        editLocationArea = (EditText) findViewById(R.id.txtlocationArea);
        editTreatmentPlan = (EditText) findViewById(R.id.txttreatment);
        editComments = (EditText) findViewById(R.id.txtcomments);

        databaseReports = FirebaseDatabase.getInstance().getReference();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = "rj@gmail.com";
                final String passWord = "1234567";
                String messageToSend = editDiseaseName.getText().toString();
                Properties props = new Properties();
                props.put("mail.smtp.auth","true");
                props.put("mail.smtp.starttls.enable","true");
                props.put("mail.smtp.host","smtp.gmail.com");
                props.put("mail.smtp.port","587");
                Session session = Session.getInstance(props, new javax.mail.Authenticator(){
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userName, passWord);
                    }
                });
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(userName));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(editDiseaseName.getText().toString()));
                    message.setSubject("Sending email without opening gmail apps");
                    message.setText(messageToSend);
                    Transport.send(message);
                    Toast.makeText(getApplicationContext(), "Report sent successfully.", Toast.LENGTH_LONG).show();
                }
                catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diagnosis = binding.diseaseName.getText().toString();
                String fieldType = binding.fieldType.getText().toString();
                String location = binding.locationArea.getText().toString();
                String treatment = binding.treatment.getText().toString();
                String comments = binding.comments.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, diagnosis);
                intent.putExtra(Intent.EXTRA_TEXT, diagnosis + "Diagnosis" + fieldType + "" + location + "Comments" + treatment + "" + comments);

                //if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                //} else {
                //    Toast.makeText(WriteReport.this, "No app is installed!", Toast.LENGTH_SHORT).show();
                //}

            }
        }); */

    }
    /*private void InsertData() {
        String diagnosisName = editDiseaseName.getText().toString().trim();
        String fieldType = editFieldType.getText().toString().trim();
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
    } */


}