package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarthak.notes.models.Checklists;
import com.example.sarthak.notes.models.Notes;
import com.example.sarthak.notes.R;
import com.example.sarthak.notes.utils.Constants;
import com.example.sarthak.notes.utils.NotesRecyclerViewItemClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private final int NOTES = 0;
    private final int CHECKLISTS = 1;

    ArrayList<Object> notesList = new ArrayList<>();
    ArrayList<String> noteType = new ArrayList<>();

    private LayoutInflater mInflater;

    private NotesRecyclerViewItemClickListener onNotesRecyclerViewItemClickListener;

    DatabaseReference mDatabase;

    public NotesRecyclerAdapter(Context context, ArrayList<Object> notesList, ArrayList<String> typeOfNote) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mContext = context;
        this.notesList = notesList;
        this.noteType = typeOfNote;

        // set up an instance of firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_REFERENCE);
    }

    public void setOnRecyclerViewItemClickListener(NotesRecyclerViewItemClickListener onNotesRecyclerViewItemClickListener) {

        this.onNotesRecyclerViewItemClickListener = onNotesRecyclerViewItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {

        if (noteType.get(position).equals(Constants.TYPE_NOTES)) {

            return NOTES;
        } else if (noteType.get(position).equals(Constants.TYPE_CHECKLISTS)) {

            return CHECKLISTS;
        }

        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {

            case NOTES :

                View notesView = mInflater.inflate(R.layout.cardview_notes, parent, false);
                viewHolder = new NotesViewHolder(notesView);
                break;

            case CHECKLISTS :

                View checklistsView = mInflater.inflate(R.layout.cardview_checklists, parent, false);
                viewHolder = new ChecklistsViewHolder(checklistsView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case NOTES :

                NotesViewHolder notesViewHolder = (NotesViewHolder) holder;
                Notes notesItem = (Notes) notesList.get(holder.getAdapterPosition());
                notesViewHolder.bindData(notesItem);

                notesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // set up listener for recycler view item click
                        // callback in NotesFragment
                        onNotesRecyclerViewItemClickListener.onClick(view, holder.getAdapterPosition());
                    }
                });
                break;

            case CHECKLISTS :

                ChecklistsViewHolder checklistsViewHolder = (ChecklistsViewHolder) holder;
                Checklists checklistsItem = (Checklists) notesList.get(holder.getAdapterPosition());
                checklistsViewHolder.bindData(mContext, checklistsItem);

                checklistsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // set up listener for recycler view item click
                        // callback in NotesFragment
                        onNotesRecyclerViewItemClickListener.onClick(view, holder.getAdapterPosition());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
