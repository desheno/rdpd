package com.example.ricetreatment;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class NetworkOffline extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
