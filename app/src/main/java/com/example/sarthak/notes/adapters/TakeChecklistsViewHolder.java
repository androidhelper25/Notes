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

public class TakeChecklistsViewHolder extends RecyclerView.ViewHolder{

    View itemView;

    CheckBox mCheckBox;
    EditText mDataEt;
    ImageButton mDeleteButton;

    CheckListListener checkListListener;

    public TakeChecklistsViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        mCheckBox = (CheckBox) itemView.findViewById(R.id.checklist_checkbox);
        mDataEt = (EditText) itemView.findViewById(R.id.checklist_data);
        mDeleteButton = (ImageButton) itemView.findViewById(R.id.checklist_delete);

        checkListListener = new TakeChecklistsFragment();
    }

    public void bindData(Context context, String value, String status) {

        mDataEt.setText(value);

        if (status.equals("checked")) {

            mCheckBox.setChecked(true);
            mDataEt.setTextColor(ContextCompat.getColor(context, R.color.colorDividerLine));
        }
    }
}
