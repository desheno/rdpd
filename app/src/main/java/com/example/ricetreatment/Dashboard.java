package com.example.ricetreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

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
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Dashboard extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userName;
    private String userEmail;

    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        LinearLayout scan = findViewById(R.id.btnScan);
        LinearLayout diseasePest = findViewById(R.id.btnDiseasePest);
        LinearLayout reports = findViewById(R.id.btnReports);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        startActivity(new Intent(getApplicationContext(), AboutApp.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_logout:
                        startActivity(new Intent(getApplicationContext(), Account.class));
                        overridePendingTransition(0, 0);
                        return true;
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

        /*user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userName = user.getUid();

        final TextView nameTextView = findViewById(R.id.fullName);
        //final TextView emailTextView = findViewById(R.id.fullEmail);

        reference.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if (userProfile != null) {
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;

                    nameTextView.setText(fullName);
                    //emailTextView.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Something wrong happened!", Toast.LENGTH_LONG).show();

            }
        }); */
    }
}