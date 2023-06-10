package com.iotalabs.geoar.util.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.lotalabsappui.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iotalabs.geoar.data.ClassUUID;
import com.iotalabs.geoar.data.StaticUUID;
import com.iotalabs.geoar.util.network.InsertToken;
import com.iotalabs.geoar.data.Constants;
import com.iotalabs.geoar.util.db.DbOpenHelper;
import com.iotalabs.geoar.view.main.MainActivity;
import com.iotalabs.geoar.view.create_qr_code.CreateQR_codeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String UUID;
    private DatabaseReference mDatabase;
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
        if (remoteMessage.getData().size() > 0)
        {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }

        if (remoteMessage.getNotification() != null)
        {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

    }

    private RemoteViews getCustomDesign(String title, String message)
    {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewText(R.id.noti_message, message);
        remoteViews.setImageViewResource(R.id.noti_icon, R.mipmap.iotalabs_app_icon);
        return remoteViews;
    }

    @SuppressLint("Range")
    public void showNotification(String title, String message)
    {
        Intent intent = new Intent(this, MainActivity.class);
        String channel_id = "FriendGetOutArea";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
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
            builder = builder.setContent(getCustomDesign(title, message));
        }
        else
        {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.iotalabs_app_icon);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

}