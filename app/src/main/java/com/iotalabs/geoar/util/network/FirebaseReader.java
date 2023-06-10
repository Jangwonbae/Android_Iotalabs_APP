package com.iotalabs.geoar.util.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iotalabs.geoar.data.PersonLocation;
import com.iotalabs.geoar.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseReader {
    private List<User> firebaseList;


    public FirebaseReader(){

    }
    public List<User> getFirebaseData(){

        return firebaseList;
    }
}
