package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser;
    private EditText editFullName, editAddress, editContact, editEmail, editPassword;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button) findViewById(R.id.btnRegister);
        registerUser.setOnClickListener(this);

        editFullName = (EditText) findViewById(R.id.txtName);
        editAddress = (EditText)  findViewById(R.id.txtAddress);
        editContact= (EditText)  findViewById(R.id.txtContact);
        editEmail= (EditText) findViewById(R.id.txtEmail);
        editPassword = (EditText)  findViewById(R.id.txtPassword);
        Button register = findViewById(R.id.btnRegister);
        ImageView imgBack = findViewById(R.id.imgBackLogin);
        TextView txtBack = findViewById(R.id.txtBackLogin);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.fab_close);
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.fab_close);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                registerUser();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_in_left);
    }
    private void registerUser() {
        String fullName = editFullName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String contact = editContact.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if(fullName.isEmpty()) {
            editFullName.setError("Full Name is required!");
            editFullName.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            editAddress.setError("Address is required!");
            editAddress.requestFocus();
            return;
        }

        if (contact.isEmpty()) {
            editContact.setError("Contact number is required!");
            editContact.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editEmail.setError("Email Address is required!");
            editEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please provide a valid email address!");
            editEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Password is required!");
            editPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editPassword.setError("Minimum password length should be at least 6 characters!");
            editPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Users users = new Users(fullName, address, contact, email, password);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "User has been registered successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(Register.this, "Failed to register!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(Register.this, "Failed to register!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}