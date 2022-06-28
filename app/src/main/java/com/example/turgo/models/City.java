package com.example.turgo.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;
import java.util.Arrays;

@ParseClassName("City")
public class City extends ParseObject {

    public static final String KEY_LOCATION = "location";
    public static final String KEY_PARKS = "parks";
    public static final String KEY_DATA = "data";
    public static final String KEY_TREE = "tree";

    public City() {}

    public String getLocation() {
        return getString(KEY_LOCATION);
    }
    public List<String> getParks() {
        return getList(KEY_PARKS);
    }
    public int[] getData() {
        List<Integer> that = getList(KEY_DATA);
        return that.stream().mapToInt(Integer::intValue).toArray();
    }
    public int[] getTree() {
        List<Integer> that = getList(KEY_TREE);
        return that.stream().mapToInt(Integer::intValue).toArray();
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }
    public void setParks(List<String> parks) {
        put(KEY_PARKS, parks);
    }
    public void setData(List<Integer> data) {
        put(KEY_DATA, data);
    }
    public void setTree(int[] tree) {
        put(KEY_TREE, Arrays.asList(tree));
    }
}
