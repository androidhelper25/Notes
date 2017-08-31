package com.example.sarthak.notes.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.adapters.TakeChecklistsRecyclerAdapter;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.example.sarthak.notes.firebasemanager.FirebaseUploadDataManager;
import com.example.sarthak.notes.models.ChecklistReminders;
import com.example.sarthak.notes.utils.AlarmReceiver;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TakeChecklistRemindersFragment extends Fragment implements
        CheckListListener, BackButtonListener, SetImageListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener {

    boolean checkboxStatus = false;

    String checklistRemindersTitle = "";
    String checklistReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    String checklistReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
    String checklistReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE) + 1);
    String checklistReminderHour = String.valueOf(20);
    String checklistReminderMinute = String.valueOf(0);

    String checklistListenerContext = "checklistReminders";

    int count, notesPosition;

    private Uri notesImageUri;

    private static String[] dayArray;
    private static String[] timeArray;

    private ArrayList<String> dataList = new ArrayList<>();
    private ArrayList<String> statusList = new ArrayList<>();

    ChecklistReminders checklistRemindersData = new ChecklistReminders();

    private EditText mChecklistRemindersTitleEt;
    private ImageView mNotesImage;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private Button mAlarmButton;

    private Spinner daySpinner, timeSpinner;

    private TakeChecklistsRecyclerAdapter takeChecklistsRecyclerAdapter;

    DatabaseReference mDatabase;
    StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_checklist_reminders, container, false);

        // set title bar
        ((NotesActivity) getActivity()).getSupportActionBar().setTitle(R.string.reminders);

        // set spinner items to string array
        dayArray = getResources().getStringArray(R.array.day_array);
        timeArray = getResources().getStringArray(R.array.time_array);

        // retrieve notesPosition and data from 'Reminders' fragment
        notesPosition = getArguments().getInt(Constants.INTENT_PASS_POSITION);
        checklistRemindersData = (ChecklistReminders) getArguments().getSerializable(Constants.INTENT_PASS_SERIALIZABLE_OBJECT);

        // set up an instance of firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_REFERENCE);
        // set up an instance of firebase storage reference
        mStorage = FirebaseStorage.getInstance().getReference();

        // set up view components
        setUpView(view);
        // set up date and time picker
        setUpDateTimePicker();

        // get total number of items in 'Reminders' in firebase database
        getRemindersCount();

        RecyclerView mChecklistRemindersList = (RecyclerView) view.findViewById(R.id.checklistList);
        takeChecklistsRecyclerAdapter = new TakeChecklistsRecyclerAdapter(getActivity(), dataList, statusList, this, checklistListenerContext);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mChecklistRemindersList.setLayoutManager(mLayoutManager);
        mChecklistRemindersList.setItemAnimator(new DefaultItemAnimator());
        mChecklistRemindersList.setAdapter(takeChecklistsRecyclerAdapter);

        // display data in view components
        displayData();

        // editText textChanged listener
        mChecklistRemindersTitleEt.addTextChangedListener(this);
        // button onClick listener
        mAlarmButton.setOnClickListener(this);

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
    // checkList component's click listeners
    //----------------------------------------------------------------------------------------------
    @Override
    public void checklistEnterKeyPressed(String string, String s, int pos) {

    }

    /**
     * Callback for enter key pressed in check list for adding data to checklist
     *
     * @param data is the string value entered in individual item of checklist
     * @param status is the status whether the item is checked or not
     * @param position is the position of the item in arraylist
     */
    @Override
    public void checklistReminderEnterKeyPressed(String data, String status, int position) {

        if (position < dataList.size()) {
            // update data in arraylist for existing values in checklist
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
    public void checklistCheckboxChecked(boolean b, int i) {

    }

    /**
     * Callback for check box selected for individual item in checklist
     *
     * @param status is the status whether the item is checked or not
     * @param position is the position of the item in arraylist
     */
    @Override
    public void checklistReminderCheckboxChecked(boolean status, int position) {

        checkboxStatus = status;

        if (position < statusList.size()) {

            if (status) {
                statusList.set(position, Constants.CHECKED_STATUS);
            } else {
                statusList.set(position, Constants.UNCHECKED_STATUS);
            }
        }
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

        // remove item at specified position from arraylist
        dataList.remove(position);
        // remove item at specified position from arraylist
        statusList.remove(position);
        // update recycler view adapter
        takeChecklistsRecyclerAdapter.notifyDataSetChanged();
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

        checklistRemindersTitle = mChecklistRemindersTitleEt.getText().toString();
    }

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

                        checklistReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        checklistReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
                        checklistReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
                        break;

                    case 1 :

                        checklistReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        checklistReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
                        checklistReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE) + 1);
                        break;

                    case 2 :

                        datePickerDialog.show();
                        break;
                }
                break;

            case R.id.timeSpinner :

                switch (position) {

                    case 0 :

                        checklistReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1);
                        checklistReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
                        break;

                    case 1 :

                        checklistReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 6);
                        checklistReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
                        break;

                    case 2 :

                        checklistReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 12);
                        checklistReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
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

    //----------------------------------------------------------------------------------------------
    // Date and time picker's callback
    //----------------------------------------------------------------------------------------------
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

        checklistReminderYear = String.valueOf(year);
        checklistReminderMonth = String.valueOf(month + 1);
        checklistReminderDate = String.valueOf(date);
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
        if (daySpinner.getSelectedItemPosition() == 0 || (checklistReminderYear.equals(String.valueOf(Calendar.YEAR)) &&
                checklistReminderMonth.equals(String.valueOf(Calendar.MONTH)) &&
                checklistReminderDate.equals(String.valueOf(Calendar.DATE)))) {

            if(datetime.getTimeInMillis() > current.getTimeInMillis()){

                checklistReminderHour = String.valueOf(hour);
                checklistReminderMinute = String.valueOf(minute);
            } else {

                Toast.makeText(getActivity(), "Invalid Time.", Toast.LENGTH_SHORT).show();
                // set alarm for after 1 hour as default
                checklistReminderHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1);
                checklistReminderMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
            }
        } else {

            checklistReminderHour = String.valueOf(hour);
            checklistReminderMinute = String.valueOf(minute);
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
        final String currentUser = firebaseAuth.getCurrentUser();

        // set up an instance for firebase database
        // if data is null, create new database reference. Else refer to existing database reference
        if (checklistRemindersData != null) {

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

        if (!(checklistRemindersTitle.equals("") && dataList.isEmpty())) {

            final HashMap<String, HashMap<String, String>> dataMap = new HashMap<>();

            // set up content for each item in checklist as HashMap
            for (int i = 0; i < dataList.size(); i++) {

                HashMap<String, String> contentMap = new HashMap<>();
                contentMap.put(Constants.HASHMAP_VALUE, dataList.get(i));
                contentMap.put(Constants.HASHMAP_STATUS, statusList.get(i));

                dataMap.put("content_0" + String.valueOf(i + 1), contentMap);
            }

            final ChecklistReminders checklistReminders = new ChecklistReminders(checklistRemindersTitle, dataMap,
                    checklistReminderYear, checklistReminderMonth, checklistReminderDate, checklistReminderHour, checklistReminderMinute);

            // If image is not set,i.e., notesImageUri is null, set model 'ChecklistReminders' to firebase database.
            // Else, use a hashMap to add values to database so that the database is updated with the values instead
            // of creating a new model which would delete the imageUri from firebase database.
            if (notesImageUri != null) {

                Map notesMap = new HashMap<>();
                notesMap.put("noteReminderYear", checklistReminderYear);
                notesMap.put("noteReminderMonth", checklistReminderMonth);
                notesMap.put("noteReminderDate", checklistReminderDate);
                notesMap.put("noteReminderHour", checklistReminderHour);
                notesMap.put("noteReminderMinute", checklistReminderMinute);
                notesMap.put("notesTitle", checklistRemindersTitle);
                notesMap.put("content", dataMap);

                notesDatabase.updateChildren(notesMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {

                            //setAlarm(setAlarm);
                            firebaseUploadDataManager.uploadImageToFirebase(notesDatabase, imageStorage, notesImageUri);
                        }
                    }
                });

            } else {

                notesDatabase.setValue(checklistReminders).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            setAlarm(calendar);

                            if (getActivity() != null) {

                                Toast.makeText(getActivity(), getString(R.string.note_added_toast), Toast.LENGTH_SHORT).show();
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

        mChecklistRemindersTitleEt = (EditText) view.findViewById(R.id.checklistsTitle);
        mAlarmButton = (Button) view.findViewById(R.id.buttonAlarm);
        mNotesImage = (ImageView) view.findViewById(R.id.notesImage);
    }

    /**
     * Set up date and time picker dialog
     */
    private void setUpDateTimePicker() {

        datePickerDialog = new DatePickerDialog(getActivity(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        timePickerDialog = new TimePickerDialog(getActivity(), this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);
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
        setAlarm.set(Calendar.YEAR, Integer.parseInt(checklistReminderYear));
        setAlarm.set(Calendar.MONTH, Integer.parseInt(checklistReminderMonth) - 1);
        setAlarm.set(Calendar.DATE, Integer.parseInt(checklistReminderDate));
        setAlarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(checklistReminderHour));
        setAlarm.set(Calendar.MINUTE, Integer.parseInt(checklistReminderMinute));
        setAlarm.set(Calendar.SECOND, 0);

        return setAlarm;
    }

    private void setAlarm(Calendar cal) {

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    /**
     * Display data in view components
     */
    private void displayData() {

        // check checklistRemindersData to avoid NullPointerException for a new Note
        if (checklistRemindersData != null) {

            mChecklistRemindersTitleEt.setText(checklistRemindersData.getNotesTitle());

            mAlarmButton.setText(checklistRemindersData.getNoteReminderDate() + "/" +
                    checklistRemindersData.getNoteReminderMonth() + "/" +
                    checklistRemindersData.getNoteReminderYear() + ", " +
                    checklistRemindersData.getNoteReminderHour() + ":" +
                    checklistRemindersData.getNoteReminderMinute());

            if (checklistRemindersData.getImageUri() != null) {

                this.notesImageUri = Uri.parse(checklistRemindersData.getImageUri());
                Picasso.with(getActivity())
                        .load(checklistRemindersData.getImageUri())
                        .into(mNotesImage);
            }

            for (int i = 0 ; i < checklistRemindersData.getContent().size() ; i++) {

                dataList.add(checklistRemindersData.getContent().get("content_0" + String.valueOf(i + 1)).get(Constants.HASHMAP_VALUE));
                statusList.add(checklistRemindersData.getContent().get("content_0" + String.valueOf(i + 1)).get(Constants.HASHMAP_STATUS));
            }

            this.checklistReminderYear = checklistRemindersData.getNoteReminderYear();
            this.checklistReminderMonth = checklistRemindersData.getNoteReminderMonth();
            this.checklistReminderDate = checklistRemindersData.getNoteReminderDate();
            this.checklistReminderHour = checklistRemindersData.getNoteReminderHour();
            this.checklistReminderMinute = checklistRemindersData.getNoteReminderMinute();
        }
    }

    /**
     * Gets number of children inside 'Reminders' of current user from firebase database
     */
    private void getRemindersCount() {

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
