package com.example.dogbook.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogbook.R;
import com.example.dogbook.main.models.Comment;
import com.example.dogbook.main.models.Like;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.main.viewholders.CommentViewHolder;
import com.example.dogbook.main.viewholders.PostViewHolder;

import java.util.List;

public class PostDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_POST = 1;
    private static final int TYPE_COMMENT = 2;

    private Context context;
    private FragmentManager fragmentManager;
    public Post post;
    private List<Comment> comments;

    public PostDetailsAdapter(Context context, FragmentManager fragmentManager, Post post, List<Comment> comments) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.post = post;
        this.comments = comments;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_POST) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view, context, this, fragmentManager);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostViewHolder) {
            ((PostViewHolder) holder).bind(post);
            return;
        }
        ((CommentViewHolder) holder).bind(comments.get(position-1));

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_POST;
        }
        return TYPE_COMMENT;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).recycle();
        }
    }

    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }
}
