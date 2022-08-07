package com.example.okareappv2.familyUI;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ElderSignal {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    public ElderSignal(){
//        reference.child("older_side/datetime").setValue(
//                formatter.format(new Date(System.currentTimeMillis()))
//        );
    }

    public ElderSignal(String path) {
        DatabaseReference reference = database.getReference(path);
        this.reference = reference;
    }
}
