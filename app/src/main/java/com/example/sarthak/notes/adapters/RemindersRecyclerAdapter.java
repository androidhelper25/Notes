package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.models.ChecklistReminders;
import com.example.sarthak.notes.models.NoteReminders;
import com.example.sarthak.notes.utils.RemindersRecyclerViewItemClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RemindersRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private final int NOTES = 0;
    private final int CHECKLISTS = 1;

    ArrayList<Object> remindersList = new ArrayList<>();
    ArrayList<String> noteType = new ArrayList<>();

    private LayoutInflater mInflater;

    private RemindersRecyclerViewItemClickListener onRemindersRecyclerViewItemClickListener;

    DatabaseReference mDatabase;

    public RemindersRecyclerAdapter(Context context, ArrayList<Object> remindersList, ArrayList<String> typeOfNote) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mContext = context;
        this.remindersList = remindersList;
        this.noteType = typeOfNote;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void setOnRecyclerViewItemClickListener(RemindersRecyclerViewItemClickListener onRemindersRecyclerViewItemClickListener) {

        this.onRemindersRecyclerViewItemClickListener = onRemindersRecyclerViewItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {

        if (noteType.get(position).equals("Notes")) {

            return NOTES;
        } else if (noteType.get(position).equals("Checklists")) {

            return CHECKLISTS;
        }

        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {

            case NOTES :

                View noteRemindersView = mInflater.inflate(R.layout.cardview_note_reminders, parent, false);
                viewHolder = new RemindersViewHolder(noteRemindersView);
                break;

            case CHECKLISTS :

                View checklistRemindersView = mInflater.inflate(R.layout.cardview_checklist_reminders, parent, false);
                viewHolder = new ChecklistRemindersViewHolder(checklistRemindersView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case NOTES :

                RemindersViewHolder remindersViewHolder = (RemindersViewHolder) holder;
                NoteReminders noteRemindersItem = (NoteReminders) remindersList.get(holder.getAdapterPosition());
                remindersViewHolder.bindData(noteRemindersItem);

                remindersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onRemindersRecyclerViewItemClickListener.onClick(view , holder.getAdapterPosition());
                    }
                });
                break;

            case CHECKLISTS :

                ChecklistRemindersViewHolder checklistRemindersViewHolder = (ChecklistRemindersViewHolder) holder;
                ChecklistReminders checklistRemindersItem = (ChecklistReminders) remindersList.get(holder.getAdapterPosition());
                checklistRemindersViewHolder.bindData(mContext, checklistRemindersItem);

                checklistRemindersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onRemindersRecyclerViewItemClickListener.onClick(view, holder.getAdapterPosition());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return remindersList.size();
    }
}
