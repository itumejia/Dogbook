package com.example.dogbook.main.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dogbook.common.ParseApplication;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Parcel(analyze = Post.class)
@ParseClassName("Post")
public class Post extends ParseObject implements ClusterItem {

    private static final String TAG = "PostClass";

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_AUTHOR = "author";

    public static final String KEY_REACTIONS_IS_LIKED = "isLiked";
    public static final String KEY_REACTIONS_LIKES_COUNT = "likesCount";
    public static final String KEY_REACTIONS_COMMENTS_COUNT = "commentsCount";

    private boolean isLikedByLoggedUser = false; //Default value: the post has not been liked by the user
    private int likesCount;
    private int commentsCount;

    public Post() {}

    //Add reactions information to the posts
    public static List<Post> addReactions(List<Post> posts, List<HashMap> reactions) {
        for (int i = 0; i < posts.size(); i++) {
            posts.get(i).setLikedByLoggedUser((Boolean) reactions.get(i).get(KEY_REACTIONS_IS_LIKED));
            posts.get(i).setLikesCount((int) reactions.get(i).get(KEY_REACTIONS_LIKES_COUNT));
            posts.get(i).setCommentsCount((int) reactions.get(i).get(KEY_REACTIONS_COMMENTS_COUNT));
        }

        return posts;
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getPhoto() {
        return getParseFile(KEY_PHOTO);
    }

    public void setPhoto(ParseFile image){
        put(KEY_PHOTO, image);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public ParseGeoPoint getLocation() { return getParseGeoPoint(KEY_LOCATION); }

    public void setLocation(ParseGeoPoint location) { put(KEY_LOCATION, location);}

    public boolean isLikedByLoggedUser() {
        return isLikedByLoggedUser;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setLikedByLoggedUser(boolean likedByLoggedUser) {
        isLikedByLoggedUser = likedByLoggedUser;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    private void likedLocally() {
        isLikedByLoggedUser = true;
        likesCount +=1;
    }

    private void dislikedLocally() {
        isLikedByLoggedUser = false;
        likesCount -=1;
    }

    public String getRelativeTime() {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        Date createdAt = this.getCreatedAt();
        long time = createdAt.getTime();
        long now = System.currentTimeMillis();

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " m";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " h";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " d";
        }
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        ParseGeoPoint location = this.getLocation();
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Nullable
    @Override
    public String getTitle() {
        return this.getAuthor().getUsername();
    }

    @Nullable
    @Override
    public String getSnippet() {
        return this.getDescription();
    }

    public void like(Context context, PostReactionCallback postReactionCallback) {
        this.likedLocally();
        postReactionCallback.onOptimisticUpdate(this);

        //Apply changes on database
        Like like = new Like();
        like.setAuthor(ParseUser.getCurrentUser());
        like.setPost(this);
        like.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Like saved successfully");
                    postReactionCallback.onSuccess(Post.this);
                    return;
                }
                Log.e(TAG, "Issue saving Like", e);
                Toast.makeText(context, "Liking was not possible", Toast.LENGTH_SHORT).show();
                Post.this.dislikedLocally();
                postReactionCallback.onFailure(Post.this);
            }
        });

    }

    public void dislike(Context context, PostReactionCallback postReactionCallback) {
        this.dislikedLocally();
        postReactionCallback.onOptimisticUpdate(this);
        //Apply changes on database
        ParseQuery<Like> query = ParseApplication.getLikeFromPostByUser(ParseUser.getCurrentUser(), this);
        query.getFirstInBackground(new GetCallback<Like>() {
            @Override
            public void done(Like object, ParseException e) {
                if (e == null) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i(TAG, "Dislike saved successfully");
                                postReactionCallback.onSuccess(Post.this);
                                return;

                            }
                            //Like was not deleted
                            Log.e(TAG, "Issue saving dislike", e);
                            Toast.makeText(context, "Disliking was not possible", Toast.LENGTH_SHORT).show();
                            Post.this.likedLocally();
                            postReactionCallback.onFailure(Post.this);
                        }
                    });
                } else {
                    //Like was not found
                    Log.e(TAG, "Issue saving dislike", e);
                    Toast.makeText(context, "Disliking was not possible", Toast.LENGTH_SHORT).show();
                    Post.this.likedLocally();
                    postReactionCallback.onFailure(Post.this);
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
