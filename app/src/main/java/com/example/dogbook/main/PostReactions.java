package com.example.dogbook.main;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.dogbook.common.ParseApplication;
import com.example.dogbook.main.models.Like;
import com.example.dogbook.main.models.Post;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PostReactions {

    private static final String TAG = "PostReactions";

    public static void like(Post post, Context context, PostReactionCallback postReactionCallback) {
        post.likedLocally();
        postReactionCallback.onOptimisticUpdate(post);

        //Apply changes on database
        Like like = new Like();
        like.setAuthor(ParseUser.getCurrentUser());
        like.setPost(post);
        like.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Like saved successfully");
                    postReactionCallback.onSuccess(post);
                    return;
                }
                Log.e(TAG, "Issue saving Like", e);
                Toast.makeText(context, "Liking was not possible", Toast.LENGTH_SHORT).show();
                post.dislikedLocally();
                postReactionCallback.onFailure(post);
            }
        });

    }

    public static void dislike(Post post, Context context, PostReactionCallback postReactionCallback) {
        post.dislikedLocally();
        postReactionCallback.onOptimisticUpdate(post);
        //Apply changes on database
        ParseQuery<Like> query = ParseApplication.getLikeFromPostByUser(ParseUser.getCurrentUser(), post);
        query.getFirstInBackground(new GetCallback<Like>() {
            @Override
            public void done(Like object, ParseException e) {
                if (e == null) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i(TAG, "Dislike saved successfully");
                                postReactionCallback.onSuccess(post);
                                return;

                            }
                            //Like was not deleted
                            Log.e(TAG, "Issue saving dislike", e);
                            Toast.makeText(context, "Disliking was not possible", Toast.LENGTH_SHORT).show();
                            post.likedLocally();
                            postReactionCallback.onFailure(post);
                        }
                    });
                } else {
                    //Like was not found
                    Log.e(TAG, "Issue saving dislike", e);
                    Toast.makeText(context, "Disliking was not possible", Toast.LENGTH_SHORT).show();
                    post.likedLocally();
                    postReactionCallback.onFailure(post);
                }
            }
        });
    }

    public interface PostReactionCallback {
        void onOptimisticUpdate(Post post);
        void onSuccess(Post post);
        void onFailure(Post post);
    }
}
