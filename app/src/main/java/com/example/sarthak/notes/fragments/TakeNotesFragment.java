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
import android.widget.Toast;

import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
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

import java.util.HashMap;
import java.util.Map;

public class TakeNotesFragment extends Fragment implements BackButtonListener, SetImageListener {

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

        notesPosition = getArguments().getInt("position");
        notesData = (Notes) getArguments().getSerializable("notes");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference();

        setUpView(view);

        getNotesCount();

        displayData();

        //------------------------------------------------------------------------------------
        // textChangedListeners for edit texts
        //------------------------------------------------------------------------------------
        mNotesTitleEt.addTextChangedListener(titleWatcher);
        mNotesBodyEt.addTextChangedListener(bodyWatcher);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((NotesActivity) context).backButtonListener = this;
        ((NotesActivity) context).setImageListener = this;
    }

    //----------------------------------------------------------------------------------
    // editText textChanged listener
    //----------------------------------------------------------------------------------
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

    @Override
    public void setImage(Uri uri) {

        this.notesImageUri = uri;
        Picasso.with(getActivity())
                .load(uri)
                .into(mNotesImage);
    }

    @Override
    public void backButtonPressed() {

        final DatabaseReference notesDatabase;
        final StorageReference imageStorage;

        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(getActivity());
        final String currentUser = firebaseAuth.getCurrentUser();

        if (notesData != null) {

            notesDatabase = mDatabase.child(currentUser).child("Notes").child("Notes_0" + String.valueOf(notesPosition));
            imageStorage = mStorage.child(currentUser).child("Notes").child("Notes_0" + String.valueOf(notesPosition) + ".jpg");
        } else {

            int notesCount = count;

            notesDatabase = mDatabase.child(currentUser).child("Notes").child("Notes_0" + String.valueOf(notesCount + 1));
            imageStorage = mStorage.child(currentUser).child("Notes").child("Notes_0" + String.valueOf(notesCount + 1) + ".jpg");
        }

        if (!(notesTitle.equals("") && notesBody.equals(""))) {

            Notes notes = new Notes(notesTitle, notesBody);

            if (notesData != null) {

                Map notesMap = new HashMap<>();
                notesMap.put("notesTitle", notesTitle);
                notesMap.put("notesBody", notesBody);

                notesDatabase.updateChildren(notesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Log.e("podi", String.valueOf(notesImageUri));
                            if (notesImageUri != null) {

                                imageStorage.putFile(notesImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            @SuppressWarnings("VisibleForTests") Uri Url = task.getResult().getDownloadUrl();
                                            if (Url != null) {

                                                Map imageMap = new HashMap<>();
                                                imageMap.put("imageUri", Url.toString());

                                                notesDatabase.updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                if (getActivity() != null) {

                                    Toast.makeText(getActivity(), "Note added.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
            } else {

                notesDatabase.setValue(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Log.e("podi", String.valueOf(notesImageUri));
                            if (notesImageUri != null) {

                                imageStorage.putFile(notesImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            @SuppressWarnings("VisibleForTests") Uri Url = task.getResult().getDownloadUrl();
                                            if (Url != null) {

                                                Map imageMap = new HashMap<>();
                                                imageMap.put("imageUri", Url.toString());

                                                notesDatabase.updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                if (getActivity() != null) {

                                    Toast.makeText(getActivity(), "Note added.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private void setUpView(View view) {

        mNotesTitleEt = (EditText) view.findViewById(R.id.notesTitle);
        mNotesBodyEt = (EditText) view.findViewById(R.id.notesBody);
        mNotesImage = (ImageView) view.findViewById(R.id.notesImage);
    }

    private void displayData() {

        if (notesData != null) {

            this.notesTitle = notesData.getNotesTitle();
            this.notesBody = notesData.getNotesBody();

            mNotesTitleEt.setText(notesData.getNotesTitle());
            mNotesBodyEt.setText(notesData.getNotesBody());

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
