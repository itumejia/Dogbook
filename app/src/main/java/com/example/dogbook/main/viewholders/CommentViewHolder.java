package com.example.dogbook.main.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogbook.R;
import com.example.dogbook.main.models.Comment;
import com.example.dogbook.main.models.User;
import com.parse.ParseFile;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private TextView tvCommentContent;


    public CommentViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;

        ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
    }

    public void bind(Comment comment) {
        ParseFile profilePicture = comment.getAuthor().getParseFile(User.KEY_PROFILE_PICTURE);
        if (profilePicture != null) {
            Glide.with(context).load(profilePicture.getUrl()).into(ivProfilePicture);
        }
        tvUsername.setText(comment.getAuthor().getUsername());
        tvCommentContent.setText(comment.getContent());
    }

    public void recycle() {
        ivProfilePicture.setImageResource(R.drawable.dog_icon);
        tvUsername.setText("");
        tvCommentContent.setText("");
    }
}
