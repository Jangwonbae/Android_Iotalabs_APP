package com.iotalabs.geoar.data;

public class PersonLocation {
    public static PersonLocation personLocationInstance;
    private String time;
    private String latitude;
    private String longitude;

    public static PersonLocation getInstance(){
        if(personLocationInstance == null){
            personLocationInstance = new PersonLocation();
        }
        return personLocationInstance;
    }


    public String getTime() {
        return time;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
