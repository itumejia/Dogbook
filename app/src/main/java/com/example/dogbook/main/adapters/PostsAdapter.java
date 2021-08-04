package com.example.dogbook.main.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogbook.main.models.Post;
import com.example.dogbook.main.viewholders.PostViewHolder;
import com.example.dogbook.R;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {

    public List<Post> posts;
    private Context context;
    private FragmentManager fragmentManager;

    public PostsAdapter(Context context, FragmentManager fragmentManager, List<Post> posts) {
        this.posts = posts;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view, context, this, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onViewRecycled(@NonNull PostViewHolder holder) {
        super.onViewRecycled(holder);
        holder.recycle();
    }
}
