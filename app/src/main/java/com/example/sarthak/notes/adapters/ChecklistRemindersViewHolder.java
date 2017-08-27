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

        this.itemView = itemView;
        this.mTitleTv = (TextView) itemView.findViewById(R.id.cardTitleTv);
        this.mChecklistReminderTv = (TextView) itemView.findViewById(R.id.cardReminderTv);

        this.mChecklistList = (RecyclerView) itemView.findViewById(R.id.cardChecklistList);
    }

    void bindData(Context context, ChecklistReminders checklistReminders) {

        ViewChecklistsRecyclerAdapter viewChecklistsRecyclerAdapter = new ViewChecklistsRecyclerAdapter(context, checklistReminders.getContent(), checklistType);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mChecklistList.setLayoutManager(mLayoutManager);
        mChecklistList.setItemAnimator(new DefaultItemAnimator());
        mChecklistList.setAdapter(viewChecklistsRecyclerAdapter);

        if (checklistReminders.getNotesTitle().equals("")) {
            mTitleTv.setHeight(0);
        } else {
            mTitleTv.setText(checklistReminders.getNotesTitle());
        }
        mChecklistReminderTv.setText(checklistReminders.getNoteReminderDate() + "/" +
                checklistReminders.getNoteReminderMonth() + "/" +
                checklistReminders.getNoteReminderYear() + ", " +
                checklistReminders.getNoteReminderHour() + ":" +
                checklistReminders.getNoteReminderMinute());
    }
}
