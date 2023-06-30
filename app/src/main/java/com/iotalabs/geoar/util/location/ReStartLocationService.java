package com.iotalabs.geoar.util.location;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.PolyUtil;
import com.iotalabs.geoar.data.Clock;
import com.iotalabs.geoar.data.Constants;
import com.iotalabs.geoar.data.StaticUUID;
import com.iotalabs.geoar.util.noti.NotificationCreator;
import com.iotalabs.geoar.view.main.data.PersonLocation;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReStartLocationService extends Service {

    private NotificationCreator useingLocationNotification;

    //Service
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//서비스를 시작하도록 요청
        StartForeground();
        /////////////////////////////////////////////////////////////////////
        Intent in = new Intent(this, LocationService.class);
        startService(in);

        stopForeground(true);//노티 지우고
        stopSelf();//서비스 종료
        return START_STICKY;
    }

    @Override
    public void onDestroy() {//서비스를 소멸시킬 때 호출
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }//다른 구성요소와 서비스를 바인딩하려는 경우 호출
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void StartForeground() {//서비스가 시작할 때 <위치정보 사용중> 노티 띄우기
        String title = "IotalabsApp";
        String message = "위치 정보 사용중";
        String channelId = "1";
        String channelName = "2";

        useingLocationNotification = new NotificationCreator(title,message,getApplicationContext(),channelId,channelName);
        startForeground(9, useingLocationNotification.showUseingLocationNoti());
    }

}