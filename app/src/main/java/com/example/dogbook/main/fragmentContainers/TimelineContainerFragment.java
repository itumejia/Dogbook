package com.example.dogbook.main.fragmentContainers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dogbook.R;
import com.example.dogbook.main.fragments.TimelineFragment;

public class TimelineContainerFragment extends Fragment {

    private FragmentManager fragmentManager;

    public TimelineContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment fragment = new TimelineFragment();
        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(R.id.timelineFragmentContainer, fragment).commit();
    }
}