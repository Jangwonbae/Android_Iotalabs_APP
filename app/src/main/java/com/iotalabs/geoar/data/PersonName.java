package com.iotalabs.geoar.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PersonName {
    private static PersonName nameInstance;
    private String name;
    private SharedPreferences prefs;
    private Context context;
    public PersonName(Context context){
        this.context=context;
       prefs = context.getSharedPreferences("person_name",0);
    }

    public static PersonName getInstance(Context context){
        if(nameInstance == null){
            nameInstance = new PersonName(context);
        }
        return nameInstance;
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
