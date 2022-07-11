package com.example.turgo.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@ParseClassName("City")
public class City extends ParseObject {
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PARKS = "parks";
    public static final String KEY_TREE = "tree";
    public static final String KEY_HOURS = "hours";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LNG = "longitude";

    public City() {}

    public String getLocation() {
        return getString(KEY_LOCATION);
    }
    public List<String> getParks() {
        return getList(KEY_PARKS);
    }
    public int[] getTree() {
        List<Integer> that = getList(KEY_TREE);
        return that.stream().mapToInt(Integer::intValue).toArray();
    }
    public List<String> getHours() {
        return getList(KEY_HOURS);
    }
    public List<Number> getLatitude() {
        return getList(KEY_LAT);
    }
    public List<Number> getLongitude() {
        return getList(KEY_LNG);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }
    public void setParks(List<String> parks) {
        put(KEY_PARKS, parks);
    }
    public void setTree(int[] tree) {
        put(KEY_TREE, Arrays.stream(tree).boxed().collect(Collectors.toList()));
    }
    public void setHours(List<String> hours) {
         put(KEY_HOURS, hours);
    }
    public void setLatitude(List<Number> latitudes) {
         put(KEY_LAT, latitudes);
    }
    public void setLongitude(List<Number> longitudes) {
         put(KEY_LNG, longitudes);
    }
}
