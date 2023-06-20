package com.iotalabs.geoar.view.main;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iotalabs.geoar.data.PersonLocation;
import com.iotalabs.geoar.data.StaticUUID;
import com.iotalabs.geoar.data.User;
import com.iotalabs.geoar.view.main.adapter.friend_list.FriendData;
import com.rugovit.eventlivedata.MutableEventLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataBaseViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private String UUID;
    private final String formatUUID = "[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";
    private List<User> allUserList;
    static MutableEventLiveData<List<LatLng>> allUserLocationList = new MutableEventLiveData<>();//맵에서 구독할 사람위치 리스트
    static MutableEventLiveData<ArrayList<FriendData>> myFriendList; //친구리스트에서 구독할 친구 리스트

    public DataBaseViewModel() {
        UUID = StaticUUID.UUID;
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }
    public MutableEventLiveData<ArrayList<FriendData>> getMyFriendList(){
        if (myFriendList == null){
            myFriendList= new MutableEventLiveData<ArrayList<FriendData>>();
        }
        return myFriendList;
    }

    public void getAllUserData() {//새로고침을 요청하거나 친구를 추가 삭제할 경우 호출
        myFriendList= getMyFriendList();
        //전체 데이터를 firebase로부터 받아옴
        //데이터베이스로 부터 한번 받아오기
        //서버 값을 반환할 수 없는 경우 클라이언트는 로컬 스토리지 캐시를 프로브하고 값을 여전히 찾을 수 없으면 오류를 반환합니다.
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {//받아오기 실패
                    Log.e("firebase", "Error getting data", task.getException());
                } else {//받아오기 성공
                    allUserList = new ArrayList<User>();
                    //USER노드 밑에 있는 자식들을 하나씩 가져옴
                    for (DataSnapshot userSnapshot : task.getResult().child("USER").getChildren()) {
                        //token
                        User user = userSnapshot.getValue(User.class);
                        //UUID
                        user.setUserUUID(userSnapshot.getKey());
                        //latitude, longitude, time
                        user.setPersonLocation(userSnapshot.child("location").getValue(PersonLocation.class));
                        //follow(key:UUID, value:name)
                        HashMap<String, String> map = new HashMap<>();
                        for (DataSnapshot followSnapshot : userSnapshot.child("follow").getChildren()) {
                            map.put(followSnapshot.getKey(), (String) followSnapshot.getValue());
                        }
                        user.setFollows(map);
                        allUserList.add(user);
                    }
                    //전체 사람위치,친구정보 정리
                    List<LatLng> tempLatLng = new ArrayList<LatLng>();
                    ArrayList<FriendData> tempFriendList = new ArrayList<FriendData>();
                    for (User user : allUserList) {
                        if (!user.getUserUUID().equals(UUID)) {//내 UUID가 아니면 위치 받아옴 (나를 제외한 모든 사람의 위치)

                            //String curTime = new Clock().getTime();//시간값 계산해서 1일이상인 데이터는 표시x
                            tempLatLng.add(new LatLng(Double.parseDouble(user.getPersonLocation().getLatitude()),//위도
                                    Double.parseDouble(user.getPersonLocation().getLongitude())));//경도
                        } else {//내 UUID면 친구정보를 봄 (내 친구의 정보)
                            for (Map.Entry<String, String> follow : user.getFollows().entrySet()) {
                                tempFriendList.add(new FriendData(follow.getKey(), follow.getValue()));//UUID와 설정된 이름을 저장
                            }
                        }
                    }
                    for (User user : allUserList){//친구 위치정보를 저장
                        for (FriendData friend : tempFriendList){
                            if(user.getUserUUID().equals(friend.getUUID())){
                                friend.setLatLog(Double.parseDouble(user.getPersonLocation().getLatitude()),Double.parseDouble(user.getPersonLocation().getLongitude()));
                            }
                        }
                    }
                    allUserLocationList.setValue(tempLatLng);
                    myFriendList.setValue(tempFriendList);
                }
            }
        });
    }

    public void addFriend(String result) {

        String uuidFriend;
        String nameFriend;
        if (result != null) {
            //성공
            Pattern patten = Pattern.compile(formatUUID);
            Matcher matcher = patten.matcher(result);
            boolean regex = matcher.find();
            if (regex) {//UUID형식인지 체크
                uuidFriend = result.split("문자열나누기")[0];
                nameFriend = result.split("문자열나누기")[1];
                if(myFriendList.getValue().isEmpty()){
                    mDatabase.child("USER").child(UUID).child("follow").child(uuidFriend).setValue(nameFriend);//추가
                    ArrayList<FriendData> tempFriendList =myFriendList.getValue();
                    tempFriendList.add(new FriendData(uuidFriend,nameFriend));
                    myFriendList.setValue(tempFriendList);
                }
                else {
                    for (FriendData friendData : myFriendList.getValue()) {
                        if (friendData.getUUID().equals(uuidFriend)) {
                            //이미등록된 친구

                        } else {
                            mDatabase.child("USER").child(UUID).child("follow").child(uuidFriend).setValue(nameFriend);//추가
                            ArrayList<FriendData> tempFriendList = myFriendList.getValue();
                            tempFriendList.add(new FriendData(uuidFriend, nameFriend));
                            myFriendList.setValue(tempFriendList);

                        }
                    }
                }
            } else {
                //친구 큐알이 아님
                
            }
        }
    }

    public void removeFriend(String friendUUID) {
        mDatabase.child("USER").child(UUID).child("follow").child(friendUUID).removeValue();//(임시코드) DB에서 삭제하는게 best!
        getAllUserData();
    }
}
