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

    // since ViewChecklistsViewHolder is used by both ChecklistsViewHolder and ChecklistRemindersViewHolder,
    // checklistType is used to distinguish between the two.
    // It is passed as an argument to ViewChecklistsAdapter.
    String checklistType = "ChecklistReminders";

    String updatedMinute, updatedHour;

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

        // display minute with 0 if minute is less than 10
        // done to enhance UI
        if (Integer.parseInt(checklistReminders.getNoteReminderMinute()) < 10) {
            updatedMinute = "0" + checklistReminders.getNoteReminderMinute();
        } else {
            updatedMinute = checklistReminders.getNoteReminderMinute();
        }
        // display hour with 0 if hour is less than 10
        // done to enhance UI
        if (Integer.parseInt(checklistReminders.getNoteReminderHour()) < 10) {
            updatedHour = "0" + checklistReminders.getNoteReminderHour();
        } else {
            updatedHour = checklistReminders.getNoteReminderHour();
        }

        // set reminder date to 'Reminder' textView
        mChecklistReminderTv.setText(checklistReminders.getNoteReminderDate() + "/" +
                checklistReminders.getNoteReminderMonth() + "/" +
                checklistReminders.getNoteReminderYear() + ", " +
                updatedHour + ":" + updatedMinute);
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
