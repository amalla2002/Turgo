package com.example.turgo.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Map")
public class Map extends ParseObject {
    public static final String KEY_LOCATION = "postLocation";
    public static final String KEY_POPULARITY = "popularity";

    public Map() {}

    public ParseGeoPoint getPostLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }
    public Number getPopularity() {
        return getNumber(KEY_POPULARITY);
    }

    public void setPostLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }
    public void setPopularity(Number popularity) {
        put(KEY_POPULARITY, popularity);
    }
}
