package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricetreatment.ml.Model;
import com.example.ricetreatment.ml.ModelUnquant4;
import com.example.ricetreatment.ml.Modeltf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Scan extends AppCompatActivity {
    Button openCamera;
    Button pickImage;
    //Button manualInput;
    Button diagnose;
    ImageView imageView;
    EditText result;
    TextView holder;
    int imageSize = 224;

    RecyclerView recyclerView;
    List<DiseaseModel> recycleList;
    List<DiseaseModel> filterList = new ArrayList<>();
    DiseaseAdapter adapter;

    FirebaseDatabase firebaseDatabase;

    //int SELECT_IMAGE_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Scan or Upload");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.imageRecycler);
        recycleList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("NetworkOffline");
        reference.keepSynced(true);

        DiseaseAdapter recyclerAdapter = new DiseaseAdapter((ArrayList<DiseaseModel>) recycleList, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.suppressLayout(true);
        recyclerView.setAdapter(recyclerAdapter);

        firebaseDatabase.getReference().child("Disease").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            DiseaseModel diseaseModel = dataSnapshot.getValue(DiseaseModel.class);
                            assert diseaseModel != null;
                            recycleList.add(diseaseModel);
                }
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pickImage = findViewById(R.id.btnPickImage);
        openCamera = findViewById(R.id.btnScanUpload);
        //manualInput = findViewById(R.id.btnManualInput);
        imageView = findViewById(R.id.imageHere);
        result = findViewById(R.id.result);
        //holder = findViewById(R.id.holder);

        result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    DiseaseAdapter recyclerAdapter = new DiseaseAdapter((ArrayList<DiseaseModel>) recycleList, getApplicationContext());
                    recyclerView.setAdapter(recyclerAdapter);

                    firebaseDatabase.getReference().child("Disease").addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                DiseaseModel diseaseModel = dataSnapshot.getValue(DiseaseModel.class);
                                assert diseaseModel != null;
                                recycleList.add(diseaseModel);
                            }
                            recyclerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    Filter(s.toString());
                }
            }
        });
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, 100);
                }
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 100);

            }
        });
    }

    private void Filter(String text) {
        for (DiseaseModel disease: recycleList) {
            if (disease.getDiseaseName().equals(text)) {
                filterList.clear();
                filterList.add(disease);
            }
        }
        DiseaseAdapter recyclerAdapter = new DiseaseAdapter((ArrayList<DiseaseModel>) filterList, getApplicationContext());
        recyclerView.setAdapter(recyclerAdapter);

        firebaseDatabase.getReference().child("Disease").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DiseaseModel diseaseModel = dataSnapshot.getValue(DiseaseModel.class);
                    assert diseaseModel != null;
                    filterList.add(diseaseModel);
                }
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void classifyImage(Bitmap image) {
        try {
            ModelUnquant4 model = ModelUnquant4.newInstance(getApplicationContext());

            //creates input reference
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;

            //iterate over each pixel and extract RGB values and add individually to bytebuffer
            for (int i = 0; i < imageSize; i ++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f/ 255.f)); //255
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f/255.f)); //255
                    byteBuffer.putFloat((val & 0xFF) * (1.f/255.f)); //255
                }

            }
            inputFeature0.loadBuffer(byteBuffer);

            //runs model inference and gets result
            ModelUnquant4.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            //find the max index of the class with the biggest confidence
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Aphids", "Bacterial Leaf Blight", "Blast", "Brownspot","Crickets", "Fall Armyworm", "False Smut", "Leaf Scald", "Leaf Smuf", "Mealybug", "Nitrogen (N) Deficiency ", "Phosphorus (P) Deficiency ", "Potassium (K) Deficiency ", "Rats", "Rice Bug", "Snail", "Unknown"};
            result.setText(classes[maxPos]);
            if (result.getText().toString().equals("Unknown")) {
                recyclerView.setVisibility(View.INVISIBLE);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.suppressLayout(true);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.suppressLayout(true);
            }

            //holder.setText(classes[maxPos]);
            //holder.setVisibility(View.VISIBLE);
            //String s = "";
            //s += String.format("%s: %.1f%%\n", "Confidence",confidences[maxPos]*100);

            //confidence.setText(s);

            //release model resource if no longer used
            model.close();
        } catch (IOException ignored) {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            } else {
                assert data != null;
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}