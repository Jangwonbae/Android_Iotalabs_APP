package com.iotalabs.geoar.util.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.iotalabs.geoar.util.noti.NotificationCreator;

public class ReStartBackgroundLocationService extends Service {

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
        Intent in = new Intent(this, BackgroundLocationService.class);
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
        String channelId = "ReStartNoti";
        String channelName = "위치 정보 사용 알림";

        useingLocationNotification = new NotificationCreator(title,message,getApplicationContext(),channelId,channelName);
        startForeground(9, useingLocationNotification.showUseingLocationNoti());
    }

}