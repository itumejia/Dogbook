package com.example.dogbook.Common;

import android.app.Application;

import com.example.dogbook.MainActivity.Models.Post;
import com.example.dogbook.MainActivity.Models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseUser.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("T1jE58fMZ8vK3hhqx6unQjQTOqD00iq710HO6a6H")
                .clientKey("qXL3ridoOP7CzL58qfdcUpngJJECzglK7Ydv4inz")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}