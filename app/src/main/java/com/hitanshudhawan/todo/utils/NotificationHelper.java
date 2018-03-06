package com.hitanshudhawan.todo.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;

import com.hitanshudhawan.todo.R;

/**
 * Created by hitanshu on 6/3/18.
 */

public class NotificationHelper {

    private Context context;

    private NotificationManager notificationManager;

    public static final String CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Todo Notifications";

    public NotificationHelper(Context context) {
        this.context = context;
        createChannel();
    }

    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getNotificationManager().createNotificationChannel(notificationChannel);
    }

    public void notify(Integer id, String title, String body, PendingIntent pendingIntent) {
        Notification.Builder notificationBuilder = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_done_all_black_48dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        getNotificationManager().notify(id, notificationBuilder.build());
    }

    private NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}
