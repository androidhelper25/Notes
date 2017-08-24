package com.example.sarthak.notes.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.sarthak.notes.fragments.TakeChecklistRemindersFragment;
import com.example.sarthak.notes.fragments.TakeChecklistsFragment;
import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.utils.Constants;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.fragments.TakeNoteRemindersFragment;
import com.example.sarthak.notes.fragments.TakeNotesFragment;

public class NotesActivity extends AppCompatActivity {

    Bundle dataBundle = new Bundle();

    int notesPosition;

    String notesType;

    public BackButtonListener backButtonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.notes_toolbar);
        setSupportActionBar(toolbar);

        notesType = getIntent().getStringExtra("type");
        notesPosition = getIntent().getIntExtra("position", 0);

        launchFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        configureBackButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home :

                onBackPressed();
                break;
        }
        return true;
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

    private void configureBackButton() {

        switch (notesType) {
            case Constants.INTENT_PASS_NOTES:

                backButtonListener.notesBackButtonPressed();
                break;

            case Constants.INTENT_PASS_NOTE_REMINDERS:

                backButtonListener.noteRemindersBackButtonPressed();
                break;

            case Constants.INTENT_PASS_CHECKLISTS:

                backButtonListener.checklistsBackButtonPressed();
                break;

            case Constants.INTENT_PASS_CHECKLIST_REMINDERS:

                backButtonListener.checklistRemindersBackButtonPressed();
                break;
        }
    }
}
