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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText editFullName, editUserName, editAddress, editContact, editEmail, editPassword;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editFullName = (EditText) findViewById(R.id.txtName);
        editUserName = (EditText) findViewById(R.id.txtUserName);
        editAddress = (EditText)  findViewById(R.id.txtAddress);
        editContact= (EditText)  findViewById(R.id.txtContact);
        editEmail= (EditText) findViewById(R.id.txtEmail);
        editPassword = (EditText)  findViewById(R.id.txtPassword);
        Button register = findViewById(R.id.btnRegister);
        ImageView imgBack = findViewById(R.id.imgBackLogin);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("Users");

                registerUser();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.fab_close);
            }
        });
    }

    private Boolean validateName() {
        String val = editFullName.getText().toString();
        if (val.isEmpty()) {
            editFullName.setError("Name cannot be empty");
            return false;
        }
        else {
            editFullName.setError(null);
            return true;
        }
    }

    private Boolean validateUName() {
        String val = editUserName.getText().toString();
        if (val.isEmpty()) {
            editUserName.setError("Username cannot be empty");
            return false;
        }
        else {
            editUserName.setError(null);
            return true;
        }
    }

    private Boolean validateAddress() {
        String val = editAddress.getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            editAddress.setError("Address cannot be empty");
            return false;
        }
        else if(val.matches(emailPattern)) {
            editAddress.setError("Address cannot be empty");
            return false;
        }
        else {
            editAddress.setError(null);
            return true;
        }
    }

    private Boolean validateContact() {
        String val = editContact.getText().toString();
        if (val.isEmpty()) {
            editContact.setError("Contact number cannot be empty");
            return false;
        }
        else {
            editContact.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = editEmail.getText().toString();
        if (val.isEmpty()) {
            editEmail.setError("Name cannot be empty");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            editEmail.setError("Provide a valid email address");
            return false;
        }
        else {
            editFullName.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = editPassword.getText().toString();
        String passwordVal = ".{4,}";
        if (val.isEmpty()) {
            editPassword.setError("Password cannot be empty");
            return false;
        }
        else if (!val.matches(passwordVal)) {
            editPassword.setError("Password must be atleast 5 characters");
            return false;
        }
        else {
            editPassword.setError(null);
            return true;
        }
    }

    public void registerUser() {
        if (!validateName() | !validateUName() | !validateAddress() | !validateContact() | !validateEmail() | !validatePassword()) {
            return;
        }

        String name = editFullName.getText().toString();
        String username = editUserName.getText().toString();
        String address = editAddress.getText().toString();
        String contact = editContact.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        Users users = new Users(name, username, address, contact, email, password);

        reference.child(username).setValue(users);

        Toast.makeText(Register.this, "You have successfully registered your account.", Toast.LENGTH_SHORT).show();
        editUserName.setText("");
        editFullName.setText("");
        editAddress.setText("");
        editContact.setText("");
        editEmail.setText("");
        editPassword.setText("");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_in_left);
    }
}