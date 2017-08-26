package com.example.sarthak.notes.firebasemanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.sarthak.notes.activities.HomeScreenActivity;
import com.example.sarthak.notes.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseAuthorisation {

    private Context mContext;

    private FirebaseAuth mAuth;

    // set up firebase authentication
    public FirebaseAuthorisation(Context context) {

        this.mContext = context;

        mAuth = FirebaseAuth.getInstance();
    }
    /**
     * Registers user logged in through Google account to firebase authentication
     *
     * @param account is the GoogleSignInAccount of the user
     */
    public void firebaseAuthWithGoogle(final GoogleSignInAccount account, final ProgressDialog mProgressDialog) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(((Activity) mContext), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        } else {

                            // get current user UID
                            String UID = getCurrentUser();

                            DatabaseReference mDatabase;

                            // create a database reference for the user
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

                            User user = new User(account.getPhotoUrl(), account.getDisplayName(), account.getEmail());

                            // store user details to firebase database
                            mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        // dismiss progress dialog
                                        mProgressDialog.dismiss();

                                        // launch HomeScreenActivity when user registration is complete
                                        Intent mainActivity = new Intent(mContext, HomeScreenActivity.class);
                                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        mContext.startActivity(mainActivity);

                                        // finish current activity
                                        ((Activity) mContext).finish();
                                    } else {

                                        // display error message
                                        mProgressDialog.dismiss();
                                        Toast.makeText(mContext, "LOL", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
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
