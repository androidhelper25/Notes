package com.example.sarthak.notes.fragments;

import android.content.Context;
import android.net.Uri;
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
import android.widget.ImageView;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.adapters.TakeChecklistsRecyclerAdapter;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.example.sarthak.notes.firebasemanager.FirebaseUploadDataManager;
import com.example.sarthak.notes.models.Checklists;
import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.utils.CheckListListener;
import com.example.sarthak.notes.utils.Constants;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TakeChecklistsFragment extends Fragment implements CheckListListener, BackButtonListener, SetImageListener, TextWatcher {

    private String TAG = "MSG";

    int count, notesPosition;

    private Uri notesImageUri;

    String checklistsTitle = "";
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

        // Set title bar
        ((NotesActivity) getActivity()).getSupportActionBar().setTitle(R.string.notes);

        // retrieve notesPosition and data from 'Notes' fragment
        notesPosition = getArguments().getInt(Constants.INTENT_PASS_POSITION);
        checklistsData = (Checklists) getArguments().getSerializable(Constants.INTENT_PASS_SERIALIZABLE_OBJECT);

        // set up an instance of firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_REFERENCE);
        // set up an instance of firebase storage
        mStorage = FirebaseStorage.getInstance().getReference();

        // set up view components
        setUpView(view);
        // get total number of items in 'Notes' in firebase database
        getNotesCount();

        RecyclerView mChecklistList = (RecyclerView) view.findViewById(R.id.checklistList);
        takeChecklistsRecyclerAdapter = new TakeChecklistsRecyclerAdapter(getActivity(), dataList, statusList, this, checklistListenerContext);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mChecklistList.setLayoutManager(mLayoutManager);
        mChecklistList.setItemAnimator(new DefaultItemAnimator());
        mChecklistList.setAdapter(takeChecklistsRecyclerAdapter);

        // display data in view components
        displayData();

        // editText textChanged listener
        mChecklistsTitleEt.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // callback for back button pressed in fragment
        ((NotesActivity) context).backButtonListener = this;
        // callback for setting image in fragment
        ((NotesActivity) context).setImageListener = this;
    }

    //----------------------------------------------------------------------------------------------
    // checkList component's click listeners
    //----------------------------------------------------------------------------------------------
    /**
     * Callback for enter key pressed in check list for adding data to checklist
     *
     * @param data is the string value entered in individual item of checklist
     * @param status is the status whether the item is checked or not
     * @param position is the position of the item in arraylist
     */
    @Override
    public void checklistEnterKeyPressed(String data, String status, int position) {

        if (position < dataList.size()) {
            // update data tin arraylist for existing values in checklist
            dataList.set(position, data);
            statusList.set(position, status);
        } else {
            // add data to arraylist for values entered in checklist
            dataList.add(data);
            // add status for each item entered in checklist
            statusList.add(status);
        }
        // update recycler view adapter
        takeChecklistsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void checklistReminderEnterKeyPressed(String data, String status, int position) {

    }

    /**
     * Callback for check box selected for individual item in checklist
     *
     * @param status is the status whether the item is checked or not
     * @param position is the position of the item in arraylist
     */
    @Override
    public void checklistCheckboxChecked(boolean status, int position) {

        if (position < statusList.size()) {

            // set status of each item in arraylist based on value of checkbox
            if (status) {
                statusList.set(position, Constants.CHECKED_STATUS);
            } else {
                statusList.set(position, Constants.UNCHECKED_STATUS);
            }
        }
    }

    @Override
    public void checklistReminderCheckboxChecked(boolean b, int pos) {

    }

    @Override
    public void checklistDeleteButtonPressed(int position) {

    }

    /**
     * Callback for 'Delete' imageButton pressed in checklist to remove individual item
     *
     * @param position is position of the item in arraylist to be deleted
     */
    @Override
    public void checklistReminderDeleteButtonPressed(int position) {

        if (position < dataList.size()) {

            // remove item at specified position from arraylist
            dataList.remove(position);
            // remove item at specified position from arraylist
            statusList.remove(position);
            // update recycler view adapter
            takeChecklistsRecyclerAdapter.notifyDataSetChanged();
        }
    }

    //----------------------------------------------------------------------------------------------
    // editText textChanged listener
    //----------------------------------------------------------------------------------------------
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

    //----------------------------------------------------------------------------------------------
    // Callback for setImage in fragment
    //----------------------------------------------------------------------------------------------
    @Override
    public void setImage(Uri uri) {

        this.notesImageUri = uri;
        Picasso.with(getActivity())
                .load(uri)
                .into(mNotesImage);
    }

    //----------------------------------------------------------------------------------------------
    // Callback for back button pressed in fragment
    //----------------------------------------------------------------------------------------------
    @Override
    public void backButtonPressed() {

        final DatabaseReference notesDatabase;
        final StorageReference imageStorage;

        final FirebaseUploadDataManager firebaseUploadDataManager = new FirebaseUploadDataManager(getActivity());

        // get current user
        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(getActivity());
        final String currentUser = firebaseAuth.getCurrentUser();

        // set up an instance for firebase database
        // If data is null, create new database reference. Else refer to existing database reference.
        if (checklistsData != null) {

            notesDatabase = mDatabase.child(currentUser).child(Constants.TYPE_NOTES)
                    .child(Constants.TYPE_NOTES + "_0" + String.valueOf(notesPosition));
            imageStorage = mStorage.child(currentUser).child(Constants.TYPE_NOTES)
                    .child(Constants.TYPE_NOTES + "_0" + String.valueOf(notesPosition) + ".jpg");
        } else {

            int notesCount = count;

            notesDatabase = mDatabase.child(currentUser).child(Constants.TYPE_NOTES)
                    .child(Constants.TYPE_NOTES + "_0" + String.valueOf(notesCount + 1));
            imageStorage = mStorage.child(currentUser).child(Constants.TYPE_NOTES)
                    .child(Constants.TYPE_NOTES + "_0" + String.valueOf(notesCount + 1) + ".jpg");
        }

        if (!(checklistsTitle.equals("") && dataList.isEmpty())) {

            final HashMap<String, HashMap<String, String>> dataMap = new HashMap<>();

            // set up content for each item in checklist as HashMap
            for (int i = 0; i < dataList.size(); i++) {

                HashMap<String, String> contentMap = new HashMap<>();
                contentMap.put(Constants.HASHMAP_VALUE, dataList.get(i));
                contentMap.put(Constants.HASHMAP_STATUS, statusList.get(i));

                dataMap.put("content_0" + String.valueOf(i + 1), contentMap);
            }

            Checklists checklists = new Checklists(checklistsTitle, dataMap);

            // If image is not set,i.e., notesImages is null, set model 'Checklists' to firebase database.
            // Else, use a hashMap to add values to database so that the database is updated with the values instead
            // of creating a new model which would delete the imageUri from firebase database.
            if (notesImageUri != null) {

                Map notesMap = new HashMap<>();
                notesMap.put("notesTitle", checklistsTitle);
                notesMap.put("content", dataMap);

                notesDatabase.updateChildren(notesMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {

                            // upload image to firebase storage and set value in firebase database
                            firebaseUploadDataManager.uploadImageToFirebase(notesDatabase, imageStorage, notesImageUri);
                        }
                    }
                });

            } else {

                notesDatabase.setValue(checklists).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            if (getActivity() != null) {
                                Log.i(TAG, getString(R.string.note_added));
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * Initialise view components
     */
    private void setUpView(View view) {

        mChecklistsTitleEt = (EditText) view.findViewById(R.id.checklistsTitle);
        mNotesImage = (ImageView) view.findViewById(R.id.notesImage);
    }

    /**
     * Display data in view components
     */
    private void displayData() {

        // check checklistsData to avoid NullPointerException for a new Note
        if (checklistsData != null) {

            checklistsTitle = checklistsData.getNotesTitle();

            mChecklistsTitleEt.setText(checklistsData.getNotesTitle());

            if (checklistsData.getImageUri() != null) {

                this.notesImageUri = Uri.parse(checklistsData.getImageUri());
                Picasso.with(getActivity())
                        .load(checklistsData.getImageUri())
                        .into(mNotesImage);
            }

            for (int i = 0 ; i < checklistsData.getContent().size() ; i++) {

                dataList.add(checklistsData.getContent().get("content_0" + String.valueOf(i + 1)).get(Constants.HASHMAP_VALUE));
                statusList.add(checklistsData.getContent().get("content_0" + String.valueOf(i + 1)).get(Constants.HASHMAP_STATUS));
            }
        }
    }

    /**
     * Gets number of children inside 'Notes' of current user from firebase database
     */
    private void getNotesCount() {

        FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(getActivity());

        String currentUser = firebaseAuthorisation.getCurrentUser();

        mDatabase.child(currentUser).child(Constants.TYPE_NOTES).addValueEventListener(new ValueEventListener() {
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
