package com.mpetroiu.smc_admin;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.net.Uri;
import android.os.Build;

public class BaseApplication extends Application {
    public static final String CHANNEL_ID = "Notification";

    @Override
    public void onCreate() {
        super.onCreate();

    createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel noticationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "PlaceNotification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            noticationChannel.setDescription("Notification for places");

            NotificationManager manager = getSystemService(NotificationManager.class);
            
        }
    }
}
