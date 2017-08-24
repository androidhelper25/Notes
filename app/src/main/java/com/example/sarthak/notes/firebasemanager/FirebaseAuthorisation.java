package com.example.sarthak.notes.firebasemanager;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthorisation {

    private Context mContext;

    private FirebaseAuth mAuth;

    // set up firebase authentication
    public FirebaseAuthorisation(Context context) {

        this.mContext = context;

        mAuth = FirebaseAuth.getInstance();
    }

    // get current user UID
    public String getCurrentUser() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }
}
