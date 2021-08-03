package com.example.dogbook.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dogbook.R;
import com.example.dogbook.main.adapters.PostsAdapter;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.main.models.User;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class PostDetailsFragment extends Fragment {

    private Post post;
    private ImageView ivProfilePicture;
    private ImageView ivPostPicture;
    private TextView tvUsername;
    private TextView tvOwner;
    private TextView tvRelativeTime;
    private TextView tvCaption;
    private CheckBox cbLike;
    private TextView tvLikeCount;
    private TextView tvCommentCount;


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
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        ivPostPicture = view.findViewById(R.id.ivPostPicture);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvOwner = view.findViewById(R.id.tvOwner);
        tvRelativeTime = view.findViewById(R.id.tvRelativeTime);
        tvCaption = view.findViewById(R.id.tvCaption);
        cbLike = view.findViewById(R.id.cbLike);
        tvLikeCount = view.findViewById(R.id.tvLikeCount);
        tvCommentCount = view.findViewById(R.id.tvCommentCount);

        ParseFile profilePicture = post.getAuthor().getParseFile(User.KEY_PROFILE_PICTURE);
        if (profilePicture != null) {
            Glide.with(getContext()).load(profilePicture.getUrl()).into(ivProfilePicture);
        }
        tvUsername.setText(post.getAuthor().getUsername());
        tvOwner.setText(String.format("from %s", post.getAuthor().getString(User.KEY_OWNER_NAME)));
        tvRelativeTime.setText(post.getRelativeTime());
        tvCaption.setText(post.getDescription());
        ParseFile postPicture = post.getPhoto();
        if (postPicture != null) {
            ivPostPicture.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(postPicture.getUrl()).into(ivPostPicture);
        }
        tvLikeCount.setText(String.valueOf(post.getLikesCount()));
        tvCommentCount.setText(String.valueOf(post.getCommentsCount()));
        cbLike.setChecked(post.isLikedByLoggedUser());

        cbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Liking action
                if (cbLike.isChecked()){
                    post.like(getContext(), new Post.PostReactionCallback() {
                        @Override
                        public void onOptimisticUpdate(Post post) {
                            tvLikeCount.setText(String.valueOf(post.getLikesCount()));
                        }

                        @Override
                        public void onSuccess(Post post) {}

                        @Override
                        public void onFailure(Post post) {
                            tvLikeCount.setText(String.valueOf(post.getLikesCount()));
                            cbLike.setChecked(false);
                        }
                    });

                    return;
                }
                //Disliking action
                post.dislike(getContext(), new Post.PostReactionCallback() {
                    @Override
                    public void onOptimisticUpdate(Post post) {
                        tvLikeCount.setText(String.valueOf(post.getLikesCount()));
                    }

                    @Override
                    public void onSuccess(Post post) {}

                    @Override
                    public void onFailure(Post post) {
                        tvLikeCount.setText(String.valueOf(post.getLikesCount()));
                        cbLike.setChecked(true);
                    }
                });
            }
        });
    }
}