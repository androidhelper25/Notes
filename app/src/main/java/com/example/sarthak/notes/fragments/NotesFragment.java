package com.example.sarthak.notes.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.utils.RecyclerViewItemClickListener;
import com.example.sarthak.notes.adapters.NotesRecyclerAdapter;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotesFragment extends Fragment implements RecyclerViewItemClickListener {

    ArrayList<Notes> notesList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private RecyclerView mNotesList;
    private NotesRecyclerAdapter notesRecyclerAdapter;

    DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(getActivity());
        String currentUser = firebaseAuthorisation.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("Notes");

        mNotesList = (RecyclerView) view.findViewById(R.id.notesList);

        setUpProgressDialog();

        readNotesFromFirebase();

        notesRecyclerAdapter = new NotesRecyclerAdapter(getActivity(), notesList);
        notesRecyclerAdapter.setOnRecyclerViewItemClickListener(this);

        // set grid layout with 2 columns
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mNotesList.setLayoutManager(mLayoutManager);
        mNotesList.setItemAnimator(new DefaultItemAnimator());
        mNotesList.setAdapter(notesRecyclerAdapter);

        return view;
    }

    @Override
    public void onClick(View view, int position) {

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

        if (mDatabase != null) {

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // clear notesList to remove any redundant data
                    notesList.clear();

                    for ( DataSnapshot userDataSnapshot : dataSnapshot.getChildren() ) {

                        if (userDataSnapshot != null) {
                            // add values fetched from firebase database to 'Item' newsItem
                            notesList.add(userDataSnapshot.getValue(Notes.class));

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
