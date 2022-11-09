package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {
    private EditText editUsername, editPassword;
    private TextInputLayout tilUser, tilPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView register = (TextView) findViewById(R.id.txtRegister);
        Button noAccount = (Button) findViewById(R.id.btnNoAccount);
        Button logIn = findViewById(R.id.btnLoginNow);

        editUsername = findViewById(R.id.txtEmail);
        editPassword = findViewById(R.id.txtPassword);

        tilUser = findViewById(R.id.tilUser);
        tilPassword = findViewById(R.id.tilPassword);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, NoAccount.class);
                startActivity(intent);
            }
        });

    }

    private Boolean validateEmail() {
        String val = editUsername.getText().toString();
        if (val.isEmpty()) {
            tilUser.setError("Email cannot be empty");
            return false;
        }
        else {
            editUsername.setError(null);
            tilUser.setError(null);
            editUsername.setEnabled(true);
            return true;
        }
    }
    private Boolean validatePassword() {
        String val = editPassword.getText().toString();
        if (val.isEmpty()) {
            tilPassword.setError("Password cannot be empty");
            return false;
        }
        else {
            editPassword.setError(null);
            tilPassword.setError(null);
            editPassword.setEnabled(true);
            return true;
        }
    }

    public void loginUser() {
        if (!validateEmail() | !validatePassword()) {
            isUser();
            return;
        }
        else {
            isUser();
        }
    }
    private void isUser() {
        final String userName = editUsername.getText().toString().trim();
        final String userPassword = editPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        Query checkUser = reference.orderByChild("username").equalTo(userName);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    editUsername.setError(null);
                    String passwordDB = snapshot.child(userName).child("password").getValue(String.class);
                    assert passwordDB != null;
                    if (passwordDB.equals(userPassword)) {
                        String nameDB = snapshot.child(userName).child("fullName").getValue(String.class);
                        String userDB = snapshot.child(userName).child("username").getValue(String.class);
                        String addressDB = snapshot.child(userName).child("address").getValue(String.class);
                        String contactDB = snapshot.child(userName).child("contact").getValue(String.class);
                        String emailDB = snapshot.child(userName).child("email").getValue(String.class);


                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);

                        intent.putExtra("fullName", nameDB);
                        intent.putExtra("username", userDB);
                        intent.putExtra("address", addressDB);
                        intent.putExtra("contact", contactDB);
                        intent.putExtra("email", emailDB);
                        startActivity(intent);

                    } else {
                        tilPassword.setError("Wrong password");
                        editPassword.requestFocus();
                    }
                } else {
                    tilUser.setError("No such user exist");
                    editPassword.requestFocus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}