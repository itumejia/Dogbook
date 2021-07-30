package com.example.dogbook.main.fragments;

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

import com.baoyz.widget.PullRefreshLayout;
import com.example.dogbook.common.ParseApplication;
import com.example.dogbook.main.adapters.PostsAdapter;
import com.example.dogbook.main.models.Post;
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
    private PullRefreshLayout ptrContainer;

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
        setUpRecyclerView();
        ParseApplication.functionTry();
    }

    private void initView(View view) {
        ptrContainer = view.findViewById(R.id.ptrContainer);
        ptrContainer.setColor(R.color.main_color);
        ptrContainer.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        rvPosts = view.findViewById(R.id.rvPosts);
        posts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), posts);
        ptrContainer.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPosts();
            }
        });
    }

    private void setUpRecyclerView() {
        rvPosts.setAdapter(postsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);
        refreshPosts();
    }


    public void refreshPosts() {
        ParseQuery<Post> query = ParseApplication.getAllPostsQuery();
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts.clear();
                    posts.addAll(objects);
                    postsAdapter.notifyDataSetChanged();
                    rvPosts.smoothScrollToPosition(0);
                    ptrContainer.setRefreshing(false);

                    return;
                }
                Log.e(TAG, "Issue finding posts in Parse", e);
                Toast.makeText(getContext(), "Unable to refresh posts", Toast.LENGTH_SHORT).show();
                ptrContainer.setRefreshing(false);
            }
        });
    }
}