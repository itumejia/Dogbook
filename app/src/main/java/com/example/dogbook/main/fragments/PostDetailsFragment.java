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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dogbook.R;
import com.example.dogbook.common.ParseApplication;
import com.example.dogbook.main.adapters.PostDetailsAdapter;
import com.example.dogbook.main.data.PostReactions;
import com.example.dogbook.main.models.Comment;
import com.example.dogbook.main.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsFragment extends Fragment {

    private static final String TAG = "PostDetailsFragment";

    private Post post;
    private RecyclerView rvPostDetails;
    private PostDetailsAdapter adapter;
    private List<Comment> comments;
    private Button btnComment;
    private EditText etComposeComment;


    public PostDetailsFragment() {}

    public static PostDetailsFragment newInstance(Post post) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Post.class.getSimpleName(), Parcels.wrap(post));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        post = Parcels.unwrap(getArguments().getParcelable(Post.class.getSimpleName()));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        rvPostDetails = view.findViewById(R.id.rvPostDetails);
        comments = new ArrayList<>();
        adapter = new PostDetailsAdapter(getContext(), getParentFragmentManager(), post, comments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPostDetails.setAdapter(adapter);
        rvPostDetails.setLayoutManager(layoutManager);
        refreshComments();

        btnComment = view.findViewById(R.id.btnComment);
        etComposeComment = view.findViewById(R.id.etComposeComment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadComment();
            }
        });
    }

    private void uploadComment() {
        String commentText = etComposeComment.getText().toString();
        if (commentText.length() == 0) {
            Toast.makeText(getContext(), "Your comment is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        PostReactions.comment(post, commentText, getContext(), new PostReactions.PostCommentCallback() {
            @Override
            public void onOptimisticUpdate(Post post) {}

            @Override
            public void onSuccess(Post post, Comment comment) {
                comments.add(comment);
                PostDetailsFragment.this.post.setCommentsCount(post.getCommentsCount());
                adapter.notifyItemChanged(0);
                adapter.notifyItemInserted(comments.size());
                etComposeComment.setText("");
                //Disable and enable edit text to hide keyboard
                etComposeComment.setEnabled(false);
                etComposeComment.setEnabled(true);
                rvPostDetails.smoothScrollToPosition(comments.size());
                return;
            }

            @Override
            public void onFailure(Post post, Comment comment) {

            }
        });
    }

    private void refreshComments() {
        ParseApplication.getCommentsFromPost(post).findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e == null) {
                    comments.clear();
                    comments.addAll(objects);
                    adapter.notifyDataSetChanged();
                    return;
                }
                Toast.makeText(getContext(), "Issue loading comments", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Could not fetch comments", e);

            }
        });
    }
}