package com.iotalabs.geoar.util.noti;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import com.example.lotalabsappui.R;
import com.iotalabs.geoar.util.fcm.PushNoti;
import com.iotalabs.geoar.view.main.activity.MainActivity;

import java.util.UUID;

public class NotificationCreator {
    private String notiTitle;
    private String notiMessage;
    private Context notiContext;
    private String notiChannelId;
    private String notiChannelName;

    private Uri notiSound;
    private NotificationCompat.Builder builder;
    private NotificationChannel channel;
    private NotificationManager notificationManager;

    public NotificationCreator(String title, String msg, Context context, String channelId, String channelName){
        this.notiTitle=title;
        this.notiMessage=msg;
        this.notiContext=context;
        this.notiChannelId=channelId;
        this.notiChannelName=channelName;

        Intent intent = new Intent(notiContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(notiContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        builder = new NotificationCompat.Builder(notiContext, notiChannelId)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.iotalabs_app_icon)
                .setContentTitle(notiTitle)
                .setContentText(notiMessage);

        notificationManager = (NotificationManager) notiContext.getSystemService(notiContext.NOTIFICATION_SERVICE);

    }

    public Notification showUseingLocationNoti(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Android 8.0(API 수준 26)부터는 모든 알림을 채널에 할당해야 합니다.

            channel = new NotificationChannel(notiChannelId, notiChannelName, NotificationManager.IMPORTANCE_LOW);//소리없음
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);

            builder.setChannelId(notiChannelId);
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);

        }
        return builder.build();
    }

    public void showNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            builder = builder.setContent(getCustomDesign(notiTitle, notiMessage));
        }

        notiSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//사운드

        builder.setSound(notiSound)
                .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)//Android 8.0(API 수준 26)부터는 모든 알림을 채널에 할당해야 합니다.
        {
            channel = new NotificationChannel(notiChannelId, notiChannelName, NotificationManager.IMPORTANCE_HIGH);//소리있음
            channel.setSound(notiSound, null);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, builder.build());
    }

    private RemoteViews getCustomDesign(String title, String message)
    {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(notiContext.getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewText(R.id.noti_message, message);
        remoteViews.setImageViewResource(R.id.noti_icon, R.mipmap.iotalabs_app_icon);
        return remoteViews;
    }
}
