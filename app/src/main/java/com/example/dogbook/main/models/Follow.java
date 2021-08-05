package com.example.dogbook.main.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Follow")
public class Follow extends ParseObject {
    public static final String KEY_FOLLOWED_USER = "followedUser";
    public static final String KEY_FOLLOWING_USER = "followingUser";

    public Follow() {}

    public void setFollowedUser(ParseUser user) {
        put(KEY_FOLLOWED_USER, user);
    }

    public void setFollowingUser(ParseUser user) {
        put(KEY_FOLLOWING_USER, user);
    }

}
