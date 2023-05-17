package com.iotalabs.geoar.view.main.adapter.friend_list;

public class FriendData {
    public String UUID;
    public String name;
    public int _id;
    public FriendData(int _id,String UUID,String name){
        this._id=_id;
        this.UUID=UUID;
        this.name=name;
    }
}