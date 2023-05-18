package com.iotalabs.geoar.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PersonName {
    private String name;
    private SharedPreferences prefs;
    public PersonName(Context context){
        prefs = context.getSharedPreferences("person_name",0);
        this.name = prefs.getString("name","");
    }
    public String getName(){
        return name;
    }
}
