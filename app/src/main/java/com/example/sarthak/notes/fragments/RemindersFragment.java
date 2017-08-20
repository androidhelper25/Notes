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

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.adapters.RemindersRecyclerAdapter;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.example.sarthak.notes.models.NoteReminders;
import com.example.sarthak.notes.utils.RecyclerViewItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RemindersFragment extends Fragment implements RecyclerViewItemClickListener {

    ArrayList<NoteReminders> remindersList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private RecyclerView mRemindersList;
    private RemindersRecyclerAdapter remindersRecyclerAdapter;

    DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(getActivity());
        String currentUser = firebaseAuthorisation.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("Reminders");

        mRemindersList = (RecyclerView) view.findViewById(R.id.remindersList);

        setUpProgressDialog();

        readRemindersFromFirebase();

        remindersRecyclerAdapter = new RemindersRecyclerAdapter(getActivity(), remindersList);
        remindersRecyclerAdapter.setOnRecyclerViewItemClickListener(this);

        // set grid layout with 2 columns
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRemindersList.setLayoutManager(mLayoutManager);
        mRemindersList.setItemAnimator(new DefaultItemAnimator());
        mRemindersList.setAdapter(remindersRecyclerAdapter);

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

    private void readRemindersFromFirebase() {

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
                    remindersList.clear();

                    for ( DataSnapshot userDataSnapshot : dataSnapshot.getChildren() ) {

                        if (userDataSnapshot != null) {
                            // add values fetched from firebase database to 'Item' newsItem
                            remindersList.add(userDataSnapshot.getValue(NoteReminders.class));

                            // update recycler view adapter
                            remindersRecyclerAdapter.notifyDataSetChanged();
                            // dismiss progress dialog
                            progressDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    remindersRecyclerAdapter.notifyDataSetChanged();
                }
            });
        } else {

            progressDialog.dismiss();
        }
    }
}
