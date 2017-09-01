package com.example.sarthak.notes.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.sarthak.notes.firebasemanager.FirebaseUploadDataManager;
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
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TakeNoteRemindersFragment extends Fragment implements
        View.OnClickListener, BackButtonListener, SetImageListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    String notesTitle = "";
    String notesBody = "";
    String noteReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    String noteReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
    String noteReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
    String noteReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1);
    String noteReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));

    int count, notesPosition;

    private Uri notesImageUri;

    private static String[] dayArray;
    private static String[] timeArray;

    NoteReminders noteRemindersData = new NoteReminders();

    private EditText mNotesTitleEt, mNotesBodyEt;
    private ImageView mNotesImage;

    private Button mAlarmButton;

    private Spinner daySpinner, timeSpinner;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    DatabaseReference mDatabase;
    StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_note_reminders, container, false);

        // set title bar
        ((NotesActivity) getActivity()).getSupportActionBar().setTitle(R.string.reminders);

        // set spinner items to string array
        dayArray = getResources().getStringArray(R.array.day_array);
        timeArray = getResources().getStringArray(R.array.time_array);

        // retrieve notesPosition and data from 'Reminders' fragment
        notesPosition = getArguments().getInt(Constants.INTENT_PASS_POSITION);
        noteRemindersData = (NoteReminders) getArguments().getSerializable(Constants.INTENT_PASS_SERIALIZABLE_OBJECT);

        // set up an instance of firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_REFERENCE);
        // set up an instance of firebase storage reference
        mStorage = FirebaseStorage.getInstance().getReference();

        // set up view components
        setUpView(view);

        // get total number of items in 'Notes' in firebase database
        getNoteRemindersCount();

        // editText textChanged listener
        mNotesTitleEt.addTextChangedListener(titleWatcher);
        mNotesBodyEt.addTextChangedListener(bodyWatcher);
        // button onClick listener
        mAlarmButton.setOnClickListener(this);

        // display data in view components
        displayData();

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
    // button onClick listener
    //----------------------------------------------------------------------------------------------
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

                // set up date and time picker
                setUpDateTimeSpinner(dialogView);

                // setup alert dialog
                alertDialogBuilder
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        // value for date and time are set in spinner's callback
                                        // no special action to be taken on positive button click
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

    //----------------------------------------------------------------------------------------------
    // spinner itemSelected listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        Spinner spinner = (Spinner) adapterView;

        switch (spinner.getId()) {

            case R.id.daySpinner :

                switch (position) {

                    case 0 :

                        noteReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        noteReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
                        noteReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
                        break;

                    case 1 :

                        noteReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        noteReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
                        noteReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE) + 1);
                        break;

                    case 2 :

                        setUpDatePicker();
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

                        setUpTimePicker();
                        break;
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //----------------------------------------------------------------------------------------------
    // Date and time picker's callback
    //----------------------------------------------------------------------------------------------
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

        noteReminderYear = String.valueOf(year);
        noteReminderMonth = String.valueOf(month + 1);
        noteReminderDate = String.valueOf(date);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        // create an instance of current date
        Calendar current = Calendar.getInstance();

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hour);
        datetime.set(Calendar.MINUTE, minute);

        // check if the set time has already passed or not
        // First compare if the date set is for today  or not. If not then no need to check time.
        // If yes, compare the current time with set time.
        if (daySpinner.getSelectedItemPosition() == 0 || (noteReminderYear.equals(String.valueOf(Calendar.YEAR)) &&
                                                            noteReminderMonth.equals(String.valueOf(Calendar.MONTH)) &&
                                                            noteReminderDate.equals(String.valueOf(Calendar.DATE)))) {

            if(datetime.getTimeInMillis() > current.getTimeInMillis()){

                noteReminderHour = String.valueOf(hour);
                noteReminderMinute = String.valueOf(minute);
            } else {

                Toast.makeText(getActivity(), R.string.invalid_time_toast, Toast.LENGTH_SHORT).show();
                // set alarm for after 1 hour as default
                noteReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1);
                noteReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
            }
        } else {

            noteReminderHour = String.valueOf(hour);
            noteReminderMinute = String.valueOf(minute);
        }
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
        String currentUser = firebaseAuth.getCurrentUser();

        // set up an instance for firebase database
        // if data is null, create new database reference. Else refer to existing database reference
        if (noteRemindersData != null) {

            notesDatabase = mDatabase.child(currentUser).child(Constants.TYPE_REMINDERS)
                    .child(Constants.TYPE_REMINDERS + "_0" + String.valueOf(notesPosition));
            imageStorage = mStorage.child(currentUser).child(Constants.TYPE_REMINDERS)
                    .child(Constants.TYPE_REMINDERS + "_0" + String.valueOf(notesPosition) + ".jpg");
        } else {

            int notesCount = count;

            notesDatabase = mDatabase.child(currentUser).child(Constants.TYPE_REMINDERS)
                    .child(Constants.TYPE_REMINDERS + "_0" + String.valueOf(notesCount + 1));
            imageStorage = mStorage.child(currentUser).child(Constants.TYPE_REMINDERS)
                    .child(Constants.TYPE_REMINDERS + "_0" + String.valueOf(notesCount + 1) + ".jpg");
        }

        // get calendar instance to set alarm
        final Calendar calendar = getCalendarInstance();

        if (!(notesTitle.equals("") && notesBody.equals(""))) {

            NoteReminders notes = new NoteReminders(notesTitle, notesBody, noteReminderYear,
                    noteReminderMonth, noteReminderDate, noteReminderHour, noteReminderMinute);

            // If image is not set,i.e., notesImageUri is null, set model 'NoteReminders' to firebase database.
            // Else, use a hashMap to add values to database so that the database is updated with the values instead
            // of creating a new model which would delete the imageUri from firebase database.
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

                            setAlarm(calendar);
                            firebaseUploadDataManager.uploadImageToFirebase(notesDatabase, imageStorage, notesImageUri);
                        }
                    }
                });

            } else {

                notesDatabase.setValue(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            setAlarm(calendar);
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

        mNotesTitleEt = (EditText) view.findViewById(R.id.noteRemindersTitle);
        mNotesBodyEt = (EditText) view.findViewById(R.id.noteRemindersBody);
        mAlarmButton = (Button) view.findViewById(R.id.buttonAlarm);
        mNotesImage = (ImageView) view.findViewById(R.id.notesImage);
    }

    /**
     * Set up date and time picker dialog
     */
    private void setUpDatePicker() {

        datePickerDialog = new DatePickerDialog(getActivity(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void setUpTimePicker() {

        timePickerDialog = new TimePickerDialog(getActivity(), this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    /**
     * Initialise data for date and time spinners
     */
    private void setUpDateTimeSpinner(View dialogView) {

        daySpinner = (Spinner) dialogView.findViewById(R.id.daySpinner);
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dayArray);

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setSelection(2);
        daySpinner.setAdapter(dayAdapter);
        daySpinner.setOnItemSelectedListener(this);

        timeSpinner = (Spinner) dialogView.findViewById(R.id.timeSpinner);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, timeArray);

        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setSelection(1);
        timeSpinner.setAdapter(timeAdapter);
        timeSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Set value to calendar with the date and time set by the user.
     *
     * @return a Calendar object which contains the date and time set by the user
     */
    private Calendar getCalendarInstance() {

        Calendar setAlarm = Calendar.getInstance();
        setAlarm.set(Calendar.YEAR, Integer.parseInt(noteReminderYear));
        setAlarm.set(Calendar.MONTH, Integer.parseInt(noteReminderMonth) - 1);
        setAlarm.set(Calendar.DATE, Integer.parseInt(noteReminderDate));
        setAlarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(noteReminderHour));
        setAlarm.set(Calendar.MINUTE, Integer.parseInt(noteReminderMinute));
        setAlarm.set(Calendar.SECOND, 0);

        return setAlarm;
    }

    private void setAlarm(Calendar setAlarm) {

        Calendar current = Calendar.getInstance();

        if (setAlarm.compareTo(current) > 0) {

            if (getActivity() != null) {

                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, setAlarm.getTimeInMillis(), pendingIntent);
            }
        }
    }

    /**
     * Display data in view components
     */
    private void displayData() {

        // check noteRemindersData to avoid NullPointerException for a new Note
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

            this.notesTitle = noteRemindersData.getNotesTitle();
            this.noteReminderYear = noteRemindersData.getNoteReminderYear();
            this.noteReminderMonth = noteRemindersData.getNoteReminderMonth();
            this.noteReminderDate = noteRemindersData.getNoteReminderDate();
            this.noteReminderHour = noteRemindersData.getNoteReminderHour();
            this.noteReminderMinute = noteRemindersData.getNoteReminderMinute();
        }
    }

    /**
     * Gets number of children inside 'NoteReminders' of current user from firebase database
     */
    private void getNoteRemindersCount() {

        FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(getActivity());

        String currentUser = firebaseAuthorisation.getCurrentUser();

        mDatabase.child(currentUser).child(Constants.TYPE_REMINDERS).addValueEventListener(new ValueEventListener() {
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
