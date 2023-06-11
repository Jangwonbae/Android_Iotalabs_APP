package com.iotalabs.geoar.view.main;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iotalabs.geoar.data.Clock;
import com.iotalabs.geoar.data.PersonLocation;
import com.iotalabs.geoar.data.StaticUUID;
import com.iotalabs.geoar.data.User;
import com.iotalabs.geoar.util.network.FirebaseReader;
import com.iotalabs.geoar.view.main.adapter.friend_list.FriendData;
import com.rugovit.eventlivedata.MutableEventLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class DataBaseViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private String UUID;
    private List<User> allUserList;
    MutableEventLiveData<List<LatLng>> allUserLocationList = new MutableEventLiveData<>();//맵에서 구독할 사람위치 리스트
    MutableEventLiveData<ArrayList<FriendData>> myFriendList = new MutableEventLiveData<>();//친구리스트에서 구독할 친구 리스트

    public DataBaseViewModel() {//프래그먼트 첫생성시 한번씩 호출 (총 2번)
        UUID= StaticUUID.UUID;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //getAllUserList();
    }
    public void getAllUserData(){//새로고침을 요청하거나 친구를 추가 삭제할 경우 호출
        //전체 데이터를 firebase로부터 받아옴
        //데이터베이스로 부터 한번 받아오기
        //서버 값을 반환할 수 없는 경우 클라이언트는 로컬 스토리지 캐시를 프로브하고 값을 여전히 찾을 수 없으면 오류를 반환합니다.
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    allUserList = new ArrayList<User>();
                    //USER노드 밑에 있는 자식들을 하나씩 가져옴
                    for (DataSnapshot userSnapshot: task.getResult().child("USER").getChildren()){
                        //token
                        User user= userSnapshot.getValue(User.class);
                        //UUID
                        user.setUserUUID(userSnapshot.getKey());
                        //latitude, longitude, time
                        user.setPersonLocation(userSnapshot.child("location").getValue(PersonLocation.class));
                        //follow(key:UUID, value:name)
                        HashMap<String, String> map = new HashMap<>();
                        for (DataSnapshot followSnapshot: userSnapshot.child("follow").getChildren()){
                            map.put(followSnapshot.getKey(),(String) followSnapshot.getValue());
                        }
                        user.setFollows(map);
                        allUserList.add(user);
                    }
                    //사람위치,친구정보 정리
                    List<LatLng> tempLatLng = new ArrayList<LatLng>();
                    ArrayList<FriendData> tempFriendList  = new ArrayList<FriendData>();
                    for (User user:allUserList) {
                        if(!user.getUserUUID().equals(UUID)){//내 UUID가 아니면 위치 받아옴

                            //String curTime = new Clock().getTime();//시간값 계산해서 1일이상인 데이터는 표시x
                            tempLatLng.add(new LatLng(Double.parseDouble(user.getPersonLocation().getLatitude()),//위도
                                    Double.parseDouble(user.getPersonLocation().getLongitude())));//경도
                        }
                        else{//내 UUID면 친구정보를 봄
                            for (Map.Entry<String, String> follow : user.getFollows().entrySet()) {

                                tempFriendList.add(new FriendData(follow.getKey(),follow.getValue()));
                            }
                        }
                    }
                    allUserLocationList.setValue(tempLatLng);
                    myFriendList.setValue(tempFriendList);
                }
            }
        });
    }
}
