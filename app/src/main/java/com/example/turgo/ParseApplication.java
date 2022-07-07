package com.example.turgo;

import android.app.Application;

import com.example.turgo.models.City;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;

import java.time.Instant;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(City.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("3fHfoZJ4dbkd7HUWRcdKQvvERczTduLpDOGAjzQV")
                .clientKey("eQ198frFPoMxkoKMl8HeOaRdtqCJAFTg1uxvFikO")
                .server("https://parseapi.back4app.com")
                .build()
        );
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "794775232312");
        installation.saveInBackground();
        ParsePush.subscribeInBackground("User");

    }

    public static void parkVeryPopulated(String parkName, Number amount) {
        ParsePush push = new ParsePush();
        push.setMessage(parkName + " currently has " + amount.toString());
        push.setChannel("User");
        push.sendInBackground();
    }
}
