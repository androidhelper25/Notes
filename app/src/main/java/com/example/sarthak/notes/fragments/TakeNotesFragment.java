package com.example.sarthak.notes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TakeNotesFragment extends Fragment implements BackButtonListener {

    String notesTitle = " ";
    String notesBody = " ";

    int count, notesPosition;

    Notes notesData = new Notes();

    private EditText mNotesTitleEt, mNotesBodyEt;

    DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_notes, container, false);

        notesPosition = getArguments().getInt("position");
        notesData = (Notes) getArguments().getSerializable("notes");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        getNotesCount();

        mNotesTitleEt = (EditText) view.findViewById(R.id.notesTitle);
        mNotesBodyEt = (EditText) view.findViewById(R.id.notesBody);

        mNotesTitleEt.addTextChangedListener(titleWatcher);
        mNotesBodyEt.addTextChangedListener(bodyWatcher);

        displayData();

        return view;
    }

    private void displayData() {

        if (notesData != null) {

            mNotesTitleEt.setText(notesData.getNotesTitle());
            mNotesBodyEt.setText(notesData.getNotesBody());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((NotesActivity) context).backButtonListener = this;
    }

    @Override
    public void notesBackButtonPressed() {

        DatabaseReference notesDatabase;

        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(getActivity());

        if (notesData != null) {

            notesDatabase = mDatabase.child(firebaseAuth.getCurrentUser()).child("Notes").child("Notes_0" + String.valueOf(notesPosition));
        } else {

            int notesCount = count;

            notesDatabase = mDatabase.child(firebaseAuth.getCurrentUser()).child("Notes").child("Notes_0" + String.valueOf(notesCount + 1));
        }

        if (!(notesTitle.equals(" ") && notesBody.equals(" "))) {

            Notes notes = new Notes(notesTitle, notesBody);

            notesDatabase.setValue(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    public void noteRemindersBackButtonPressed() {

    }

    @Override
    public void checklistsBackButtonPressed() {

    }

    @Override
    public void checklistRemindersBackButtonPressed() {

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

    TextWatcher titleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            notesTitle =  mNotesTitleEt.getText().toString();
        }
    };

    TextWatcher bodyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            notesBody = mNotesBodyEt.getText().toString();
        }
    };
}
