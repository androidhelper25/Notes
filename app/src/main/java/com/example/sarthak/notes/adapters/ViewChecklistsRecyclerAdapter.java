package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarthak.notes.R;

import java.util.HashMap;

public class ViewChecklistsRecyclerAdapter extends RecyclerView.Adapter<ViewChecklistsViewHolder> {

    Context mContext;

    String checklistType;

    private HashMap<String, HashMap<String, String>> dataMap = new HashMap<>();

    private LayoutInflater mInflater;

    public ViewChecklistsRecyclerAdapter(Context context, HashMap<String, HashMap<String, String>> dataMap, String checklistType) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mContext = context;
        this.dataMap = dataMap;
        this.checklistType = checklistType;
    }

    @Override
    public ViewChecklistsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.view_checklist_item, parent, false);

        return new ViewChecklistsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewChecklistsViewHolder holder, int position) {

        // bind data to view holder
        holder.bindData(dataMap.get("content_0" + String.valueOf(position + 1)), checklistType, mContext);
    }

    @Override
    public int getItemCount() {
        return dataMap.size();
    }
}
