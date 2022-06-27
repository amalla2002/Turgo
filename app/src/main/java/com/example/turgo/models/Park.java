package com.example.turgo.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Park")
public class Park extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_HOURS = "hours";
    public static final String KEY_NPEOPLE = "npeople";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    public Park() {}

    public String getParkName() {
        return getString(KEY_NAME);
    }
    public String getHours() {
        return getString(KEY_HOURS);
    }
    public List<Number> getNpeople() {
        return getList(KEY_NPEOPLE);
    }
    public Number getLatitude() {
        return getNumber(KEY_LATITUDE);
    }
    public Number getLongitude() {
        return getNumber(KEY_LONGITUDE);
    }

    public void setParkName(String name) {
        put(KEY_NAME, name);
    }
    public void setHours(String hours) {
        put(KEY_HOURS, hours);
    }
    public void setNpeople(Number npeople) {
        put(KEY_NPEOPLE, npeople);
    }
    public void setLatitude(Number latitude) {
        put(KEY_LATITUDE, latitude);
    }
    public void setLongitude(Number longitude) {
        put(KEY_LONGITUDE, longitude);
    }
}
