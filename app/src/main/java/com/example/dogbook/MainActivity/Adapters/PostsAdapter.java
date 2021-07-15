package com.example.dogbook.MainActivity.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogbook.MainActivity.Models.Post;
import com.example.dogbook.MainActivity.ViewHolders.PostViewHolder;
import com.example.dogbook.R;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private List<Post> posts;
    private Context context;

    public PostsAdapter(Context context, List<Post> posts) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}