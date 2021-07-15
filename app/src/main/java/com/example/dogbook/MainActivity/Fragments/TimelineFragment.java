package com.example.dogbook.MainActivity.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dogbook.MainActivity.Adapters.PostsAdapter;
import com.example.dogbook.MainActivity.Models.Post;
import com.example.dogbook.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment {

    private static final String TAG = "TimelineFragment";

    private RecyclerView rvPosts;
    private RecyclerView.Adapter postsAdapter;
    private List<Post> posts;


    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

    }

    private void initView(View view) {
        rvPosts = view.findViewById(R.id.rvPosts);
        posts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), posts);
        rvPosts.setAdapter(postsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);
        refreshPosts();
    }

    private void refreshPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(20);
        query.orderByDescending("createdAt");
        query.include("author");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts.clear();
                    posts.addAll(objects);
                    postsAdapter.notifyDataSetChanged();
                }
                else {
                    Log.e(TAG, "Issue finding posts in Parse", e);
                    Toast.makeText(getContext(), "Unable to refresh posts", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}