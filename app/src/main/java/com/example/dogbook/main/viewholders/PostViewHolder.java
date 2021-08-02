package com.example.dogbook.main.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogbook.main.adapters.PostsAdapter;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.R;
import com.example.dogbook.main.models.User;
import com.parse.ParseFile;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;

    private ImageView ivProfilePicture;
    private ImageView ivPostPicture;
    private TextView tvUsername;
    private TextView tvOwner;
    private TextView tvRelativeTime;
    private TextView tvCaption;
    private CheckBox cbLike;
    private TextView tvLikeCount;
    private TextView tvCommentCount;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        ivPostPicture = itemView.findViewById(R.id.ivPostPicture);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        tvOwner = itemView.findViewById(R.id.tvOwner);
        tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
        tvCaption = itemView.findViewById(R.id.tvCaption);
        cbLike = itemView.findViewById(R.id.cbLike);
        tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
        this.context = context;

        itemView.setOnClickListener(this);
        cbLike.setOnClickListener(this);
    }

    public void bind(Post post) {
        ParseFile profilePicture = post.getAuthor().getParseFile(User.KEY_PROFILE_PICTURE);
        if (profilePicture != null) {
            Glide.with(context).load(profilePicture.getUrl()).into(ivProfilePicture);
        }

        tvUsername.setText(post.getAuthor().getUsername());
        tvOwner.setText(String.format("from %s", post.getAuthor().getString(User.KEY_OWNER_NAME)));
        tvRelativeTime.setText(post.getRelativeTime());
        tvCaption.setText(post.getDescription());

        ParseFile postPicture = post.getPhoto();
        if (postPicture != null) {
            ivPostPicture.setVisibility(View.VISIBLE);
            Glide.with(context).load(postPicture.getUrl()).into(ivPostPicture);
        }

        tvLikeCount.setText(String.valueOf(post.getLikesCount()));
        tvCommentCount.setText(String.valueOf(post.getCommentsCount()));
        cbLike.setChecked(post.isLikedByLoggedUser());
    }

    public void recycle() {
        ivProfilePicture.setImageResource(R.drawable.dog_icon);
        tvUsername.setText("");
        tvOwner.setText("");
        tvCaption.setText("");
        ivPostPicture.setVisibility(View.GONE);
        ivPostPicture.setImageResource(0);
        tvLikeCount.setText("");
        tvCommentCount.setText("");
        cbLike.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        int adapterPosition = getAdapterPosition();

        //Liking action
        if (v == cbLike && cbLike.isChecked()){
            PostsAdapter.posts.get(adapterPosition).like(context, new Post.PostReactionCallback() {
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
        if (v ==cbLike && !cbLike.isChecked()){
            PostsAdapter.posts.get(adapterPosition).dislike(context, new Post.PostReactionCallback() {
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
            return;
        }

        if (PostsAdapter.clickListener != null) {
            PostsAdapter.clickListener.onItemClick(getAdapterPosition(),v);
        }
    }
}
