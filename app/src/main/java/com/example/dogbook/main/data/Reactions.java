package com.example.dogbook.main.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.dogbook.common.ParseApplication;
import com.example.dogbook.main.models.Comment;
import com.example.dogbook.main.models.Follow;
import com.example.dogbook.main.models.Like;
import com.example.dogbook.main.models.Post;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class Reactions {

    private static final String TAG = "PostReactions";

    public static void comment(Post post, String content, Context context, PostCommentCallback postLikeCallback) {
        post.commentAddedLocally();
        postLikeCallback.onOptimisticUpdate(post);
        Comment comment = new Comment();
        comment.setAuthor(ParseUser.getCurrentUser());
        comment.setPost(post);
        comment.setContent(content);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    postLikeCallback.onSuccess(post, comment);
                    Log.i(TAG, "Comment uploaded");
                    return;
                }
                Toast.makeText(context, "Could not upload comment", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Issue uploading comment", e);
                postLikeCallback.onFailure(post, comment);
            }
        });
    }

    public static void like(Post post, Context context, PostLikeCallback postLikeCallback) {
        post.likedLocally();
        postLikeCallback.onOptimisticUpdate(post);

        //Apply changes on database
        Like like = new Like();
        like.setAuthor(ParseUser.getCurrentUser());
        like.setPost(post);
        like.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Like saved successfully");
                    postLikeCallback.onSuccess(post);
                    return;
                }
                Log.e(TAG, "Issue saving Like", e);
                Toast.makeText(context, "Liking was not possible", Toast.LENGTH_SHORT).show();
                post.dislikedLocally();
                postLikeCallback.onFailure(post);
            }
        });

    }

    public static void dislike(Post post, Context context, PostLikeCallback postLikeCallback) {
        post.dislikedLocally();
        postLikeCallback.onOptimisticUpdate(post);
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
                                postLikeCallback.onSuccess(post);
                                return;

                            }
                            //Like was not deleted
                            Log.e(TAG, "Issue saving dislike", e);
                            Toast.makeText(context, "Disliking was not possible", Toast.LENGTH_SHORT).show();
                            post.likedLocally();
                            postLikeCallback.onFailure(post);
                        }
                    });
                } else {
                    //Like was not found
                    Log.e(TAG, "Issue saving dislike", e);
                    Toast.makeText(context, "Disliking was not possible", Toast.LENGTH_SHORT).show();
                    post.likedLocally();
                    postLikeCallback.onFailure(post);
                }
            }
        });
    }

    public static void follow(ParseUser followedUser, Context context, UserFollowCallback userFollowCallback) {
        userFollowCallback.onOptimisticUpdate();
        //Apply changes on database
        Follow follow = new Follow();
        follow.setFollowedUser(followedUser);
        follow.setFollowingUser(ParseUser.getCurrentUser());
        follow.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Follow saved successfully");
                    userFollowCallback.onSuccess();
                    return;
                }
                Log.e(TAG, "Issue saving Follow", e);
                Toast.makeText(context, "Follow was not possible", Toast.LENGTH_SHORT).show();
                userFollowCallback.onFailure();
            }
        });
    }

    public static void unfollow(ParseUser unfollowedUser, Context context, UserFollowCallback userFollowCallback) {
        userFollowCallback.onOptimisticUpdate();
        //Apply changes on database
        ParseQuery<Follow> query = ParseApplication.getFollowFromUserToUser(ParseUser.getCurrentUser(), unfollowedUser);
        query.getFirstInBackground(new GetCallback<Follow>() {
            @Override
            public void done(Follow object, ParseException e) {
                if (e == null) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i(TAG, "Unfollow saved successfully");
                                userFollowCallback.onSuccess();
                                return;

                            }
                            //Follow was not deleted
                            Log.e(TAG, "Issue saving unfollow", e);
                            Toast.makeText(context, "Unfollowing was not possible", Toast.LENGTH_SHORT).show();
                            userFollowCallback.onFailure();
                        }
                    });
                }
                //Early return not possible as the code inside if is async
                else {
                    //Like was not found
                    Log.e(TAG, "Issue saving unfollow", e);
                    Toast.makeText(context, "Unfollowing was not possible", Toast.LENGTH_SHORT).show();
                    userFollowCallback.onFailure();
                }
            }
        });
    }

    public interface PostLikeCallback {
        void onOptimisticUpdate(Post post);
        void onSuccess(Post post);
        void onFailure(Post post);
    }

    public interface PostCommentCallback {
        void onOptimisticUpdate(Post post);
        void onSuccess(Post post, Comment comment);
        void onFailure(Post post, Comment comment);
    }

    public interface UserFollowCallback {
        void onOptimisticUpdate();
        void onSuccess();
        void onFailure();
    }
}
