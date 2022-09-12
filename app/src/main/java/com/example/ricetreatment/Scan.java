package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricetreatment.ml.Model;
import com.example.ricetreatment.ml.Modeltf;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Scan extends AppCompatActivity {
    Button openCamera;
    Button pickImage;
    //Button manualInput;
    Button diagnose;
    ImageView imageView;
    TextView result, confidence;
    int imageSize = 224;

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

        pickImage = findViewById(R.id.btnPickImage);
        openCamera = findViewById(R.id.btnScanUpload);
        diagnose = findViewById(R.id.btnDiagnose);
        //manualInput = findViewById(R.id.btnManualInput);
        imageView = findViewById(R.id.imageHere);
        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);

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

        diagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Scan.this, Gallery.class));
            }
        });
        /*manualInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scan.this, SymptomsInput.class);
                startActivity(intent);
            }
        });*/

        /*openCamera.setOnClickListener(v -> {
            try {
                Intent intent1 = new Intent();
                intent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
            }
        });*/
    }

    public void classifyImage(Bitmap image) {
        try {
            Modeltf model = Modeltf.newInstance(getApplicationContext());

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
            Modeltf.Outputs outputs = model.process(inputFeature0);
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
            String[] classes = {"Bacterialblight", "Blast", "Brownspot", "Healthy","Leaf smut", "Nitrogen(N)", "Phosphorus(P)", "Potassium(K)"};
            result.setText(classes[maxPos]);

            String s = "";
            s += String.format("%s: %.1f%%\n", "Confidence",confidences[maxPos]*100);

            confidence.setText(s);

            //release model resource if no longer used
            model.close();
        }catch (IOException e) {

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

        /*if (requestCode == 1) {
            Uri uri = data.getData();
            imageView.setImageURI(uri);
        }*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}