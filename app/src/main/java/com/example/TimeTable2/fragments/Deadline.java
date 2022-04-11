package com.example.TimeTable2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.TimeTable2.R;
import com.example.TimeTable2.views.DeadlineView;
import com.example.TimeTable2.views.FragmentView;

public class Deadline extends Fragment
{
    private DeadlineView fragmentView;

    public Deadline(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deadline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = new DeadlineView(getActivity(), view);
        fragmentView.initiateProcess("deadline");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            switch (item.getItemId()){
                case 0:
                    fragmentView.getHandleDeadlineData().deleteDeadlineData(item.getGroupId());
                    return true;
            }
        }
        return false;
    }

}
