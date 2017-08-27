package com.example.sarthak.notes.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.models.NoteReminders;
import com.example.sarthak.notes.utils.AlarmReceiver;
import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.utils.Constants;
import com.example.sarthak.notes.R;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TakeNoteRemindersFragment extends Fragment implements
        View.OnClickListener, BackButtonListener, SetImageListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    String notesTitle = " ";
    String notesBody = " ";
    String noteReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    String noteReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
    String noteReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE) + 1);
    String noteReminderHour = String.valueOf(20);
    String noteReminderMinute = String.valueOf(0);

    int count, notesPosition;

    private Uri notesImageUri;

    private static final String[] dayArray = {"Today", "Tomorrow", "Select any day..."};
    private static final String[] timeArray = {"After 1 hour", "After 6 hours", "After 12 hours", "Select any time..."};

    NoteReminders noteRemindersData = new NoteReminders();

    private EditText mNotesTitleEt, mNotesBodyEt;
    private ImageView mNotesImage;

    private Button mAlarmButton;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    DatabaseReference mDatabase;
    StorageReference mStorage;

    private Calendar cal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_note_reminders, container, false);

        // Set title bar
        ((NotesActivity) getActivity()).getSupportActionBar().setTitle(R.string.reminders);

        notesPosition = getArguments().getInt("position");
        noteRemindersData = (NoteReminders) getArguments().getSerializable("notes");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference();

        setUpView(view);
        setUpDateTimePicker();

        getNoteRemindersCount();

        cal = Calendar.getInstance();

        // editText textChanged listener
        mNotesTitleEt.addTextChangedListener(titleWatcher);
        mNotesBodyEt.addTextChangedListener(bodyWatcher);
        // button onClick listener
        mAlarmButton.setOnClickListener(this);

        displayData();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((NotesActivity) context).backButtonListener = this;
        ((NotesActivity) context).setImageListener = this;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.buttonAlarm:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = LayoutInflater.from(getActivity());
                // set view group as null
                final ViewGroup nullParent = null;

                View dialogView = inflater.inflate(R.layout.dialog_configure_reminder, nullParent);
                alertDialogBuilder.setView(dialogView);

                Spinner daySpinner = (Spinner) dialogView.findViewById(R.id.daySpinner);
                ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dayArray);

                dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                daySpinner.setSelection(2);
                daySpinner.setAdapter(dayAdapter);
                daySpinner.setOnItemSelectedListener(this);

                Spinner timeSpinner = (Spinner) dialogView.findViewById(R.id.timeSpinner);
                ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, timeArray);

                timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timeSpinner.setSelection(1);
                timeSpinner.setAdapter(timeAdapter);
                timeSpinner.setOnItemSelectedListener(this);

                // setup alert dialog
                alertDialogBuilder
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {


                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show alert dialog
                alertDialog.show();
                break;
        }
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

            notesTitle = mNotesTitleEt.getText().toString();
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

    //----------------------------------------------------------------------------------
    // spinner itemSelected listener
    //----------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        Spinner spinner = (Spinner) adapterView;

        SharedPreferences.Editor edit = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).edit();

        switch (spinner.getId()) {

            case R.id.daySpinner :

                switch (position) {

                    case 0 :

                        noteReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        noteReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
                        noteReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
                        edit.apply();
                        break;

                    case 1 :

                        noteReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        noteReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
                        noteReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE) + 1);
                        edit.apply();
                        break;

                    case 2 :

                        datePickerDialog.show();
                        break;
                }
                break;

            case R.id.timeSpinner :

                switch (position) {

                    case 0 :

                        noteReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1);
                        noteReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
                        break;

                    case 1 :

                        noteReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 6);
                        noteReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
                        break;

                    case 2 :

                        noteReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 12);
                        noteReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
                        break;

                    case 3 :

                        timePickerDialog.show();
                        break;
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

        noteReminderYear = String.valueOf(year);
        noteReminderMonth = String.valueOf(month + 1);
        noteReminderDate = String.valueOf(date);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        noteReminderHour = String.valueOf(hour);
        noteReminderMinute = String.valueOf(minute);
    }

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
        String currentUser = firebaseAuth.getCurrentUser();

        if (noteRemindersData != null) {

            notesDatabase = mDatabase.child(currentUser).child("Reminders").child("Reminders_0" + String.valueOf(notesPosition));
            imageStorage = mStorage.child(currentUser).child("Reminders").child("Reminders_0" + String.valueOf(notesPosition) + ".jpg");
        } else {

            int notesCount = count;

            notesDatabase = mDatabase.child(currentUser).child("Reminders").child("Reminders_0" + String.valueOf(notesCount + 1));
            imageStorage = mStorage.child(currentUser).child("Reminders").child("Reminders_0" + String.valueOf(notesCount + 1) + ".jpg");
        }

        if (!(notesTitle.equals(" ") && notesBody.equals(" "))) {

            NoteReminders notes = new NoteReminders(notesTitle, notesBody, noteReminderYear,
                    noteReminderMonth, noteReminderDate, noteReminderHour, noteReminderMinute);

            if (notesImageUri != null) {

                Map notesMap = new HashMap<>();
                notesMap.put("noteReminderYear", noteReminderYear);
                notesMap.put("noteReminderMonth", noteReminderMonth);
                notesMap.put("noteReminderDate", noteReminderDate);
                notesMap.put("noteReminderHour", noteReminderHour);
                notesMap.put("noteReminderMinute", noteReminderMinute);
                notesMap.put("notesTitle", notesTitle);
                notesMap.put("notesBody", notesBody);

                notesDatabase.updateChildren(notesMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {

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

        mNotesTitleEt = (EditText) view.findViewById(R.id.noteRemindersTitle);
        mNotesBodyEt = (EditText) view.findViewById(R.id.noteRemindersBody);
        mAlarmButton = (Button) view.findViewById(R.id.buttonAlarm);
        mNotesImage = (ImageView) view.findViewById(R.id.notesImage);
    }

    private void setUpDateTimePicker() {

        datePickerDialog = new DatePickerDialog(getActivity(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        timePickerDialog = new TimePickerDialog(getActivity(), this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);
    }

    private void setAlarm(Calendar cal) {

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    private void displayData() {

        if (noteRemindersData != null) {

            mNotesTitleEt.setText(noteRemindersData.getNotesTitle());
            mNotesBodyEt.setText(noteRemindersData.getNotesBody());
            mAlarmButton.setText(noteRemindersData.getNoteReminderDate() + "/" +
                    noteRemindersData.getNoteReminderMonth() + "/" +
                    noteRemindersData.getNoteReminderYear() + ", " +
                    noteRemindersData.getNoteReminderHour() + ":" +
                    noteRemindersData.getNoteReminderMinute());

            if (noteRemindersData.getImageUri() != null) {

                this.notesImageUri = Uri.parse(noteRemindersData.getImageUri());
                Picasso.with(getActivity())
                        .load(noteRemindersData.getImageUri())
                        .into(mNotesImage);
            }
        }
    }

    /**
     * Gets number of children inside 'NoteReminders' of current user from firebase database
     */
    private void getNoteRemindersCount() {

        FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(getActivity());

        String currentUser = firebaseAuthorisation.getCurrentUser();

        mDatabase.child(currentUser).child("Reminders").addValueEventListener(new ValueEventListener() {
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
