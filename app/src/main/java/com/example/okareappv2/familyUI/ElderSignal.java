package com.example.okareappv2.familyUI;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ElderSignal {
    FirebaseDatabase database;
    DatabaseReference reference;

    public ElderSignal(){
    }

    public ElderSignal(String path) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(path);
    }
}
