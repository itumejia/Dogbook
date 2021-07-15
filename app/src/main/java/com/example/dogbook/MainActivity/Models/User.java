package com.example.dogbook.MainActivity.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {

    public String getName() {
        return getUsername();
    }

    public void setName() {
        setUsername("");
    }

//    public Post getPost() {
//        // return getParseObject(); // How to turn ParseObject to Post object
//    }
}
