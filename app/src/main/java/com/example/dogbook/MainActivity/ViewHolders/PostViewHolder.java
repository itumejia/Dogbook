package com.example.dogbook.MainActivity.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogbook.MainActivity.Models.Post;
import com.example.dogbook.R;
import com.parse.ParseFile;

public class PostViewHolder extends RecyclerView.ViewHolder {


    ImageView ivProfilePicture;
    ImageView ivPostPicture;
    TextView tvUsername;
    TextView tvOwner;
    TextView tvCaption;
    Context context;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        ivPostPicture = itemView.findViewById(R.id.ivPostPicture);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        tvOwner = itemView.findViewById(R.id.tvOwner);
        tvCaption = itemView.findViewById(R.id.tvCaption);
        this.context = context;
    }

    public void bind(Post post) {
        ParseFile profilePicture = post.getAuthor().getParseFile("profilePicture");
        if (profilePicture != null) {
            Glide.with(context).load(profilePicture.getUrl()).into(ivProfilePicture);
        }

        tvUsername.setText(post.getAuthor().getUsername());
        tvOwner.setText(String.format("from %s", post.getAuthor().getString("ownerName")));
        tvCaption.setText(post.getDescription());

        ParseFile postPicture = post.getPhoto();
        if (postPicture != null) {
            ivPostPicture.setVisibility(View.VISIBLE);
            Glide.with(context).load(postPicture.getUrl()).into(ivPostPicture);
        }
    }

    public void recycle() {
        ivProfilePicture.setImageResource(R.drawable.dog_icon);
        tvUsername.setText("");
        tvOwner.setText("");
        tvCaption.setText("");
        ivPostPicture.setVisibility(View.GONE);
        ivPostPicture.setImageResource(0);
    }
}
