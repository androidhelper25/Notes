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

    // default value of noteType to launch NotesFragment
    String noteType = Constants.TYPE_NOTES;

    // navigation view components
    CircleImageView profileImage;
    TextView profileName, profileEmail;

    // home activity view components
    ImageButton buttonChecklist;
    TextView takeNote;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up view components
        setUpView();

        // launch default fragment as 'Notes' category
        launchNotesFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // update navigation view with Google account details
        setUpNavigationView();

        //------------------------------------------------------------------------------------------
        // onClick listeners
        //------------------------------------------------------------------------------------------
        takeNote.setOnClickListener(this);
        buttonChecklist.setOnClickListener(this);
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

    //----------------------------------------------------------------------------------------------
    // onClick listener's callback
    //----------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {

        // launch Notes activity
        Intent notesIntent = new Intent(HomeScreenActivity.this, NotesActivity.class);

        // check for the value of 'noteType' and launch corresponding fragment
        switch (view.getId()) {

            case R.id.textViewNotes :

                if (noteType.equals(Constants.TYPE_NOTES)) {

                    // launch Notes fragment
                    notesIntent.putExtra(Constants.INTENT_PASS_NOTES_TYPE, Constants.INTENT_PASS_NOTES);

                } else if (noteType.equals(Constants.TYPE_REMINDERS)) {

                    // launch NoteReminders fragment
                    notesIntent.putExtra(Constants.INTENT_PASS_NOTES_TYPE, Constants.INTENT_PASS_NOTE_REMINDERS);
                }
                startActivity(notesIntent);
                break;

            case R.id.button_checkList :

                if (noteType.equals(Constants.TYPE_NOTES)) {

                    // launch Checklists fragment
                    notesIntent.putExtra(Constants.INTENT_PASS_NOTES_TYPE, Constants.INTENT_PASS_CHECKLISTS);

                } else if (noteType.equals(Constants.TYPE_REMINDERS)) {

                    // launch ChecklistReminders fragment
                    notesIntent.putExtra(Constants.INTENT_PASS_NOTES_TYPE, Constants.INTENT_PASS_CHECKLIST_REMINDERS);
                }
                startActivity(notesIntent);
                break;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Navigation view item click listener
    //----------------------------------------------------------------------------------------------
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_notes :

                // set notesType as 'Notes' to identify the currently active fragment as 'Notes'
                noteType = Constants.TYPE_NOTES;
                //launch 'Notes' fragment
                launchNotesFragment();
                break;
            case R.id.nav_reminders :

                // set notesType as 'Reminders' to identify the currently active fragment as 'Reminders'
                noteType = Constants.TYPE_REMINDERS;
                // launch 'Reminders' fragment
                launchReminderFragment();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Initialise view components
     */
    private void setUpView() {

        takeNote = (TextView) findViewById(R.id.textViewNotes);
        buttonChecklist = (ImageButton) findViewById(R.id.button_checkList);
    }

    /**
     * Launch NotesFragment in activity
     */
    public void launchNotesFragment() {

        Fragment notesFragment = new NotesFragment();

        FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
        notesFragmentTransaction.replace(R.id.content_frame, notesFragment);
        notesFragmentTransaction.commit();
    }

    /**
     * Launch RemindersFragment in activity
     */
    private void launchReminderFragment() {

        Fragment remindersFragment = new RemindersFragment();

        FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
        notesFragmentTransaction.replace(R.id.content_frame, remindersFragment);
        notesFragmentTransaction.commit();
    }

    /**
     * Set up Navigation drawer view with Google account details
     */
    private void setUpNavigationView() {

        View header = navigationView.getHeaderView(0);

        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(HomeScreenActivity.this);
        String currentUser = firebaseAuth.getCurrentUser();

        profileImage = (CircleImageView) header.findViewById(R.id.nav_profile_image);
        profileName = (TextView) header.findViewById(R.id.nav_profile_name);
        profileEmail = (TextView) header.findViewById(R.id.nav_profile_email);

        // set up an instance of firebase database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_REFERENCE).child(currentUser);

        // retrieve user details and set up in view components
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                // display data in navigation view components
                Picasso.with(HomeScreenActivity.this)
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.default_profile_picture)
                        .into(profileImage);
                profileName.setText(user.getProfileName());
                profileEmail.setText(user.getProfileEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
