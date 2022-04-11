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
import com.example.TimeTable2.views.FragmentView;

public class Saturday extends Fragment {

    private FragmentView fragmentView;

    public Saturday(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saturday, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = new FragmentView(getActivity(), view);
        fragmentView.initiateProcess("saturday");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            switch (item.getItemId()){
                case 0:
                    fragmentView.getHandleTimetableData().deleteTimetableData(item.getGroupId());
                    return true;
            }
        }
        return false;
    }
}
