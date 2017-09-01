package com.example.sarthak.notes.activities;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.sarthak.notes.fragments.TakeChecklistRemindersFragment;
import com.example.sarthak.notes.fragments.TakeChecklistsFragment;
import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.utils.Constants;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.fragments.TakeNoteRemindersFragment;
import com.example.sarthak.notes.fragments.TakeNotesFragment;
import com.example.sarthak.notes.utils.SetImageListener;

import java.io.Serializable;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {

    Bundle dataBundle = new Bundle();

    boolean isFABOpen = false;

    int notesPosition;
    Object notesData;

    String notesType;

    private FloatingActionButton fabAddCity,fabCamera,fabGallery;

    public BackButtonListener backButtonListener;
    public SetImageListener setImageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // set up toolbar
        setUpToolbar();

        // retrieve a set of values from HomeScreenActivity/NotesFragment/RemindersFragment
        // notesType is the type of note, viz, 'Notes' or 'Reminders'
        // notesPosition is the position of note in the arraylist for existing Note
        // notesData is the data retrieved from firebase database for existing Note
        notesType = getIntent().getStringExtra(Constants.INTENT_PASS_NOTES_TYPE);
        notesPosition = getIntent().getIntExtra(Constants.INTENT_PASS_POSITION, 0);
        notesData = getIntent().getSerializableExtra(Constants.INTENT_PASS_SERIALIZABLE_OBJECT);

        // set up view components
        setUpView();

        // launch fragment view
        launchFragment();

        //------------------------------------------------------------------------------------------
        // onClick listeners for floating buttons
        //------------------------------------------------------------------------------------------
        fabAddCity.setOnClickListener(this);
        fabCamera.setOnClickListener(this);
        fabGallery.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if(!isFABOpen) {

            super.onBackPressed();
            // callback for back button pressed from respective fragment
            backButtonListener.backButtonPressed();
        }

        else {
            // call closeFABMenu() to hide floating action buttons, if visible
            closeFABMenu();
        }
    }

    //----------------------------------------------------------------------------------------------
    // Callback for action bar back button
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home :

                super.onBackPressed();
                // callback for back button pressed from respective fragment
                backButtonListener.backButtonPressed();
                break;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------
    // fab onClick listener's callback
    //----------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fabAddCity :

                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
                break;

            case R.id.fabCamera :

                // launch camera intent
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, Constants.CAMERA_REQUEST);
                break;

            case R.id.fabGallery :

                // launch implicit intent to access gallery
                // 'image/*' specifies images of all types
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, getString(R.string.gallery_dialog_title)), Constants.PICK_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Constants.PICK_IMAGE && null != data) {

                // onClick listener for setting image
                // callback in respective fragment in which the image is to be set
                setImageListener.setImage(data.getData());

            } else if (requestCode == Constants.CAMERA_REQUEST) {

                // onClick listener for setting image
                // callback in respective fragment in which the image is to be set
                setImageListener.setImage(data.getData());
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // floating action button animations
    //----------------------------------------------------------------------------------------------
    private void showFABMenu(){
        isFABOpen=true;
        fabCamera.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabGallery.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabCamera.animate().translationY(0);
        fabGallery.animate().translationY(0);
    }

    /**
     * Set up toolbar
     */
    private void setUpToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.notes_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initialise view components
     */
    private void setUpView() {

        fabAddCity = (FloatingActionButton) findViewById(R.id.fabAddCity);
        fabCamera = (FloatingActionButton) findViewById(R.id.fabCamera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fabGallery);
    }

    /**
     * Launch fragment based on the value of 'notesType' retrieved from HomeScreenActivity
     *
     * Pass 'notesPosition' and 'notesData' to the fragment.
     *
     * notesPosition identifies the position of the existing Note in the recyclerView to view
     * its contents or edit them.
     * notesData contains the data of that particular Note.
     */
    private void launchFragment() {

        switch (notesType) {

            case Constants.INTENT_PASS_NOTES: {

                Fragment takeNotesFragment = new TakeNotesFragment();
                // pass data to fragment via bundle
                dataBundle.putInt(Constants.INTENT_PASS_POSITION, notesPosition);
                dataBundle.putSerializable(Constants.INTENT_PASS_SERIALIZABLE_OBJECT, (Serializable) notesData);
                takeNotesFragment.setArguments(dataBundle);

                // launch fragment
                FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                notesFragmentTransaction.replace(R.id.notes_frame, takeNotesFragment);
                notesFragmentTransaction.commit();
                break;
            }
            case Constants.INTENT_PASS_NOTE_REMINDERS: {

                Fragment takeNoteRemindersFragment = new TakeNoteRemindersFragment();
                // pass data to fragment via bundle
                dataBundle.putInt(Constants.INTENT_PASS_POSITION, notesPosition);
                dataBundle.putSerializable(Constants.INTENT_PASS_SERIALIZABLE_OBJECT, (Serializable) notesData);
                takeNoteRemindersFragment.setArguments(dataBundle);

                // launch fragment
                FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                notesFragmentTransaction.replace(R.id.notes_frame, takeNoteRemindersFragment);
                notesFragmentTransaction.commit();
                break;
            }
            case Constants.INTENT_PASS_CHECKLISTS: {

                Fragment takeChecklistsFragment = new TakeChecklistsFragment();
                // pass data to fragment via bundle
                dataBundle.putInt(Constants.INTENT_PASS_POSITION, notesPosition);
                dataBundle.putSerializable(Constants.INTENT_PASS_SERIALIZABLE_OBJECT, (Serializable) notesData);
                takeChecklistsFragment.setArguments(dataBundle);

                // launch fragment
                FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                notesFragmentTransaction.replace(R.id.notes_frame, takeChecklistsFragment);
                notesFragmentTransaction.commit();
                break;
            }
            case Constants.INTENT_PASS_CHECKLIST_REMINDERS: {

                Fragment takeChecklistRemindersFragment = new TakeChecklistRemindersFragment();
                // pass data to fragment via bundle
                dataBundle.putInt(Constants.INTENT_PASS_POSITION, notesPosition);
                dataBundle.putSerializable(Constants.INTENT_PASS_SERIALIZABLE_OBJECT, (Serializable) notesData);
                takeChecklistRemindersFragment.setArguments(dataBundle);

                // launch fragment
                FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                notesFragmentTransaction.replace(R.id.notes_frame, takeChecklistRemindersFragment);
                notesFragmentTransaction.commit();
                break;
            }
        }
    }
}
