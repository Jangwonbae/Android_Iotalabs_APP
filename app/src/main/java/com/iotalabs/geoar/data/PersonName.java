package com.iotalabs.geoar.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PersonName {
    private String name;
    private SharedPreferences prefs;
    public PersonName(Context context){
        prefs = context.getSharedPreferences("person_name",0);
    }
    public String getName(){
        this.name = prefs.getString("name","");
        return name;
    }
    public void setName(String Entered_name){
        SharedPreferences.Editor editor =prefs.edit();
        editor.putString("name",Entered_name);
        editor.apply();
    }
}
