package com.example.sarthak.notes.utils;

import android.view.View;

public interface RemindersRecyclerViewItemClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
