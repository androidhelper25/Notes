package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.models.NoteReminders;
import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.utils.RecyclerViewItemClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RemindersRecyclerAdapter extends RecyclerView.Adapter<RemindersViewHolder> {

    ArrayList<NoteReminders> noteRemindersList = new ArrayList<>();

    private LayoutInflater mInflater;

    private RecyclerViewItemClickListener onRecyclerViewItemClickListener;

    DatabaseReference mDatabase;

    public RemindersRecyclerAdapter(Context context, ArrayList<NoteReminders> noteRemindersList) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.noteRemindersList = noteRemindersList;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void setOnRecyclerViewItemClickListener(RecyclerViewItemClickListener onRecyclerViewItemClickListener) {

        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public RemindersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.cardview_note_reminders, parent, false);

        return new RemindersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RemindersViewHolder holder, int position) {

        NoteReminders noteRemindersItem = noteRemindersList.get(holder.getAdapterPosition());

        holder.bindData(noteRemindersItem);
    }

    @Override
    public int getItemCount() {
        return noteRemindersList.size();
    }
}
