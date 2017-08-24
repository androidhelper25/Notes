package com.example.sarthak.notes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.adapters.TakeChecklistsRecyclerAdapter;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.example.sarthak.notes.models.Checklists;
import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.utils.CheckListListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TakeChecklistsFragment extends Fragment implements CheckListListener, BackButtonListener, TextWatcher {

    int count, notesPosition;

    String checklistsTitle = " ";
    String checklistListenerContext = "checklists";

    private ArrayList<String> dataList = new ArrayList<>();
    private ArrayList<String> statusList = new ArrayList<>();

    Checklists checklistsData = new Checklists();

    private EditText mChecklistsTitleEt;

    private TakeChecklistsRecyclerAdapter takeChecklistsRecyclerAdapter;

    DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_checklists, container, false);

        notesPosition = getArguments().getInt("position");
        checklistsData = (Checklists) getArguments().getSerializable("notes");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        getNotesCount();

        mChecklistsTitleEt = (EditText) view.findViewById(R.id.checklistsTitle);
        mChecklistsTitleEt.addTextChangedListener(this);

        displayData();

        RecyclerView mChecklistList = (RecyclerView) view.findViewById(R.id.checklistList);

        takeChecklistsRecyclerAdapter = new TakeChecklistsRecyclerAdapter(getActivity(), dataList, statusList, this, checklistListenerContext);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mChecklistList.setLayoutManager(mLayoutManager);
        mChecklistList.setItemAnimator(new DefaultItemAnimator());
        mChecklistList.setAdapter(takeChecklistsRecyclerAdapter);

        return view;
    }

    private void displayData() {

        if (checklistsData != null) {

            mChecklistsTitleEt.setText(checklistsData.getNotesTitle());

            for (int i = 0 ; i < checklistsData.getContent().size() ; i++) {

                dataList.add(checklistsData.getContent().get("content_0" + String.valueOf(i + 1)).get("value"));
                statusList.add(checklistsData.getContent().get("content_0" + String.valueOf(i + 1)).get("status"));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((NotesActivity) context).backButtonListener = this;
    }

    @Override
    public void checklistEnterKeyPressed(String data, String status) {

        dataList.add(data);
        statusList.add(status);
        takeChecklistsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void checklistReminderEnterKeyPressed(String data, String status) {

    }

    @Override
    public void checklistCheckBoxStatus(boolean status, int position) {

        if (position < statusList.size()) {

            if (status) {
                statusList.set(position, "checked");
            } else {
                statusList.set(position, "unchecked");
            }
        }
    }

    @Override
    public void checklistReminderCheckBoxStatus(boolean b, int pos) {

    }

    @Override
    public void notesBackButtonPressed() {

    }

    @Override
    public void noteRemindersBackButtonPressed() {

    }

    @Override
    public void checklistsBackButtonPressed() {

        DatabaseReference notesDatabase;

        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(getActivity());

        if (checklistsData != null) {

            notesDatabase = mDatabase.child(firebaseAuth.getCurrentUser()).child("Notes").child("Notes_0" + String.valueOf(notesPosition));
        } else {

            int notesCount = count;

            notesDatabase = mDatabase.child(firebaseAuth.getCurrentUser()).child("Notes").child("Notes_0" + String.valueOf(notesCount + 1));
        }

        if (!(checklistsTitle.equals(" ") && dataList.isEmpty())) {

            HashMap<String, HashMap<String, String>> dataMap = new HashMap<>();

            for (int i = 0; i < dataList.size(); i++) {

                HashMap<String, String> contentMap = new HashMap<>();
                contentMap.put("value", dataList.get(i));
                contentMap.put("status", statusList.get(i));

                dataMap.put("content_0" + String.valueOf(i + 1), contentMap);

                statusList.add("unchecked");
            }

            Checklists checklists = new Checklists(checklistsTitle, dataMap);

            notesDatabase.setValue(checklists).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        if (getActivity() != null) {

                            Toast.makeText(getActivity(), "Note added.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void checklistRemindersBackButtonPressed() {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        checklistsTitle = mChecklistsTitleEt.getText().toString();
    }

    /**
     * Gets number of children inside 'Notes' of current user from firebase database
     */
    private void getNotesCount() {

        FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(getActivity());

        String currentUser = firebaseAuthorisation.getCurrentUser();

        mDatabase.child(currentUser).child("Notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // gives total number of children in the database reference
                count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
