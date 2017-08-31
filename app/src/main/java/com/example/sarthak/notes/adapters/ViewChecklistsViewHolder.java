package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.utils.Constants;

import java.util.HashMap;

public class ViewChecklistsViewHolder extends RecyclerView.ViewHolder {

    String checklistType;

    View itemView;

    private RelativeLayout mLayout;
    private EditText mChecklistDataEt;
    private CheckBox mChecklistBox;
    private ImageButton mCloseButton;

    public ViewChecklistsViewHolder(View itemView) {
        super(itemView);

        // set up view components
        setUpView(itemView);
    }

    /**
     * Bind data to individual view holder
     *
     * @param dataItem is the hashMap containing 'value' and 'status' for each item in arraylist
     * @param checklistType is the type of checklist to distinguish setting background color
     * @param context is the context of the activity
     */
    void bindData(HashMap<String, String> dataItem, String checklistType, Context context) {

        this.checklistType = checklistType;

        // set editText background color
        if (checklistType.equals("Checklists")) {
            mChecklistDataEt.setBackgroundColor(context.getResources().getColor(R.color.checklistsColor));
        } else if (checklistType.equals("ChecklistReminders")) {
            mChecklistDataEt.setBackgroundColor(context.getResources().getColor(R.color.checklistRemindersColor));
        }

        // set data from arraylist to individual items in checklist
        mChecklistDataEt.setText(dataItem.get(Constants.HASHMAP_VALUE));

        // configure checkBox based on 'status' of dataItem
        if (dataItem.get(Constants.HASHMAP_STATUS).equals(Constants.CHECKED_STATUS)) {

            mChecklistBox.setChecked(true);
            mChecklistDataEt.setTextColor(ContextCompat.getColor(context, R.color.colorDividerLine));
        }
    }

    /**
     * Initialise view components
     */
    private void setUpView(View itemView) {

        this.itemView = itemView;
        this.mLayout = (RelativeLayout) itemView.findViewById(R.id.card_layout);
        this.mChecklistDataEt = (EditText) itemView.findViewById(R.id.checklist_data);
        this.mChecklistBox = (CheckBox) itemView.findViewById(R.id.checklist_checkbox);
        this.mCloseButton = (ImageButton) itemView.findViewById(R.id.checklist_delete);

        this.mLayout.setPadding(2, 2, 2, 2);

        this.mChecklistDataEt.setFocusable(false);
        this.mChecklistDataEt.setClickable(false);
        this.mChecklistDataEt.setTextSize(16);

        this.mChecklistBox.setEnabled(false);

        this.mCloseButton.setVisibility(View.INVISIBLE);
    }
}
