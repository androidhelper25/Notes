package com.example.sarthak.notes.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sarthak.notes.activities.NotesActivity;
import com.example.sarthak.notes.models.NoteReminders;
import com.example.sarthak.notes.utils.AlarmReceiver;
import com.example.sarthak.notes.utils.BackButtonListener;
import com.example.sarthak.notes.utils.Constants;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.firebasemanager.FirebaseAuthorisation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class TakeNoteRemindersFragment extends Fragment implements
        View.OnClickListener, BackButtonListener, AdapterView.OnItemSelectedListener {

    String notesTitle = "";
    String notesBody = "";
    String noteReminderYear;
    String noteReminderMonth;
    String noteReminderDate;
    String noteReminderHour;
    String noteReminderMinute;

    int count;

    private static final String[] dayArray = {"Today", "Tomorrow", "Select any day..."};
    private static final String[] timeArray = {"After 1 hour", "After 6 hours", "After 12 hours", "Select any time..."};

    private EditText mNotesTitleEt, mNotesBodyEt;

    SharedPreferences.Editor editor;

    DatabaseReference mDatabase;

    private Button mAlarmButton;

    private Calendar cal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_note_reminders, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        editor = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).edit();

        cal = Calendar.getInstance();

        getNoteRemindersCount();

        mNotesTitleEt = (EditText) view.findViewById(R.id.noteRemindersTitle);
        mNotesBodyEt = (EditText) view.findViewById(R.id.noteRemindersBody);

        mNotesTitleEt.addTextChangedListener(titleWatcher);
        mNotesBodyEt.addTextChangedListener(bodyWatcher);

        mAlarmButton = (Button) view.findViewById(R.id.buttonAlarm);
        mAlarmButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((NotesActivity) context).backButtonListener = this;
    }

    @Override
    public void notesBackButtonPressed() {

    }

    @Override
    public void noteRemindersBackButtonPressed() {

        final SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        notesTitle = prefs.getString("title", " ");
        notesBody = prefs.getString("body", " ");

        noteReminderYear = prefs.getString("year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        noteReminderMonth = prefs.getString("month", String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
        noteReminderDate = prefs.getString("date", String.valueOf(Calendar.getInstance().get(Calendar.DATE) + 1));
        noteReminderHour = prefs.getString("hour", String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
        noteReminderMinute = prefs.getString("minute", String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));

        NoteReminders notes = new NoteReminders(notesTitle, notesBody, noteReminderYear,
                noteReminderMonth, noteReminderDate, noteReminderHour, noteReminderMinute);

        FirebaseAuthorisation firebaseAuth = new FirebaseAuthorisation(getActivity());

        int notesCount = count;

        mDatabase.child(firebaseAuth.getCurrentUser()).child("Reminders").child("Reminders_0" + String.valueOf(notesCount + 1))
                .setValue(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    if (getActivity() != null) {

                        Toast.makeText(getActivity(), "Note added.", Toast.LENGTH_SHORT).show();
                    }
                    prefs.edit().clear().apply();
                }
            }
        });
    }

    @Override
    public void checklistsBackButtonPressed() {

    }

    @Override
    public void checklistRemindersBackButtonPressed() {

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
                ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dayArray);

                dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                daySpinner.setSelection(2);
                daySpinner.setAdapter(dayAdapter);
                daySpinner.setOnItemSelectedListener(this);

                Spinner timeSpinner = (Spinner) dialogView.findViewById(R.id.timeSpinner);
                ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, timeArray);

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

    private void setAlarm(Calendar cal) {

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
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

    TextWatcher titleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            editor.putString("title", mNotesTitleEt.getText().toString());
            editor.commit();
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

            editor.putString("body", mNotesBodyEt.getText().toString());
            editor.commit();
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        Spinner spinner = (Spinner) adapterView;

        SharedPreferences.Editor edit = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).edit();

        switch (spinner.getId()) {

            case R.id.daySpinner :

                switch (position) {

                    case 0 :

                        edit.putString("year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
                        edit.putString("month", String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
                        edit.putString("date", String.valueOf(Calendar.getInstance().get(Calendar.DATE)));
                        edit.apply();
                        break;

                    case 1 :

                        edit.putString("year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
                        edit.putString("month", String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
                        edit.putString("date", String.valueOf(Calendar.getInstance().get(Calendar.DATE) + 1));
                        edit.apply();
                        break;

                    case 2 :

                        break;
                }
                break;

            case R.id.timeSpinner :

                switch (position) {

                    case 0 :

                        edit.putString("hour", String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1));
                        edit.putString("minute", String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));
                        break;

                    case 1 :

                        edit.putString("hour", String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 6));
                        edit.putString("minute", String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));
                        break;

                    case 2 :

                        edit.putString("hour", String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 12));
                        edit.putString("minute", String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));
                        break;

                    case 3 :

                        break;
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
