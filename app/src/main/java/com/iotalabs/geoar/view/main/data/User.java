package com.iotalabs.geoar.view.main.data;

import com.iotalabs.geoar.view.main.data.PersonLocation;

import java.util.HashMap;

public class User {
    private String UserUUID;
    private PersonLocation personLocation;
    private HashMap<String, String> follows;

    public User(){
        follows = new HashMap<>();
    }
    public String getUserUUID() {
        return UserUUID;
    }

    public PersonLocation getPersonLocation() {
        return personLocation;
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

    public void setFollows(HashMap<String, String> follows) {
        this.follows = follows;
    }
}