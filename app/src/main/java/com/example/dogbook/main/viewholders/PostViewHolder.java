package com.example.dogbook.main.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogbook.main.data.Reactions;
import com.example.dogbook.main.adapters.PostDetailsAdapter;
import com.example.dogbook.main.adapters.PostsAdapter;
import com.example.dogbook.main.fragments.PostDetailsFragment;
import com.example.dogbook.main.fragments.ProfileDetailsFragment;
import com.example.dogbook.main.models.Like;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.R;
import com.example.dogbook.main.models.User;
import com.parse.ParseFile;

import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private FragmentManager fragmentManager;
    private RecyclerView.Adapter adapter;

    private ImageView ivProfilePicture;
    private ImageView ivPostPicture;
    private TextView tvUsername;
    private TextView tvOwner;
    private TextView tvRelativeTime;
    private TextView tvCaption;
    private LinearLayout llLikedByLegend;
    private TextView tvLikedByLegend;
    private CheckBox cbLike;
    private TextView tvLikeCount;
    private TextView tvCommentCount;

    public PostViewHolder(@NonNull View itemView, Context context, RecyclerView.Adapter adapter, FragmentManager fragmentManager) {
        super(itemView);
        ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        ivPostPicture = itemView.findViewById(R.id.ivPostPicture);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        tvOwner = itemView.findViewById(R.id.tvOwner);
        tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
        tvCaption = itemView.findViewById(R.id.tvCaption);
        llLikedByLegend = itemView.findViewById(R.id.llLikedByLegend);
        tvLikedByLegend = itemView.findViewById(R.id.tvLikedByLegend);
        cbLike = itemView.findViewById(R.id.cbLike);
        tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.adapter = adapter;

        itemView.setOnClickListener(this);
        cbLike.setOnClickListener(this);
        tvUsername.setOnClickListener(this);
        tvOwner.setOnClickListener(this);
        ivProfilePicture.setOnClickListener(this);

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

        if (post.getLikedBy().size() > 0) {
            tvLikedByLegend.setText(post.getLikedByLegend());
            llLikedByLegend.setVisibility(View.VISIBLE);
        }

        tvLikeCount.setText(String.valueOf(post.getLikesCount()));
        tvCommentCount.setText(String.valueOf(post.getCommentsCount()));
        cbLike.setChecked(post.isLikedByLoggedInUser());
    }

    public void recycle() {
        ivProfilePicture.setImageResource(R.drawable.dog_icon);
        tvUsername.setText("");
        tvOwner.setText("");
        tvCaption.setText("");
        ivPostPicture.setVisibility(View.GONE);
        ivPostPicture.setImageResource(0);
        tvLikedByLegend.setText("");
        llLikedByLegend.setVisibility(View.GONE);
        tvLikeCount.setText("");
        tvCommentCount.setText("");
        cbLike.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        int adapterPosition = getAdapterPosition();
        Post post;

        //Get the post depending if the ViewHolder is in a PostsAdapter or in a PostDetailsAdapter
        if (adapter instanceof PostsAdapter) {
            post = ((PostsAdapter) adapter).posts.get(adapterPosition);
        } else {
            post = ((PostDetailsAdapter) adapter).post;
        }

        //Liking action
        if (v == cbLike && cbLike.isChecked()){
            Reactions.like(post, context, new Reactions.PostLikeCallback() {
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
            Reactions.dislike(post, context, new Reactions.PostLikeCallback() {
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

        //Go to User Details Fragment
        if (v == tvUsername || v == tvOwner || v == ivProfilePicture) {
            fragmentManager.beginTransaction()
                    .replace(R.id.timelineFragmentContainer, ProfileDetailsFragment.newInstance((User) post.getAuthor()))
                    .addToBackStack(null)
                    .commit();
            return;
        }

        //Add click listener to the entire item only if it is in the Posts Adapter
        if (adapter instanceof PostsAdapter) {
            fragmentManager.beginTransaction()
                    .replace(R.id.timelineFragmentContainer, PostDetailsFragment.newInstance(post))
                    .addToBackStack(null)
                    .commit();
        }
    }
}
