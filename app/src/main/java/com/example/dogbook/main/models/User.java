package com.example.dogbook.main.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_OWNER_NAME = "ownerName";
    public static final String KEY_BREED = "breed";

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public String getOwnerName() {
        return getString(KEY_OWNER_NAME);
    }

    public String getBreed() {
        return getString(KEY_BREED);
    }
}
