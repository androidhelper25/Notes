package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.fragments.TakeChecklistsFragment;
import com.example.sarthak.notes.utils.CheckListListener;
import com.example.sarthak.notes.utils.Constants;

public class TakeChecklistsViewHolder extends RecyclerView.ViewHolder{

    View itemView;

    CheckBox mCheckBox;
    EditText mDataEt;
    ImageButton mDeleteButton;

    public TakeChecklistsViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        mCheckBox = (CheckBox) itemView.findViewById(R.id.checklist_checkbox);
        mDataEt = (EditText) itemView.findViewById(R.id.checklist_data);
        mDeleteButton = (ImageButton) itemView.findViewById(R.id.checklist_delete);
    }

    /**
     * Bind data to view components
     *
     * @param context is the context of the activity
     * @param value is the value of each item in 'dataList' arraylist
     * @param status is the value of each item in 'statusList' arraylist
     */
    public void bindData(Context context, String value, String status) {

        mDataEt.setText(value);

        if (status.equals(Constants.CHECKED_STATUS)) {

            mCheckBox.setChecked(true);
            mDataEt.setTextColor(ContextCompat.getColor(context, R.color.colorDividerLine));
        }
    }
}
