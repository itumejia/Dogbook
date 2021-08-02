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
import com.example.dogbook.main.fragments.MapFragment;

//This class was made because from the map fragment, we can navigate to the Post Details View fragment, and it is not possible to replace the fragment
//of a ViewPager. That's why we use a nested fragment so that we can change this container when navigating to the Post Details View
public class MapContainerFragment extends Fragment {

    private FragmentManager fragmentManager;
    private static final String MAP_FRAGMENT_TAG = "mapFragment";

    public MapContainerFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment fragment = new MapFragment();
        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(R.id.mapFragmentContainer, fragment, MAP_FRAGMENT_TAG).commit();
    }
}