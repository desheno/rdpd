package com.example.ricetreatment;

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

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private EditText editEmail, editPassword;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView register = (TextView) findViewById(R.id.txtRegister);
        Button noAccount = (Button) findViewById(R.id.btnNoAccount);
        Button logIn = findViewById(R.id.btnLoginNow);
        logIn.setOnClickListener(this);
        register.setOnClickListener(view -> RegisterActivity());
        noAccount.setOnClickListener(view -> NoAccountActivity());

        editEmail = findViewById(R.id.txtEmail);
        editPassword = findViewById(R.id.txtPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtRegister) {
            startActivity(new Intent(this, Register.class));
        }
        if (v.getId() == R.id.btnLoginNow) {
            userLogin();
        }

    }

    private void userLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(editEmail.getText().toString().equals("adminricky@gmail.com")) {
                    startActivity(new Intent(Login.this, AdminMenu.class));
                }
                else {
                    startActivity(new Intent(Login.this, Dashboard.class));
                }
            }
            else {
                Toast.makeText(Login.this, "Failed to login! Please check credentials!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void RegisterActivity() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
    public void NoAccountActivity() {
        Intent intent = new Intent(Login.this, NoAccount.class);
        startActivity(intent);
    }
}