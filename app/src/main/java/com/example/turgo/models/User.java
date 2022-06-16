package com.example.turgo.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AVATAR = "avatar";


    public User() {}

    public ParseFile getDescription() {
        return getParseFile(KEY_AVATAR);
    }

    public void setAvatar(ParseFile avatar) {
        put(KEY_DESCRIPTION, avatar);
    }
}
