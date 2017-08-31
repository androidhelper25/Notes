package com.example.sarthak.notes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.models.NoteReminders;

public class NoteRemindersViewHolder extends RecyclerView.ViewHolder {

    View itemView;

    private TextView mNotesTitleTv, mNotesBodyTv, mNotesReminderTv;

    public NoteRemindersViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.mNotesTitleTv = (TextView) itemView.findViewById(R.id.cardTitleTv);
        this.mNotesBodyTv = (TextView) itemView.findViewById(R.id.cardBodyTv);
        this.mNotesReminderTv = (TextView) itemView.findViewById(R.id.cardReminderTv);
    }

    void bindData(NoteReminders reminders) {

        if (reminders.getNotesTitle().equals("")) {
            mNotesTitleTv.setHeight(0);
        } else {
            mNotesTitleTv.setText(reminders.getNotesTitle());
        }
        mNotesBodyTv.setText(reminders.getNotesBody());
        mNotesReminderTv.setText(reminders.getNoteReminderDate() + "/" +
                                 reminders.getNoteReminderMonth() + "/" +
                                 reminders.getNoteReminderYear() + ", " +
                                 reminders.getNoteReminderHour() + ":" +
                                 reminders.getNoteReminderMinute());
    }
}
