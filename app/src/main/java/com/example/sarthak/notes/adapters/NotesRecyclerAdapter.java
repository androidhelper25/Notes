package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.utils.RecyclerViewItemClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    ArrayList<Notes> notesList = new ArrayList<>();

    private LayoutInflater mInflater;

    private RecyclerViewItemClickListener onRecyclerViewItemClickListener;

    DatabaseReference mDatabase;

    public NotesRecyclerAdapter(Context context, ArrayList<Notes> notesList) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.notesList = notesList;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void setOnRecyclerViewItemClickListener(RecyclerViewItemClickListener onRecyclerViewItemClickListener) {

        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.cardview_notes, parent, false);

        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {

        Notes notesItem = notesList.get(holder.getAdapterPosition());

        holder.bindData(notesItem);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
