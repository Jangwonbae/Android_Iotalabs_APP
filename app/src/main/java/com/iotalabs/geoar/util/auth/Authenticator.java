package com.iotalabs.geoar.util.auth;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.atomic.AtomicInteger;

public class Authenticator {
    private FirebaseAuth mAuth;
    public void authFireBase(){
        //auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signInAnonymously:success");
                    }
                })
                .addOnFailureListener(task -> Log.d("TAG", "signInAnonymously:success"));

    }
}
