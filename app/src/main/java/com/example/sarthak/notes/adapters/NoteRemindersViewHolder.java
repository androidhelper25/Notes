package com.example.sarthak.notes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.models.NoteReminders;

public class NoteRemindersViewHolder extends RecyclerView.ViewHolder {

    View itemView;

    String updatedMinute, updatedHour;

    private TextView mNotesTitleTv, mNotesBodyTv, mNotesReminderTv;

    public NoteRemindersViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.mNotesTitleTv = (TextView) itemView.findViewById(R.id.cardTitleTv);
        this.mNotesBodyTv = (TextView) itemView.findViewById(R.id.cardBodyTv);
        this.mNotesReminderTv = (TextView) itemView.findViewById(R.id.cardReminderTv);
    }

    /**
     * Display data from model 'NoteReminders' in view components
     *
     * @param reminders is noteReminders data stored as a model object
     */
    void bindData(NoteReminders reminders) {

        // set notesTitle to 'Title' text view
        if (reminders.getNotesTitle().equals("")) {
            mNotesTitleTv.setHeight(0);
        } else {
            mNotesTitleTv.setText(reminders.getNotesTitle());
        }

        // set notesBody to 'Body' text view
        mNotesBodyTv.setText(reminders.getNotesBody());

        if (Integer.parseInt(reminders.getNoteReminderMinute()) < 10) {
            updatedMinute = "0" + reminders.getNoteReminderMinute();
        } else {
            updatedMinute = reminders.getNoteReminderMinute();
        }

        if (Integer.parseInt(reminders.getNoteReminderHour()) < 10) {
            updatedHour = "0" + reminders.getNoteReminderHour();
        } else {
            updatedHour = reminders.getNoteReminderHour();
        }

        // set reminder date to 'Reminder' textView
        mNotesReminderTv.setText(reminders.getNoteReminderDate() + "/" +
                                 reminders.getNoteReminderMonth() + "/" +
                                 reminders.getNoteReminderYear() + ", " +
                                 updatedHour + ":" + updatedMinute);
    }
}
