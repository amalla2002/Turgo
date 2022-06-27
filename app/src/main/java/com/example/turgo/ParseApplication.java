package com.example.turgo;

import android.app.Application;

import com.example.turgo.models.City;
import com.example.turgo.models.Park;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(City.class);
        ParseObject.registerSubclass(Park.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("3fHfoZJ4dbkd7HUWRcdKQvvERczTduLpDOGAjzQV")
                .clientKey("eQ198frFPoMxkoKMl8HeOaRdtqCJAFTg1uxvFikO")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
