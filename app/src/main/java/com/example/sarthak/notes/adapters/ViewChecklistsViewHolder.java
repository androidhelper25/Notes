package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.sarthak.notes.R;

import java.util.HashMap;

public class ViewChecklistsViewHolder extends RecyclerView.ViewHolder {

    String checklistType;

    View itemView;

    private EditText mChecklistDataEt;
    private CheckBox mChecklistBox;

    public ViewChecklistsViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.mChecklistDataEt = (EditText) itemView.findViewById(R.id.checklist_data);
        this.mChecklistBox = (CheckBox) itemView.findViewById(R.id.checklist_checkbox);

        this.mChecklistDataEt.setFocusable(false);
        this.mChecklistDataEt.setClickable(false);

        this.mChecklistBox.setEnabled(false);
    }

    void bindData(HashMap<String, String> dataItem, String checklistType, Context context) {

        this.checklistType = checklistType;

        if (checklistType.equals("Checklists")) {
            mChecklistDataEt.setBackgroundColor(context.getResources().getColor(R.color.checklistsColor));
        } else if (checklistType.equals("ChecklistReminders")) {
            mChecklistDataEt.setBackgroundColor(context.getResources().getColor(R.color.checklistRemindersColor));
        }

        mChecklistDataEt.setText(dataItem.get("value"));

        if (dataItem.get("status").equals("checked")) {

            mChecklistBox.setChecked(true);
            mChecklistDataEt.setTextColor(ContextCompat.getColor(context, R.color.colorDividerLine));
        }
    }
}
