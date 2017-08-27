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
import com.example.sarthak.notes.models.ChecklistReminders;
import com.example.sarthak.notes.utils.AlarmReceiver;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TakeChecklistRemindersFragment extends Fragment implements
        CheckListListener, BackButtonListener, SetImageListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener {

    boolean checkboxStatus = false;

    String checklistRemindersTitle = " ";
    String checklistReminderYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    String checklistReminderMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
    String checklistReminderDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE) + 1);
    String checklistReminderHour = String.valueOf(20);
    String checklistReminderMinute = String.valueOf(0);

    String checklistListenerContext = "checklistReminders";

    int count, notesPosition;

    private Uri notesImageUri;

    private static final String[] dayArray = {"Today", "Tomorrow", "Select any day..."};
    private static final String[] timeArray = {"After 1 hour", "After 6 hours", "After 12 hours", "Select any time..."};

    private ArrayList<String> dataList = new ArrayList<>();
    private ArrayList<String> statusList = new ArrayList<>();

    ChecklistReminders checklistRemindersData = new ChecklistReminders();

    private EditText mChecklistRemindersTitleEt;
    private ImageView mNotesImage;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private Button mAlarmButton;

    private TakeChecklistsRecyclerAdapter takeChecklistsRecyclerAdapter;

    DatabaseReference mDatabase;
    StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_checklist_reminders, container, false);

        // Set title bar
        ((NotesActivity) getActivity()).getSupportActionBar().setTitle(R.string.reminders);

        notesPosition = getArguments().getInt("position");
        checklistRemindersData = (ChecklistReminders) getArguments().getSerializable("notes");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference();

        setUpView(view);
        setUpDateTimePicker();

        getRemindersCount();

        RecyclerView mChecklistRemindersList = (RecyclerView) view.findViewById(R.id.checklistList);
        takeChecklistsRecyclerAdapter = new TakeChecklistsRecyclerAdapter(getActivity(), dataList, statusList, this, checklistListenerContext);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mChecklistRemindersList.setLayoutManager(mLayoutManager);
        mChecklistRemindersList.setItemAnimator(new DefaultItemAnimator());
        mChecklistRemindersList.setAdapter(takeChecklistsRecyclerAdapter);

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
    // checkList component's click listeners
    //----------------------------------------------------------------------------------
    @Override
    public void checklistEnterKeyPressed(String string, String s) {

    }

    @Override
    public void checklistReminderEnterKeyPressed(String data, String status) {

        dataList.add(data);
        statusList.add(status);
        takeChecklistsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void checklistCheckboxChecked(boolean b, int i) {

    }

    @Override
    public void checklistReminderCheckboxChecked(boolean status, int position) {

        checkboxStatus = status;

        if (position < statusList.size()) {

            if (status) {
                statusList.set(position, "checked");
            } else {
                statusList.set(position, "unchecked");
            }
        }
    }

    @Override
    public void checklistDeleteButtonPressed(int position) {

        dataList.remove(position);
        statusList.remove(position);
        takeChecklistsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void checklistReminderDeleteButtonPressed(int position) {

    }

    //----------------------------------------------------------------------------------
    // editText textChanged listener
    //----------------------------------------------------------------------------------
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

    //----------------------------------------------------------------------------------
    // spinner itemSelected listener
    //----------------------------------------------------------------------------------
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

        checklistReminderYear = String.valueOf(year);
        checklistReminderMonth = String.valueOf(month + 1);
        checklistReminderDate = String.valueOf(date);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        checklistReminderHour = String.valueOf(hour);
        checklistReminderMinute = String.valueOf(minute);
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
        final String currentUser = firebaseAuth.getCurrentUser();

        if (checklistRemindersData != null) {

            notesDatabase = mDatabase.child(currentUser).child("Reminders").child("Reminders_0" + String.valueOf(notesPosition));
            imageStorage = mStorage.child(currentUser).child("Reminders").child("Reminders_0" + String.valueOf(notesPosition) + ".jpg");
        } else {

            int notesCount = count;

            notesDatabase = mDatabase.child(currentUser).child("Reminders").child("Reminders_0" + String.valueOf(notesCount + 1));
            imageStorage = mStorage.child(currentUser).child("Reminders").child("Reminders_0" + String.valueOf(notesCount + 1) + ".jpg");
        }

        if (!(checklistRemindersTitle.equals(" ") && dataList.isEmpty())) {

            final HashMap<String, HashMap<String, String>> dataMap = new HashMap<>();

            for (int i = 0; i < dataList.size(); i++) {

                HashMap<String, String> contentMap = new HashMap<>();
                contentMap.put("value", dataList.get(i));
                contentMap.put("status", statusList.get(i));

                dataMap.put("content_0" + String.valueOf(i + 1), contentMap);

                if (checkboxStatus) {
                    statusList.add("checked");
                } else {
                    statusList.add("unchecked");
                }
            }

            ChecklistReminders checklistReminders = new ChecklistReminders(checklistRemindersTitle, dataMap,
                    checklistReminderYear, checklistReminderMonth, checklistReminderDate, checklistReminderHour, checklistReminderMinute);

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

                notesDatabase.setValue(checklistReminders).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        mChecklistRemindersTitleEt = (EditText) view.findViewById(R.id.checklistsTitle);
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

        if (checklistRemindersData != null) {

            mChecklistRemindersTitleEt.setText(checklistRemindersData.getNotesTitle());

            for (int i = 0 ; i < checklistRemindersData.getContent().size() ; i++) {

                dataList.add(checklistRemindersData.getContent().get("content_0" + String.valueOf(i + 1)).get("value"));
                statusList.add(checklistRemindersData.getContent().get("content_0" + String.valueOf(i + 1)).get("status"));
            }

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
        }
    }

    /**
     * Gets number of children inside 'Notes' of current user from firebase database
     */
    private void getRemindersCount() {

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
