package com.example.turgo.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.awt.font.NumericShaper;

@ParseClassName("Post")
public class Post extends ParseObject {
    //loc, pic, rate, rating, descr
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_USER = "user";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_RATES = "rates";
    public static final String KEY_RATING = "rating";


    public Post() {}

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public ParseFile getPicture() {
        return getParseFile(KEY_PICTURE);
    }
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }
    public Number getRates() {
        return getNumber(KEY_RATES);
    }
    public Number getRating() {
        return getNumber(KEY_RATING);
    }



    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }
    public void setPicture(ParseFile parseFile) {
        put(KEY_PICTURE, parseFile);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
    public void setLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }
    public void setRates(Number rates) {
        put(KEY_RATES, rates);
    }
    public void setRating(Number rating) {
        put(KEY_RATING, rating);
    }
}
