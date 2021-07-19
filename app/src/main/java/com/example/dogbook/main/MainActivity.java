package com.example.dogbook.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dogbook.compose.ComposeActivity;
import com.example.dogbook.login.LoginActivity;
import com.example.dogbook.main.fragments.MapFragment;
import com.example.dogbook.main.fragments.TimelineFragment;
import com.example.dogbook.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_COMPOSE_ACTIVITY = 2905;
    private Toolbar toolbar;
    private BottomNavigationView navigationBar;
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
        navigationBar = findViewById(R.id.navigationBar);
        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.item_home:
                        fragment = new TimelineFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                        return true;

                    case R.id.item_dogs_nearby:
                        fragment = new MapFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                        return true;

                    default:
                        fragment = new TimelineFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
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
        if (requestCode == REQUEST_CODE_COMPOSE_ACTIVITY && resultCode == RESULT_OK && isFragmentContainerATimelineFragment()) {
            navigationBar.setSelectedItemId(R.id.item_home);
        }
    }

    private boolean isFragmentContainerATimelineFragment(){
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof TimelineFragment) {
            return true;
        }
        return false;
    }
}