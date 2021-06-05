package com.example.boundservice23032021.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.boundservice23032021.R;
import com.example.boundservice23032021.model.Song;

public class MyService extends Service {

    NotificationManager mNotificationManager;
    Notification mNotification;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Song song = (Song) intent.getSerializableExtra("objectsong");

        if (song != null){
            mNotification = createNotification(this,song.getDuration() , song.getTitle());
            startForeground(1, mNotification);
        }
        return START_REDELIVER_INTENT;
    }

    public Notification createNotification(Context context, long duration , String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle(title);

        long minus = (duration / 60000) ;
        long second = (duration % 60000) / 1000;
        builder.setContentText("Current time song : " + "0" +minus + " : " + (second >= 10 ? second : "0" +second));
        builder.setShowWhen(true);
        builder.setSound(null);
        builder.setPriority(Notification.PRIORITY_HIGH);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID", "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(null , null);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        return builder.build();
    }
}
