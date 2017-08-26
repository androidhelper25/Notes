package com.example.sarthak.notes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.example.sarthak.notes.fragments.RemindersFragment;
import com.example.sarthak.notes.utils.Constants;
import com.example.sarthak.notes.models.User;
import com.example.sarthak.notes.fragments.NotesFragment;
import com.example.sarthak.notes.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    String noteType = "Notes";

    NavigationView navigationView;

    CircleImageView profileImage;
    TextView profileName, profileEmail;

    ImageButton buttonChecklist, buttonImage;
    TextView takeNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialise all view components
        setUpView();

        // launch default fragment as 'Notes' category
        launchDefaultFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // update navigation view with Google account details
        setUpNavigationView();

        //------------------------------------------------------------------------------------
        // on click listeners
        //------------------------------------------------------------------------------------
        takeNote.setOnClickListener(this);
        buttonChecklist.setOnClickListener(this);
        buttonImage.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {

        Intent notesIntent = new Intent(HomeScreenActivity.this, NotesActivity.class);

        switch (view.getId()) {

            case R.id.textViewNotes :

                if (noteType.equals("Notes")) {

                    notesIntent.putExtra("type", Constants.INTENT_PASS_NOTES);
                } else if (noteType.equals("Reminders")) {

                    notesIntent.putExtra("type", Constants.INTENT_PASS_NOTE_REMINDERS);
                }
                startActivity(notesIntent);
                break;

            case R.id.button_checkList :

                if (noteType.equals("Notes")) {

                    notesIntent.putExtra("type", Constants.INTENT_PASS_CHECKLISTS);
                } else if (noteType.equals("Reminders")) {

                    notesIntent.putExtra("type", Constants.INTENT_PASS_CHECKLIST_REMINDERS);
                }
                startActivity(notesIntent);
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_notes :

                noteType = "Notes";
                launchDefaultFragment();
                break;
            case R.id.nav_reminders :

                noteType = "Reminders";
                launchReminderFragment();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpView() {

        takeNote = (TextView) findViewById(R.id.textViewNotes);
        buttonChecklist = (ImageButton) findViewById(R.id.button_checkList);
        buttonImage = (ImageButton) findViewById(R.id.buttonMessage);
    }

    public void launchDefaultFragment() {

        Fragment notesFragment = new NotesFragment();

        FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
        notesFragmentTransaction.replace(R.id.content_frame, notesFragment);
        notesFragmentTransaction.commit();
    }

    private void launchReminderFragment() {

        Fragment remindersFragment = new RemindersFragment();

        FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
        notesFragmentTransaction.replace(R.id.content_frame, remindersFragment);
        notesFragmentTransaction.commit();
    }

    private void setUpNavigationView() {

        View header = navigationView.getHeaderView(0);

        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(HomeScreenActivity.this);
        String currentUser = firebaseAuth.getCurrentUser();

        profileImage = (CircleImageView) header.findViewById(R.id.nav_profile_image);
        profileName = (TextView) header.findViewById(R.id.nav_profile_name);
        profileEmail = (TextView) header.findViewById(R.id.nav_profile_email);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                Picasso.with(HomeScreenActivity.this).load(user.getProfileImage()).into(profileImage);
                profileName.setText(user.getProfileName());
                profileEmail.setText(user.getProfileEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
