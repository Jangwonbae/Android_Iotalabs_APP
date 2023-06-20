package com.iotalabs.geoar.view.main.adapter.friend_list;

public class FriendData {
    private String UUID;
    private String name;
    private Double latitude;
    private Double longitude;

    public FriendData(String UUID,String name){
        this.UUID=UUID;
        this.name=name;
    }
    public void setLatLog(Double latitude, Double longitude){
        this.latitude= latitude;
        this.longitude=longitude;
    }

    public String getUUID() {
        return UUID;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}