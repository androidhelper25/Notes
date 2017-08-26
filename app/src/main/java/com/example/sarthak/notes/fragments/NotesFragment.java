package com.example.sarthak.notes.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

        // Set title bar
        ((HomeScreenActivity) getActivity()).getSupportActionBar().setTitle(R.string.notes);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

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

    //recycler view on click
    @Override
    public void onClick(View view, int position) {

        Intent notesIntent = new Intent(getActivity(), NotesActivity.class);
        notesIntent.putExtra("position", position + 1);

        if (typeOfNote.get(position).equals("Notes")) {

            notesIntent.putExtra("type", Constants.INTENT_PASS_NOTES);
            notesIntent.putExtra("notes", (Notes) notesList.get(position));
        }
        else if (typeOfNote.get(position).equals("Checklists")) {

            notesIntent.putExtra("type", Constants.INTENT_PASS_CHECKLISTS);
            notesIntent.putExtra("notes", (Checklists) notesList.get(position));
        }

        startActivity(notesIntent);
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    /**
     * Set up progress dialog
     */
    private void setUpProgressDialog() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void readNotesFromFirebase() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    readData();
                } else {

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Read firebase database and store values of news articles as model 'Item'
     * to newsItem
     *
     * news headline, date and imageUrl are stored in newsListData to be displayed in
     * news list recycler view.
     */
    public void readData() {

        FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(getActivity());
        String currentUser = firebaseAuthorisation.getCurrentUser();

        DatabaseReference notesDatabase;
        notesDatabase = mDatabase.child(currentUser).child("Notes");

        if (notesDatabase != null) {

            notesDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // clear notesList to remove any redundant data
                    notesList.clear();
                    typeOfNote.clear();

                    for ( DataSnapshot userDataSnapshot : dataSnapshot.getChildren() ) {

                        if (userDataSnapshot != null) {
                            // add values fetched from firebase database to 'Item' newsItem
                            if (userDataSnapshot.getValue(Notes.class).getNotesBody() != null) {

                                notesList.add(userDataSnapshot.getValue(Notes.class));
                                typeOfNote.add("Notes");
                            } else {

                                notesList.add(userDataSnapshot.getValue(Checklists.class));
                                typeOfNote.add("Checklists");
                            }
                            // update recycler view adapter
                            notesRecyclerAdapter.notifyDataSetChanged();
                            // dismiss progress dialog
                            progressDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    notesRecyclerAdapter.notifyDataSetChanged();
                }
            });
        } else {

            progressDialog.dismiss();
        }
    }
}
