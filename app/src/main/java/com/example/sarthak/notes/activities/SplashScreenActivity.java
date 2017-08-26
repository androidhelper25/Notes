package com.example.sarthak.notes.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class SplashScreenActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 123;

    private ProgressDialog mProgressDialog;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setUpProgressDialog();

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
                FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(SplashScreenActivity.this);
                firebaseAuth.firebaseAuthWithGoogle(account, mProgressDialog);
            } else {

                Toast.makeText(SplashScreenActivity.this, "Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setUpProgressDialog() {

        mProgressDialog = new ProgressDialog(SplashScreenActivity.this);
        mProgressDialog.setTitle("Setting up things");
        mProgressDialog.setMessage("Please wait while we setup Notes for you...");
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void launchActivity() {

        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(SplashScreenActivity.this);
        String currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // launch HomeScreenActivity
            startActivity(new Intent(SplashScreenActivity.this, HomeScreenActivity.class));
            finish();
        } else {
            // launch LoginActivity
            launchGoogleSignInIntent();
        }
    }

    private void launchGoogleSignInIntent() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
