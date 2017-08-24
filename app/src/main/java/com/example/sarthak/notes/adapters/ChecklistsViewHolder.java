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

    void bindData(Context context, Checklists checklists) {

        ViewChecklistsRecyclerAdapter viewChecklistsRecyclerAdapter = new ViewChecklistsRecyclerAdapter(context, checklists.getContent(), checklistType);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mChecklistList.setLayoutManager(mLayoutManager);
        mChecklistList.setItemAnimator(new DefaultItemAnimator());
        mChecklistList.setAdapter(viewChecklistsRecyclerAdapter);

        mTitleTv.setText(checklists.getNotesTitle());
    }
}
