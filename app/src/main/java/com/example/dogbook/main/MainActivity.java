package com.example.dogbook.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.dogbook.compose.ComposeActivity;
import com.example.dogbook.login.LoginActivity;
import com.example.dogbook.main.adapters.ViewPagerAdapter;
import com.example.dogbook.main.fragments.MapContainerFragment;
import com.example.dogbook.main.fragments.MapFragment;
import com.example.dogbook.main.fragments.TimelineFragment;
import com.example.dogbook.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_COMPOSE_ACTIVITY = 2905;
    private Toolbar toolbar;
    private BottomNavigationView navigationBar;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        viewPager.setUserInputEnabled(true);
                        navigationBar.setSelectedItemId(R.id.item_home);
                        return;
                    case 1:
                        viewPager.setUserInputEnabled(false);
                        navigationBar.setSelectedItemId(R.id.item_dogs_nearby);
                        return;
                    default:
                        viewPager.setUserInputEnabled(true);
                        navigationBar.setSelectedItemId(R.id.item_my_profile);
                        return;

                }
            }
        });


        navigationBar = findViewById(R.id.navigationBar);
        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.item_home:
                        viewPager.setCurrentItem(0);
                        return true;

                    case R.id.item_dogs_nearby:
                        viewPager.setCurrentItem(1);
                        return true;

                    default:
                        viewPager.setCurrentItem(2);
                        return true;

                }
            }
        });
        navigationBar.setSelectedItemId(R.id.item_home); //Display home when initialized

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.menuLogOut):
                ParseUser.logOutInBackground();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case (R.id.menuCompose):
                intent = new Intent(this, ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_COMPOSE_ACTIVITY);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //A new post was composed
        if (requestCode == REQUEST_CODE_COMPOSE_ACTIVITY && resultCode == RESULT_OK) {
            navigationBar.setSelectedItemId(R.id.item_home);
            updateTimeline();
        }
    }

    private void updateTimeline() {
        TimelineFragment timelineFragment = (TimelineFragment) fragmentManager.findFragmentByTag("f" + ViewPagerAdapter.TIMELINE_PAGE);
        timelineFragment.refreshPosts();
        pagerAdapter.notifyItemChanged(ViewPagerAdapter.TIMELINE_PAGE);
    }
}