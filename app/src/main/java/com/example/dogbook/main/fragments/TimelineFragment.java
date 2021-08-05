package com.example.dogbook.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.example.dogbook.main.adapters.PostsAdapter;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimelineFragment extends Fragment {

    private static final String TAG = "TimelineFragment";

    private FragmentManager fragmentManager;
    private RecyclerView rvPosts;
    private PostsAdapter postsAdapter;
    private List<Post> posts = new ArrayList<>();
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
        if (posts.size() == 0) {
            refreshPosts();
        }
    }

    private void initView(View view) {
        fragmentManager = getParentFragmentManager();
        ptrContainer = view.findViewById(R.id.ptrContainer);
        ptrContainer.setColor(R.color.main_color);
        ptrContainer.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        rvPosts = view.findViewById(R.id.rvPosts);
        ptrContainer.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPosts();
            }
        });
    }

    private void setUpRecyclerView() {
        postsAdapter = new PostsAdapter(getContext(), fragmentManager, posts);
        rvPosts.setAdapter(postsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);
    }


    public void refreshPosts() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        ParseCloud.callFunctionInBackground("getTimeline", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                if (e == null) {
                    ArrayList results = (ArrayList) object;
                    posts.clear();
                    posts.addAll(Post.addPostsFromCodeCloudFunctionResults(results));
                    postsAdapter.notifyDataSetChanged();
                    rvPosts.smoothScrollToPosition(0);
                    ptrContainer.setRefreshing(false);

                    return;
                }
                Log.e(TAG, "Timeline not fetched", e);

            }
        });
    }
}