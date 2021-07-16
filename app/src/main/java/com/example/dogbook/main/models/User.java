package com.example.dogbook.main.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_OWNER_NAME = "ownerName";
}
