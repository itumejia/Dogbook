package com.example.dogbook.common;

import android.app.Application;

import com.example.dogbook.main.models.Post;
import com.example.dogbook.main.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    private static final String applicationId = "T1jE58fMZ8vK3hhqx6unQjQTOqD00iq710HO6a6H";
    private static final String clientKey = "qXL3ridoOP7CzL58qfdcUpngJJECzglK7Ydv4inz";
    private static final String server = "https://parseapi.back4app.com";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseUser.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(applicationId)
                .clientKey(clientKey)
                .server(server)
                .build()
        );
    }

    public static ParseQuery<Post> getLocationPostQuery() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.orderByDescending("createdAt");
        query.include("author");
        query.whereExists("location");
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