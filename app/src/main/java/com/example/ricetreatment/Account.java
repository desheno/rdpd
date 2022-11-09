package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Account extends AppCompatActivity {

    private TextView txtUser;
    private ScrollView scrollView;
    private EditText editUser, editEmail, editAddress, editContact, editPassword;
    DatabaseReference reference;
    Button btnUpdate, btnLogout, btnEdit;
    AlertDialog.Builder builder;

    String _FULLNAME, _USERNAME, _ADDRESS, _CONTACT, _EMAIL, _PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        scrollView = findViewById(R.id.scrollView);
        txtUser = findViewById(R.id.lblUser);
        editUser = findViewById(R.id.upName);
        editEmail = findViewById(R.id.upEmail);
        editAddress = findViewById(R.id.upAddress);
        editContact = findViewById(R.id.upContact);
        editPassword = findViewById(R.id.upPassword);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);
        btnEdit = findViewById(R.id.btnEdit);

        builder = new AlertDialog.Builder(this);
        showAccountDetails();
        txtUser.setVisibility(View.INVISIBLE);
        editUser.setEnabled(false);
        editEmail.setEnabled(false);
        editAddress.setEnabled(false);
        editContact.setEnabled(false);
        editContact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    scrollView.smoothScrollTo(0,editContact.getBottom()+5);
                }
            }
        });
        editPassword.setEnabled(false);
        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    scrollView.smoothScrollTo(0,editPassword.getBottom()+5);
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUser.setEnabled(true);
                editEmail.setEnabled(true);
                editAddress.setEnabled(true);
                editContact.setEnabled(true);
                editPassword.setEnabled(true);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccount();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Do you want to Logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Logout");
                alertDialog.show();
            }
        });
    }

    private void showAccountDetails() {
        Intent intent = getIntent();
        _FULLNAME = intent.getStringExtra("fullName");
        _USERNAME = intent.getStringExtra("username");
        _ADDRESS = intent.getStringExtra("address");
        _CONTACT = intent.getStringExtra("contact");
        _EMAIL = intent.getStringExtra("email");
        _PASSWORD = intent.getStringExtra("password");

        txtUser.setText(_USERNAME);
        editUser.setText(_FULLNAME);
        editAddress.setText(_ADDRESS);
        editContact.setText(_CONTACT);
        editEmail.setText(_EMAIL);
        editPassword.setText(_PASSWORD);

    }

    public void updateAccount() {
        if (isNameChanged() || isPasswordChanged() || isAddressChanged() || isContactChanged() || isEmailChanged()) {
            Toast.makeText(this, "Account has been updated", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNameChanged() {
        if (!_FULLNAME.equals(editUser.getText().toString())) {
            reference.child(_USERNAME).child("fullName").setValue(editUser.getText().toString());
            _FULLNAME = txtUser.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isAddressChanged() {
        if (!_ADDRESS.equals(editAddress.getText().toString())) {
            reference.child(_USERNAME).child("address").setValue(editAddress.getText().toString());
            _ADDRESS = editAddress.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isContactChanged() {
        if (!_CONTACT.equals(editContact.getText().toString())) {
            reference.child(_USERNAME).child("contact").setValue(editContact.getText().toString());
            _CONTACT = editContact.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isEmailChanged() {
        if (!_EMAIL.equals(editEmail.getText().toString())) {
            reference.child(_USERNAME).child("email").setValue(editEmail.getText().toString());
            _EMAIL = editEmail.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isPasswordChanged() {
        if (!_PASSWORD.equals(editPassword.getText().toString())) {
            reference.child(_USERNAME).child("password").setValue(editPassword.getText().toString());
            _PASSWORD = editPassword.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

}