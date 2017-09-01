package com.example.sarthak.notes.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sarthak.notes.firebasemanager.FirebaseUploadDataManager;
import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
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

import java.util.HashMap;
import java.util.Map;

public class TakeNotesFragment extends Fragment implements BackButtonListener, SetImageListener {

    private String TAG = "MSG";

    String notesTitle = "";
    String notesBody = "";

    int count, notesPosition;

    private Uri notesImageUri;

    Notes notesData = new Notes();

    private EditText mNotesTitleEt, mNotesBodyEt;
    private ImageView mNotesImage;

    DatabaseReference mDatabase;
    StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_notes, container, false);

        // set action bar title
        ((NotesActivity) getActivity()).getSupportActionBar().setTitle(R.string.notes);

        notesPosition = getArguments().getInt(Constants.INTENT_PASS_POSITION);
        notesData = (Notes) getArguments().getSerializable(Constants.INTENT_PASS_SERIALIZABLE_OBJECT);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_REFERENCE);
        mStorage = FirebaseStorage.getInstance().getReference();

        // set up view components
        setUpView(view);
        // get total number of items in 'Notes' in firebase database
        getNotesCount();

        //display data in view components
        displayData();

        //------------------------------------------------------------------------------------------
        // edit text's textChangedListeners
        //------------------------------------------------------------------------------------------
        mNotesTitleEt.addTextChangedListener(titleWatcher);
        mNotesBodyEt.addTextChangedListener(bodyWatcher);

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
    // editText textChanged listener
    //----------------------------------------------------------------------------------------------
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
        // if data is null, create new database reference. Else refer to existing database reference
        if (notesData != null) {

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

        if (!(notesTitle.equals("") && notesBody.equals(""))) {

            Notes notes = new Notes(notesTitle, notesBody);

            // If image is not set,i.e., notesImages is null, set model 'Notes' to firebase database.
            // Else, use a hashMap to add values to database so that the database is updated with the values instead
            // of creating a new model which would delete the imageUri from firebase database.
            if (notesImageUri != null) {

                Map notesMap = new HashMap<>();
                notesMap.put("notesTitle", notesTitle);
                notesMap.put("notesBody", notesBody);

                notesDatabase.updateChildren(notesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            // upload image to firebase storage and set value in firebase database
                            firebaseUploadDataManager.uploadImageToFirebase(notesDatabase, imageStorage, notesImageUri);
                        }
                    }
                });

            } else {

                notesDatabase.setValue(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        mNotesTitleEt = (EditText) view.findViewById(R.id.notesTitle);
        mNotesBodyEt = (EditText) view.findViewById(R.id.notesBody);
        mNotesImage = (ImageView) view.findViewById(R.id.notesImage);
    }

    /**
     * Display data in view components
     */
    private void displayData() {

        // check notesData to avoid NullPointerException for a new Note
        if (notesData != null) {

            notesTitle = notesData.getNotesTitle();
            notesBody = notesData.getNotesBody();

            mNotesTitleEt.setText(notesTitle);
            mNotesBodyEt.setText(notesBody);

            if (notesData.getImageUri() != null) {

                this.notesImageUri = Uri.parse(notesData.getImageUri());
                Picasso.with(getActivity())
                        .load(notesData.getImageUri())
                        .into(mNotesImage);
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
