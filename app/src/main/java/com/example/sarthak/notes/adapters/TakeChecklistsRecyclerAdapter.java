package com.example.sarthak.notes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.fragments.TakeChecklistsFragment;
import com.example.sarthak.notes.utils.CheckListListener;

import java.util.ArrayList;

public class TakeChecklistsRecyclerAdapter extends RecyclerView.Adapter<TakeChecklistsViewHolder> {

    Context mContext;

    String dataItem;

    ArrayList<String> checklistList = new ArrayList<>();

    private LayoutInflater mInflater;

    CheckListListener checkListListener;

    public TakeChecklistsRecyclerAdapter(Context context, ArrayList<String> checklistList, CheckListListener checkListListener) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mContext = context;
        this.checklistList = checklistList;
        this.checkListListener = checkListListener;

    }

    @Override
    public TakeChecklistsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.cardview_checklist, parent, false);

        return new TakeChecklistsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TakeChecklistsViewHolder holder, int position) {

        holder.mDataEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int id, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (id == KeyEvent.KEYCODE_ENTER)) {

                    dataItem = holder.mDataEt.getText().toString();

                    if (!dataItem.equals("")) checkListListener.enterKeyPressed(dataItem);
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return checklistList.size() + 1;
    }
}
