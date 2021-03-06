package com.example.dogbook.common;

import android.app.Application;
import android.util.Log;

import com.example.dogbook.main.models.Comment;
import com.example.dogbook.main.models.Follow;
import com.example.dogbook.main.models.Like;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.main.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParseApplication extends Application {

    private static final String applicationId = "T1jE58fMZ8vK3hhqx6unQjQTOqD00iq710HO6a6H";
    private static final String clientKey = "qXL3ridoOP7CzL58qfdcUpngJJECzglK7Ydv4inz";
    private static final String server = "https://parseapi.back4app.com";
    private static final String TAG = "ParseApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Follow.class);
        ParseUser.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(applicationId)
                .clientKey(clientKey)
                .server(server)
                .build()
        );
    }

    public static ParseQuery<Post> getLocationPostWithinBoundsQuery(LatLng southwest, LatLng northeast) {
        ParseQuery<Post> mainQuery = ParseQuery.getQuery(Post.class);
        //Parse whereWithinGeoBox queries have conflict when the given box rounds the Earth.
        //So first we check if the query needs special treatment because of this
        if (southwest.longitude > northeast.longitude) {
            return getLocationPostWithinBoundsQueryFixed(southwest, northeast);
        }
        mainQuery.include("author");
        ParseGeoPoint northeastGeoPoint = new ParseGeoPoint(northeast.latitude, northeast.longitude);
        ParseGeoPoint southwestGeoPoint = new ParseGeoPoint(southwest.latitude, southwest.longitude);
        mainQuery.whereWithinGeoBox("location", northeastGeoPoint, southwestGeoPoint);

        return mainQuery;
    }

    //Special query for boxes that round the Earth
    private static ParseQuery<Post> getLocationPostWithinBoundsQueryFixed(LatLng southwest, LatLng northeast) {
        ParseGeoPoint northeastGeoPoint;
        ParseGeoPoint southwestGeoPoint;

        //Make a query that goes from the given west, to the east limit 180
        ParseQuery<Post> eastQuery = ParseQuery.getQuery(Post.class);
        southwestGeoPoint = new ParseGeoPoint(southwest.latitude, southwest.longitude);
        northeastGeoPoint = new ParseGeoPoint(northeast.latitude, 180);
        eastQuery.whereWithinGeoBox("location", southwestGeoPoint, northeastGeoPoint);

        //Make a query that goes from the west limit -180 to the given east
        ParseQuery<Post> westQuery = ParseQuery.getQuery(Post.class);
        southwestGeoPoint = new ParseGeoPoint(southwest.latitude, -180);
        northeastGeoPoint = new ParseGeoPoint(northeast.latitude, northeast.longitude);
        westQuery.whereWithinGeoBox("location", southwestGeoPoint, northeastGeoPoint);

        //Compound OR query
        List<ParseQuery<Post>> queries = new ArrayList<>();
        queries.add(eastQuery);
        queries.add(westQuery);

        ParseQuery<Post> mainQuery = ParseQuery.or(queries);
        mainQuery.include("author");
        return mainQuery;
    }

    public static ParseQuery<Like> getLikeFromPostByUser(ParseUser author, Post post) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo("author", author);
        query.whereEqualTo("post", post);
        return query;
    }

    public static ParseQuery<Comment> getCommentsFromPost(Post post) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.include(Comment.KEY_AUTHOR);
        return query;
    }

    public static ParseQuery<Follow> getFollowFromUserToUser(ParseUser followingUser, ParseUser followedUser) {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_FOLLOWING_USER, followingUser);
        query.whereEqualTo(Follow.KEY_FOLLOWED_USER, followedUser);
        return query;
    }

    public static ParseQuery<Post> getAllPostsQuery() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(20);
        query.orderByDescending("createdAt");
        query.include("author");
        return query;
    }
}