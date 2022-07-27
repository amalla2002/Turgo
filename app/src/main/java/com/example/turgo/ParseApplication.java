package com.example.turgo;

import android.app.Application;
import com.example.turgo.models.City;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;

/**
 * Connects to the back4app database
 * Registers the City model for later use
 *
 * Sends notifications to Users when parkVeryPopulated
 * is called from visitParkFragment
 */
public class ParseApplication extends Application {
    public static final String allUsers = "users";

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
    }

    /**
     * Sends a notification to the user channel
     *
     * @param parkName name of park to be displayed
     * @param amount number of people in that park
     */
    public static void parkVeryPopulated(String parkName, Number amount) {
        ParsePush push = new ParsePush();
        push.setMessage(parkName + " currently has " + amount.toString());
        push.setChannel(allUsers);
        push.sendInBackground();
    }
}
