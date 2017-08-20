package com.example.sarthak.notes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.sarthak.notes.R;

public class TakeChecklistsViewHolder extends RecyclerView.ViewHolder{

    private View itemView;

    private CheckBox mCheckBox;
    EditText mDataEt;
    private ImageButton mDeleteButton;

    public TakeChecklistsViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        mCheckBox = (CheckBox) itemView.findViewById(R.id.checklist_checkbox);
        mDataEt = (EditText) itemView.findViewById(R.id.checklist_data);
        mDeleteButton = (ImageButton) itemView.findViewById(R.id.checklist_delete);
    }
}
