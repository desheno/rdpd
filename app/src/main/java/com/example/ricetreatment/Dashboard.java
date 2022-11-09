package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Dashboard extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private TextView userName, fullName;
    AlertDialog.Builder builder;

    public BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        LinearLayout scan = findViewById(R.id.btnScan);
        LinearLayout diseasePest = findViewById(R.id.btnDiseasePest);
        LinearLayout reports = findViewById(R.id.btnReports);

        userName = findViewById(R.id.UName);
        fullName = findViewById(R.id.FName);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        builder = new AlertDialog.Builder(this);

        showAccountDetails();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        startActivity(new Intent(getApplicationContext(), AboutApp.class));
                        overridePendingTransition(0, 0);
                        return false;
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_logout:
                        isUser();
                        overridePendingTransition(0, 0);
                        return false;
                }
                return false;
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        scan.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, Scan.class);
            startActivity(intent);
        });

        diseasePest.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, Gallery.class);
            startActivity(intent);
        });

        reports.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, Report.class);
            startActivity(intent);
        });
    }

    private void showAccountDetails() {
        Intent intent = getIntent();
        String user_fullName = intent.getStringExtra("fullName");
        String user_username = intent.getStringExtra("username");

        userName.setText(user_username);
        fullName.setText(user_fullName);
    }

    @Override
    public void onBackPressed()
    {
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

    private void isUser() {
        final String uName = userName.getText().toString().trim();
        final String fName = fullName.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        Query checkUser = reference.orderByChild("username").equalTo(uName);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userName.setError(null);
                    String fullnameDB = snapshot.child(uName).child("fullName").getValue(String.class);
                    assert fullnameDB != null;
                    if (fullnameDB.equals(fName)) {
                        String userDB = snapshot.child(uName).child("username").getValue(String.class);
                        String addressDB = snapshot.child(uName).child("address").getValue(String.class);
                        String contactDB = snapshot.child(uName).child("contact").getValue(String.class);
                        String emailDB = snapshot.child(uName).child("email").getValue(String.class);
                        String passwordDB = snapshot.child(uName).child("password").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), Account.class);

                        intent.putExtra("fullName", fullnameDB);
                        intent.putExtra("username", userDB);
                        intent.putExtra("address", addressDB);
                        intent.putExtra("contact", contactDB);
                        intent.putExtra("email", emailDB);
                        intent.putExtra("password", passwordDB);
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}