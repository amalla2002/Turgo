package com.example.turgo.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.awt.font.NumericShaper;
import java.util.List;

@ParseClassName("City")
public class City extends ParseObject {

    public static final String KEY_LOCATION = "location";
    public static final String KEY_PARKS = "parks";

    public City() {}

    public String getLocation() {
        return getString(KEY_LOCATION);
    }
    public List<String> getParks() {
        return getList(KEY_PARKS);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }
    public void setParks(List<String> parks) {
        put(KEY_PARKS, parks);
    }
}
