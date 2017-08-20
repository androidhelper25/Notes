package com.example.sarthak.notes.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScreenActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 123;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(SplashScreenActivity.this  /* FragmentActivity */ , this  /* OnConnectionFailedListener */ )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mProgressDialog = new ProgressDialog(SplashScreenActivity.this);
        mProgressDialog.setTitle("Setting up things");
        mProgressDialog.setMessage("Please wait while we setup Notes for you...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        launchActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {

                mProgressDialog.show();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

                Toast.makeText(SplashScreenActivity.this, "Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void launchActivity() {

        String currentUser = getCurrentUser();

        if (currentUser != null) {
            // launch HomeScreenActivity
            startActivity(new Intent(SplashScreenActivity.this, HomeScreenActivity.class));
            finish();
        } else {
            // launch LoginActivity
            launchGoogleSignInIntent();
        }
    }

    /**
     * Registers user logged in through Google account to firebase authentication
     *
     * @param account is the GoogleSignInAccount of the user
     */
    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SplashScreenActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {

                    mProgressDialog.dismiss();
                    Toast.makeText(SplashScreenActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                                Intent mainActivity = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainActivity);

                                // finish current activity
                                finish();
                            } else {

                                // display error message
                                mProgressDialog.dismiss();
                                Toast.makeText(SplashScreenActivity.this, "LOL", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void launchGoogleSignInIntent() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
