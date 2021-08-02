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
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.dogbook.common.ParseApplication;
import com.example.dogbook.main.adapters.PostsAdapter;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.R;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TimelineFragment extends Fragment {

    private static final String TAG = "TimelineFragment";

    private FragmentManager fragmentManager;
    private RecyclerView rvPosts;
    private PostsAdapter postsAdapter;
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
    }

    private void initView(View view) {
        fragmentManager = getParentFragmentManager();
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
        postsAdapter.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                PostDetailsFragment fragment = new PostDetailsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.timelineFragmentContainer, PostDetailsFragment.newInstance(posts.get(position)))
                        .addToBackStack(null)
                        .commit();
            }
        });
        rvPosts.setAdapter(postsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);
        refreshPosts();
    }


    public void refreshPosts() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        ParseCloud.callFunctionInBackground("getTimeline", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                if (e == null) {
                    HashMap results = (HashMap) object;
                    posts.clear();
                    posts.addAll((Collection<? extends Post>) results.get("posts"));
                    posts = Post.addReactions(posts, (List<HashMap>) results.get("reactions"));
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