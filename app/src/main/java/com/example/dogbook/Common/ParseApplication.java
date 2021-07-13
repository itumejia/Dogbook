package com.example.dogbook.Common;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("T1jE58fMZ8vK3hhqx6unQjQTOqD00iq710HO6a6H")
                .clientKey("qXL3ridoOP7CzL58qfdcUpngJJECzglK7Ydv4inz")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}