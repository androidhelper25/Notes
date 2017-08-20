package com.example.sarthak.notes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.models.NoteReminders;

public class RemindersViewHolder extends RecyclerView.ViewHolder {

    View itemView;

    private TextView mNotesTitleTv, mNotesBodyTv, mNotesReminderTv;

    public RemindersViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.mNotesTitleTv = (TextView) itemView.findViewById(R.id.cardTitleTv);
        this.mNotesBodyTv = (TextView) itemView.findViewById(R.id.cardBodyTv);
        this.mNotesReminderTv = (TextView) itemView.findViewById(R.id.cardReminderTv);
    }

    void bindData(NoteReminders reminders) {

        mNotesTitleTv.setText(reminders.getNotesTitle());
        mNotesBodyTv.setText(reminders.getNotesBody());
        mNotesReminderTv.setText(reminders.getNoteReminderDate() + "/" +
                                 reminders.getNoteReminderMonth() + "/" +
                                 reminders.getNoteReminderYear() + ", " +
                                 reminders.getNoteReminderHour() + ":" +
                                 reminders.getNoteReminderMinute());
    }
}
