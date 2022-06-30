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
    public static final String KEY_PEOPLE = "people";
    public static final String KEY_TREE = "tree";

    public City() {}

    public String getLocation() {
        return getString(KEY_LOCATION);
    }
    public List<String> getParks() {
        return getList(KEY_PARKS);
    }
    public int[] getPeople() {
        List<Integer> that = getList(KEY_PEOPLE);
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
    public void setPeople(List<Integer> people) {
        put(KEY_PEOPLE, people);
    }
    public void setTree(int[] tree) {
        put(KEY_TREE, Arrays.stream(tree).boxed().collect(Collectors.toList()));
    }
}
