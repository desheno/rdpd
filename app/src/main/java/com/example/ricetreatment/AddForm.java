package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class AddForm extends AppCompatActivity {

    TextView diseaseName, diseaseDescription, whereOccurs, howIdentify, treatmentPlan;
    ImageView diseaseImage, uploadBtn;
    RelativeLayout relativeLayout;
    Button btnAdd;
    Uri ImageUrl;

    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_form);

        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        diseaseName = findViewById(R.id.diseaseName);
        diseaseDescription = findViewById(R.id.diseaseDescription);
        whereOccurs = findViewById(R.id.whereOccurs);
        howIdentify = findViewById(R.id.howIdentify);
        treatmentPlan = findViewById(R.id.treatmentPlan);
        diseaseImage = findViewById(R.id.diseaseImage);
        uploadBtn = findViewById(R.id.uploadBtn);
        btnAdd = findViewById(R.id.btnAdd);

        relativeLayout = findViewById(R.id.relative);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
                relativeLayout.setVisibility(View.VISIBLE);
                uploadBtn.setVisibility(View.GONE);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StorageReference reference = firebaseStorage.getReference().child("Disease")
                        .child(System.currentTimeMillis()+"");

                reference.putFile(ImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DiseaseModel model = new DiseaseModel();
                                model.setDiseaseImage(uri.toString());

                                model.setDiseaseName(diseaseName.getText().toString());
                                model.setDiseaseDescription(diseaseDescription.getText().toString());
                                model.setWhereOccurs(whereOccurs.getText().toString());
                                model.setHowIdentify(howIdentify.getText().toString());
                                model.setTreatmentPlan(treatmentPlan.getText().toString());

                                database.getReference().child("Disease").push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AddForm.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                                                diseaseName.setText("Name of Disease/Pest");
                                                diseaseDescription.setText("What it does");
                                                whereOccurs.setText("Where it occurs");
                                                howIdentify.setText("How to identify:");
                                                treatmentPlan.setText("Treatment Plan");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddForm.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });

    }
    private void UploadImage() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 101);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(AddForm.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            ImageUrl = data.getData();
            diseaseImage.setImageURI(ImageUrl);
        }
    }
}