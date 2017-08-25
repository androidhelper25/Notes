package com.example.sarthak.notes.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.adapters.TakeChecklistsRecyclerAdapter;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.example.sarthak.notes.models.Checklists;
import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.utils.CheckListListener;
import com.example.sarthak.notes.utils.SetImageListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class TakeChecklistsFragment extends Fragment implements CheckListListener, BackButtonListener, SetImageListener, TextWatcher {

    int count, notesPosition;

    private Uri notesImageUri;

    String checklistsTitle = " ";
    String checklistListenerContext = "checklists";

    private ArrayList<String> dataList = new ArrayList<>();
    private ArrayList<String> statusList = new ArrayList<>();

    Checklists checklistsData = new Checklists();

    private EditText mChecklistsTitleEt;
    private ImageView mNotesImage;

    private TakeChecklistsRecyclerAdapter takeChecklistsRecyclerAdapter;

    DatabaseReference mDatabase;
    StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_checklists, container, false);

        notesPosition = getArguments().getInt("position");
        checklistsData = (Checklists) getArguments().getSerializable("notes");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference();

        getNotesCount();

        mChecklistsTitleEt = (EditText) view.findViewById(R.id.checklistsTitle);
        mChecklistsTitleEt.addTextChangedListener(this);

        mNotesImage = (ImageView) view.findViewById(R.id.notesImage);

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

            if (checklistsData.getImageUri() != null) {

                this.notesImageUri = Uri.parse(checklistsData.getImageUri());
                Picasso.with(getActivity())
                        .load(checklistsData.getImageUri())
                        .into(mNotesImage);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((NotesActivity) context).backButtonListener = this;
        ((NotesActivity) context).setImageListener = this;
    }

    @Override
    public void setImage(Uri uri) {

        this.notesImageUri = uri;
        Picasso.with(getActivity())
                .load(uri)
                .into(mNotesImage);
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
    public void checklistCheckboxChecked(boolean status, int position) {

        if (position < statusList.size()) {

            if (status) {
                statusList.set(position, "checked");
            } else {
                statusList.set(position, "unchecked");
            }
        }
    }

    @Override
    public void checklistReminderCheckboxChecked(boolean b, int pos) {

    }

    @Override
    public void checklistDeleteButtonPressed(int position) {

    }

    @Override
    public void checklistReminderDeleteButtonPressed(int position) {

        dataList.remove(position);
        statusList.remove(position);
        takeChecklistsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void notesBackButtonPressed() {

    }

    @Override
    public void noteRemindersBackButtonPressed() {

    }

    @Override
    public void checklistsBackButtonPressed() {

        final DatabaseReference notesDatabase;
        final StorageReference imageStorage;

        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(getActivity());
        final String currentUser = firebaseAuth.getCurrentUser();

        if (checklistsData != null) {

            notesDatabase = mDatabase.child(currentUser).child("Notes").child("Notes_0" + String.valueOf(notesPosition));
            imageStorage = mStorage.child(currentUser).child("Notes").child("Notes_0" + String.valueOf(notesPosition) + ".jpg");
        } else {

            int notesCount = count;

            notesDatabase = mDatabase.child(currentUser).child("Notes").child("Notes_0" + String.valueOf(notesCount + 1));
            imageStorage = mStorage.child(currentUser).child("Notes").child("Notes_0" + String.valueOf(notesCount + 1) + ".jpg");
        }

        if (!(checklistsTitle.equals(" ") && dataList.isEmpty() && notesImageUri != null)) {

            final HashMap<String, HashMap<String, String>> dataMap = new HashMap<>();

            for (int i = 0; i < dataList.size(); i++) {

                HashMap<String, String> contentMap = new HashMap<>();
                contentMap.put("value", dataList.get(i));
                contentMap.put("status", statusList.get(i));

                dataMap.put("content_0" + String.valueOf(i + 1), contentMap);
            }

            if (notesImageUri != null) {

                imageStorage.putFile(notesImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            @SuppressWarnings("VisibleForTests") Uri Url = task.getResult().getDownloadUrl();
                            if (Url != null) {

                                Checklists checklists = new Checklists(checklistsTitle, dataMap, Url.toString());

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
                    }
                });

            } else {

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
