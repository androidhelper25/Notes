package com.example.sarthak.notes.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarthak.notes.activities.HomeScreenActivity;
import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.models.Checklists;
import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.utils.Constants;
import com.example.sarthak.notes.utils.NotesRecyclerViewItemClickListener;
import com.example.sarthak.notes.adapters.NotesRecyclerAdapter;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotesFragment extends Fragment implements NotesRecyclerViewItemClickListener {

    ArrayList<Object> notesList = new ArrayList<>();
    ArrayList<String> typeOfNote = new ArrayList<>();

    private ProgressDialog progressDialog;

    private NotesRecyclerAdapter notesRecyclerAdapter;

    DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        // set title bar
        ((HomeScreenActivity) getActivity()).getSupportActionBar().setTitle(R.string.notes);

        // set up an instance of firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_REFERENCE);

        // set up progress dialog
        setUpProgressDialog();

        readNotesFromFirebase();

        RecyclerView mNotesList = (RecyclerView) view.findViewById(R.id.notesList);
        notesRecyclerAdapter = new NotesRecyclerAdapter(getActivity(), notesList, typeOfNote);
        notesRecyclerAdapter.setOnRecyclerViewItemClickListener(this);

        // set grid layout with 2 columns
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mNotesList.setLayoutManager(mLayoutManager);
        mNotesList.setItemAnimator(new DefaultItemAnimator());
        mNotesList.setAdapter(notesRecyclerAdapter);

        return view;
    }

    //----------------------------------------------------------------------------------------------
    // Callback to recyclerView item click from NotesRecyclerAdapter
    //----------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view, int position) {

        // launch Notes Activity
        Intent notesIntent = new Intent(getActivity(), NotesActivity.class);
        notesIntent.putExtra(Constants.INTENT_PASS_POSITION, position + 1);

        // launch 'TakeNotes' or 'TakeChecklists' fragment based on value of notesType
        if (typeOfNote.get(position).equals(Constants.TYPE_NOTES)) {

            notesIntent.putExtra(Constants.INTENT_PASS_NOTES_TYPE, Constants.INTENT_PASS_NOTES);
            notesIntent.putExtra(Constants.INTENT_PASS_SERIALIZABLE_OBJECT, (Notes) notesList.get(position));
        }
        else if (typeOfNote.get(position).equals(Constants.TYPE_CHECKLISTS)) {

            notesIntent.putExtra(Constants.INTENT_PASS_NOTES_TYPE, Constants.INTENT_PASS_CHECKLISTS);
            notesIntent.putExtra(Constants.INTENT_PASS_SERIALIZABLE_OBJECT, (Checklists) notesList.get(position));
        }

        startActivity(notesIntent);
    }

    @Override
    public void onLongClick(View view, int position) {

        removeNotesFromList(position);
    }

    /**
     * Set up progress dialog
     */
    private void setUpProgressDialog() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.fetch_data_dialog_message));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    /**
     * Read firebase database and store values of notes as an Object arraylist.
     *
     * Since notesBody is a key that will be specified only for Notes and not for Checklists,
     * a check for the same is made and data is added to 'notesList' as 'Notes' if notesBody
     * is not null and as 'Checklists' if it is null.
     *
     * To maintain a track of the type of note that is added to notesList, a string value
     * specifying the type of note is added to 'typeOfNote'.
     */
    public void readNotesFromFirebase() {

        FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(getActivity());
        String currentUser = firebaseAuthorisation.getCurrentUser();

        DatabaseReference notesDatabase;
        notesDatabase = mDatabase.child(currentUser).child(Constants.TYPE_NOTES);

        if (notesDatabase != null) {

            notesDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // clear notesList to remove any redundant data
                    notesList.clear();
                    typeOfNote.clear();

                    for ( DataSnapshot userDataSnapshot : dataSnapshot.getChildren() ) {

                        if (userDataSnapshot != null) {

                            if (userDataSnapshot.getValue(Notes.class).getNotesBody() != null) {
                                // add values fetched from firebase database to 'Notes' notesList
                                notesList.add(userDataSnapshot.getValue(Notes.class));
                                typeOfNote.add(Constants.TYPE_NOTES);
                            } else {
                                // add values fetched from firebase database to 'Checklists' notesList
                                notesList.add(userDataSnapshot.getValue(Checklists.class));
                                typeOfNote.add(Constants.TYPE_CHECKLISTS);
                            }
                            // update recycler view adapter
                            notesRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    // dismiss progress dialog
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // update recycler view adapter
                    notesRecyclerAdapter.notifyDataSetChanged();
                }
            });
        } else {
            // dismiss progress dialog
            progressDialog.dismiss();
        }
    }

    /**
     * Creates an alert dialog to confirm user to remove Note.
     *
     * @param position is the index of the Note in the recyclerView
     */
    private void removeNotesFromList(final int position) {

        // setup alert dialog
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.delete_note_alert_message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        removeNotes(position);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void removeNotes(int position) {

        final int notePosition = position + 1;

        // get firebase current user
        String currentUser = new FirebaseAuthorisation(getActivity()).getCurrentUser();

        DatabaseReference removeDataReference = mDatabase.child(currentUser).child(Constants.TYPE_NOTES)
                .child(Constants.TYPE_NOTES + "_0" + String.valueOf(notePosition));
        removeDataReference.removeValue();

        //-------------------------------------------------------------------------------------
        // update index of news items following deleted news item
        //-------------------------------------------------------------------------------------
        // decrease index of following news items by 1.
        for (int i = notePosition + 1 ; i <= notesList.size() ; i++) {

            DatabaseReference notesReference = mDatabase.child(currentUser)
                    .child(Constants.TYPE_NOTES).child(Constants.TYPE_NOTES + "_0" + String.valueOf(i - 1));

            Object notesItem = notesList.get(i - 1);

            notesReference.setValue(notesItem);
        }

        // remove last item in firebase database as it has been copied to its previous location
        DatabaseReference removeDatabaseFinalValueReference = mDatabase.child(currentUser)
                .child(Constants.TYPE_NOTES).child(Constants.TYPE_NOTES + "_0" + String.valueOf(notesList.size()));
        removeDatabaseFinalValueReference.removeValue();
    }
}
