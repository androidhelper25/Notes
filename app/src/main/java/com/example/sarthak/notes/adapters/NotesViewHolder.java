package com.example.sarthak.notes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.R;

class NotesViewHolder extends RecyclerView.ViewHolder{

    View itemView;

    private TextView mNotesTitleTv, mNotesBodyTv;

    public NotesViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.mNotesTitleTv = (TextView) itemView.findViewById(R.id.cardTitleTv);
        this.mNotesBodyTv = (TextView) itemView.findViewById(R.id.cardBodyTv);
    }

    void bindData(Notes notes) {

        mNotesTitleTv.setText(notes.getNotesTitle());
        mNotesBodyTv.setText(notes.getNotesBody());
    }
}
