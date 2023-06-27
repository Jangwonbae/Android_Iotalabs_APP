package com.iotalabs.geoar.util.location;

import android.annotation.SuppressLint;
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
import java.util.UUID;

public class NotificationCreator {
    public NotificationCreator(){
        String title="지정영역 벗어남 알림";
        String msg="지정영역을 벗어났습니다.";
        //나한테 노티 띄우기
        Intent intent = new Intent(context, BackgroundLocationUpdateService.class);
        String channel_id = "getOutArea";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.mipmap.iotalabs_app_icon)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            builder = builder.setContent(getCustomDesign(title, msg));
        }
        else
        {
            builder = builder.setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(R.mipmap.iotalabs_app_icon);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "locationGetOut", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build());
        SharedPreferences prefs = getSharedPreferences("person_name",0);
        String name = prefs.getString("name","");
        task4=new PushNoti();//생성
        task4.execute("http://" + IP_ADDRESS + "/push.php", UUID,name);//친구에게 노티보냄
    }
    private RemoteViews getCustomDesign(String title, String message)
    {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewText(R.id.noti_message, message);
        remoteViews.setImageViewResource(R.id.noti_icon, R.mipmap.iotalabs_app_icon);
        return remoteViews;
    }

}
