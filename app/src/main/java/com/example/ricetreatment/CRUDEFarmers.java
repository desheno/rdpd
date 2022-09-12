package com.example.ricetreatment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CRUDEFarmers {
    private DatabaseReference databaseReference;
    public CRUDEFarmers()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Farmers.class.getSimpleName());
    }
    public Task<Void> add(Farmers far)
    {
        return databaseReference.push().setValue(far);
    }
    public Task<Void> update(String key, HashMap<String , Object> hashMap) { return databaseReference.child(key).updateChildren(hashMap); }
    public Task<Void> remove(String key) { return databaseReference.child(key).removeValue(); }
}
