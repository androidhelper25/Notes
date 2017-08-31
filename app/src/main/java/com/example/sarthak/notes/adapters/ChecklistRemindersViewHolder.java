package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.models.ChecklistReminders;

public class ChecklistRemindersViewHolder extends RecyclerView.ViewHolder {

    String checklistType = "ChecklistReminders";

    View itemView;

    private TextView mTitleTv,mChecklistReminderTv;
    private RecyclerView mChecklistList;

    public ChecklistRemindersViewHolder(View itemView) {
        super(itemView);

        // set up view components
        setUpView(itemView);
    }

    /**
     * Display data from model 'ChecklistReminders' in view components
     *
     * @param context is the context of the activity
     * @param checklistReminders is checklistReminders data stored as a model object
     */
    void bindData(Context context, ChecklistReminders checklistReminders) {

        ViewChecklistsRecyclerAdapter viewChecklistsRecyclerAdapter = new ViewChecklistsRecyclerAdapter(context, checklistReminders.getContent(), checklistType);

        // set up recyclerView for checklist
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mChecklistList.setLayoutManager(mLayoutManager);
        mChecklistList.setItemAnimator(new DefaultItemAnimator());
        mChecklistList.setAdapter(viewChecklistsRecyclerAdapter);

        // set notesTitle to 'Title' text view
        if (checklistReminders.getNotesTitle().equals("")) {
            mTitleTv.setHeight(0);
        } else {
            mTitleTv.setText(checklistReminders.getNotesTitle());
        }

        // set reminder date to 'Reminder' textView
        mChecklistReminderTv.setText(checklistReminders.getNoteReminderDate() + "/" +
                checklistReminders.getNoteReminderMonth() + "/" +
                checklistReminders.getNoteReminderYear() + ", " +
                checklistReminders.getNoteReminderHour() + ":" +
                checklistReminders.getNoteReminderMinute());
    }

    /**
     * Initialise view components
     */
    private void setUpView(View itemView) {

        this.itemView = itemView;
        mTitleTv = (TextView) itemView.findViewById(R.id.cardTitleTv);
        mChecklistReminderTv = (TextView) itemView.findViewById(R.id.cardReminderTv);
        mChecklistList = (RecyclerView) itemView.findViewById(R.id.cardChecklistList);
    }
}
