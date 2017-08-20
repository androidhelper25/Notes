package com.example.sarthak.notes.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.adapters.TakeChecklistsRecyclerAdapter;
import com.example.sarthak.notes.utils.CheckListListener;

import java.util.ArrayList;

public class TakeChecklistsFragment extends Fragment implements CheckListListener {

    private ArrayList<String> dataList = new ArrayList<>();

    private RecyclerView mChecklistList;
    private TakeChecklistsRecyclerAdapter takeChecklistsRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_checklists, container, false);

        mChecklistList = (RecyclerView) view.findViewById(R.id.checklistList);

        takeChecklistsRecyclerAdapter = new TakeChecklistsRecyclerAdapter(getActivity(), dataList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mChecklistList.setLayoutManager(mLayoutManager);
        mChecklistList.setItemAnimator(new DefaultItemAnimator());
        mChecklistList.setAdapter(takeChecklistsRecyclerAdapter);

        return view;
    }

    @Override
    public void enterKeyPressed(String string) {

        dataList.add(string);
        takeChecklistsRecyclerAdapter.notifyDataSetChanged();
    }
}
