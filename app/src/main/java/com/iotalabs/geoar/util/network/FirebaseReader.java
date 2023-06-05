package com.iotalabs.geoar.util.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iotalabs.geoar.data.PersonLocation;
import com.iotalabs.geoar.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseReader {
    private static List<User> firebaseList = new ArrayList<User>();
    private DatabaseReference mDatabase;

    public FirebaseReader(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public List<User> getFirebaseData(){
        //데이터베이스로 부터 한번 받아오기
        //서버 값을 반환할 수 없는 경우 클라이언트는 로컬 스토리지 캐시를 프로브하고 값을 여전히 찾을 수 없으면 오류를 반환합니다.
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    firebaseList.clear();
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
                        firebaseList.add(user);
                    }
                }
            }
        });
        return firebaseList;
    }
}
