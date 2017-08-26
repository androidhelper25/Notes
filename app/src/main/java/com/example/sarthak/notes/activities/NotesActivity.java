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

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {

    Bundle dataBundle = new Bundle();

    boolean isFABOpen = false;

    int notesPosition;

    String notesType;

    private FloatingActionButton fabAddCity,fabCamera,fabGallery;

    public BackButtonListener backButtonListener;
    public SetImageListener setImageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        setUpToolbar();

        notesType = getIntent().getStringExtra("type");
        notesPosition = getIntent().getIntExtra("position", 0);

        setUpView();

        launchFragment();

        //------------------------------------------------------------------------------------
        // onClick listeners for Floating Buttons
        //------------------------------------------------------------------------------------
        fabAddCity.setOnClickListener(this);
        fabCamera.setOnClickListener(this);
        fabGallery.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if(!isFABOpen){

            super.onBackPressed();
            backButtonListener.backButtonPressed();
        }

        // call closeFABMenu() to hide floating action buttons, if visible.
        else{
            closeFABMenu();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home :

                super.onBackPressed();
                backButtonListener.backButtonPressed();
                break;
        }
        return true;
    }

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

                if (takePicture.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePicture, Constants.CAMERA_REQUEST);
                }
                break;

            case R.id.fabGallery :

                // launch implicit intent to access gallery
                // 'image/*' specifies images of all types
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Constants.PICK_IMAGE && null != data) {

                setImageListener.setImage(data.getData());

            } else if (requestCode == Constants.CAMERA_REQUEST) {

                setImageListener.setImage(data.getData());
            }
        }
    }

    //-------------------------------------------------------------------------
    // floating action button animations
    //-------------------------------------------------------------------------
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

    private void setUpToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.notes_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpView() {

        fabAddCity = (FloatingActionButton) findViewById(R.id.fabAddCity);
        fabCamera = (FloatingActionButton) findViewById(R.id.fabCamera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fabGallery);
    }

    private void launchFragment() {

        switch (notesType) {
            case Constants.INTENT_PASS_NOTES: {

                Fragment takeNotesFragment = new TakeNotesFragment();
                dataBundle.putInt("position", notesPosition);
                dataBundle.putSerializable("notes", getIntent().getSerializableExtra("notes"));
                takeNotesFragment.setArguments(dataBundle);

                FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                notesFragmentTransaction.replace(R.id.notes_frame, takeNotesFragment);
                notesFragmentTransaction.commit();
                break;
            }
            case Constants.INTENT_PASS_NOTE_REMINDERS: {

                Fragment takeNoteRemindersFragment = new TakeNoteRemindersFragment();
                dataBundle.putInt("position", notesPosition);
                dataBundle.putSerializable("notes", getIntent().getSerializableExtra("notes"));
                takeNoteRemindersFragment.setArguments(dataBundle);

                FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                notesFragmentTransaction.replace(R.id.notes_frame, takeNoteRemindersFragment);
                notesFragmentTransaction.commit();
                break;
            }
            case Constants.INTENT_PASS_CHECKLISTS: {

                Fragment takeChecklistsFragment = new TakeChecklistsFragment();
                dataBundle.putInt("position", notesPosition);
                dataBundle.putSerializable("notes", getIntent().getSerializableExtra("notes"));
                takeChecklistsFragment.setArguments(dataBundle);

                FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                notesFragmentTransaction.replace(R.id.notes_frame, takeChecklistsFragment);
                notesFragmentTransaction.commit();
                break;
            }
            case Constants.INTENT_PASS_CHECKLIST_REMINDERS: {

                Fragment takeChecklistRemindersFragment = new TakeChecklistRemindersFragment();
                dataBundle.putInt("position", notesPosition);
                dataBundle.putSerializable("notes", getIntent().getSerializableExtra("notes"));
                takeChecklistRemindersFragment.setArguments(dataBundle);

                FragmentTransaction notesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                notesFragmentTransaction.replace(R.id.notes_frame, takeChecklistRemindersFragment);
                notesFragmentTransaction.commit();
                break;
            }
        }
    }
}
