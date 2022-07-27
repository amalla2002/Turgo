package com.example.turgo.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.List;

/**
 * Facilitates getting and setting the data
 * for the logistic regression model used
 * on the NotificationSettingActivity
 */
@ParseClassName("MLData")
public class MLData extends ParseObject {
    public static final String KEY_AGES = "ages";
    public static final String KEY_CHOICES = "choices";

    public MLData() {}

    public int[] getAges() {
        List<Integer> that = getList(KEY_AGES);
        return that.stream().mapToInt(Integer::intValue).toArray();
    }
    public int[] getChoices() {
        List<Integer> that = getList(KEY_CHOICES);
        return that.stream().mapToInt(Integer::intValue).toArray();
    }

    public void setAges(List<Integer> ages) {
        put(KEY_AGES, ages);
    }
    public void setChoices(List<Integer> choices) {
        put(KEY_CHOICES, choices);
    }
}
