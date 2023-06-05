package com.iotalabs.geoar.data;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String UserUUID;
    private PersonLocation personLocation;
    private HashMap<String, String> follows;
    private String token;

    public User(){
        follows = new HashMap<>();
        Log.d("ssssssss","나 호출됨");
    }
    public String getUserUUID() {
        return UserUUID;
    }

    public PersonLocation getPersonLocation() {
        return personLocation;
    }

    public String getToken() {
        return token;
    }

    public HashMap<String, String> getFollows() {
        return follows;
    }

    public void setUserUUID(String userUUID) {
        UserUUID = userUUID;
    }

    public void setPersonLocation(PersonLocation personLocation) {
        this.personLocation = personLocation;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFollows(HashMap<String, String> follows) {
        this.follows = follows;
    }
}