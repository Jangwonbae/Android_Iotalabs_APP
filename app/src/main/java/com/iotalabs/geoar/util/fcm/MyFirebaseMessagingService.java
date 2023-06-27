package com.iotalabs.geoar.util.fcm;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iotalabs.geoar.data.StaticUUID;
import com.iotalabs.geoar.util.noti.NotificationCreator;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String UUID;
    private DatabaseReference mDatabase;
    private NotificationCreator notificationCreator;
    @SuppressLint("WrongThread")
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        UUID = StaticUUID.UUID;
        mDatabase = FirebaseDatabase.getInstance().getReference();;
        mDatabase.child("USER").child(UUID).child("token").setValue(token);
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        String channel_id = "PushNotification";
        String channel_name = "web_app";
        if (remoteMessage.getData().size() > 0)//받은 데이터가 있으면
        {//알림 띄우기
            notificationCreator = new NotificationCreator(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"),
                    getApplicationContext(),channel_id, channel_name);
            notificationCreator.showNotification();
        }
    }

}