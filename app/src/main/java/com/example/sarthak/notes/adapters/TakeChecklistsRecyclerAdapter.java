package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.utils.CheckListListener;

import java.util.ArrayList;

public class TakeChecklistsRecyclerAdapter extends RecyclerView.Adapter<TakeChecklistsViewHolder> {

    Context mContext;

    String dataItem;
    String checklistListenerContext;

    ArrayList<String> checklistList = new ArrayList<>();
    ArrayList<String> statusList = new ArrayList<>();

    private LayoutInflater mInflater;

    CheckListListener checkListListener;

    public TakeChecklistsRecyclerAdapter(Context context, ArrayList<String> checklistList, ArrayList<String> statusList, CheckListListener checkListListener, String checklistListenerContext) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mContext = context;
        this.checklistList = checklistList;
        this.statusList = statusList;
        this.checkListListener = checkListListener;
        this.checklistListenerContext = checklistListenerContext;
    }

    @Override
    public TakeChecklistsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.cardview_take_checklist, parent, false);

        return new TakeChecklistsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TakeChecklistsViewHolder holder, int position) {

        if (position < checklistList.size()) {

            Log.e("poa", String.valueOf(position));
            holder.bindData(mContext, checklistList.get(holder.getAdapterPosition()), statusList.get(holder.getAdapterPosition()));
        } else {

            holder.mDataEt.requestFocus();
            holder.bindData(mContext, "", "unchecked");
        }

        holder.mDataEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int id, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (id == KeyEvent.KEYCODE_ENTER)) {

                    dataItem = holder.mDataEt.getText().toString();

                    if (!dataItem.equals("")) {

                        if (checklistListenerContext.equals("checklists")) {

                            if (holder.mCheckBox.isChecked()) {
                                checkListListener.checklistEnterKeyPressed(dataItem, "checked", holder.getAdapterPosition());
                            } else {
                                checkListListener.checklistEnterKeyPressed(dataItem, "unchecked", holder.getAdapterPosition());
                            }
                        } else if (checklistListenerContext.equals("checklistReminders")) {

                            if (holder.mCheckBox.isChecked()) {
                                checkListListener.checklistReminderEnterKeyPressed(dataItem, "checked");
                            } else {
                                checkListListener.checklistReminderEnterKeyPressed(dataItem, "unchecked");
                            }}
                    }
                }

                return false;
            }
        });

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {

                    holder.mDataEt.setTextColor(ContextCompat.getColor(mContext, R.color.colorDividerLine));
                } else {

                    holder.mDataEt.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondaryText));
                }

                checkListListener.checklistCheckboxChecked(isChecked, holder.getAdapterPosition());
                checkListListener.checklistReminderCheckboxChecked(isChecked, holder.getAdapterPosition());
            }
        });

        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkListListener.checklistDeleteButtonPressed(holder.getAdapterPosition());
                checkListListener.checklistReminderDeleteButtonPressed(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return checklistList.size() + 1;
    }
}
