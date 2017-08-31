package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.models.Checklists;

public class ChecklistsViewHolder extends RecyclerView.ViewHolder {

    String checklistType = "Checklists";

    View itemView;

    private TextView mTitleTv;
    private RecyclerView mChecklistList;

    public ChecklistsViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.mTitleTv = (TextView) itemView.findViewById(R.id.cardTitleTv);
        this.mChecklistList = (RecyclerView) itemView.findViewById(R.id.cardChecklistList);
    }

    /**
     * Display data from model 'Checklists' in view components
     *
     * @param context is the context of the activity
     * @param checklists is checklists data stored as a model object
     */
    void bindData(Context context, Checklists checklists) {

        ViewChecklistsRecyclerAdapter viewChecklistsRecyclerAdapter = new ViewChecklistsRecyclerAdapter(context, checklists.getContent(), checklistType);

        // set up recyclerView for checklist
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mChecklistList.setLayoutManager(mLayoutManager);
        mChecklistList.setItemAnimator(new DefaultItemAnimator());
        mChecklistList.setAdapter(viewChecklistsRecyclerAdapter);

        // set notesTitle to 'Title' text view
        if (checklists.getNotesTitle().equals("")) {
            mTitleTv.setHeight(0);
        } else {
            mTitleTv.setText(checklists.getNotesTitle());
        }
    }
}
