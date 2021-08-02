package com.example.dogbook.main.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dogbook.main.fragmentContainers.MapContainerFragment;
import com.example.dogbook.main.fragmentContainers.TimelineContainerFragment;
import com.example.dogbook.main.fragments.TimelineFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;
    public static final int TIMELINE_PAGE = 0;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
            default:
                return new TimelineContainerFragment();
            case 1:
                return new MapContainerFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }



}
