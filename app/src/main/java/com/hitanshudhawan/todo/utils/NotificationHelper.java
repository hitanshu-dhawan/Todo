package com.hitanshudhawan.todo.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.hitanshudhawan.todo.R;
import com.hitanshudhawan.todo.broadcastreceivers.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

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

    public void notify(Integer id, String title, String body, PendingIntent pendingIntent, Notification.Action action) {
        Notification.Builder notificationBuilder = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_done_all_black_48dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(action);
        getNotificationManager().notify(id, notificationBuilder.build());
    }

    public void dismissNotification(Integer id) {
        getNotificationManager().cancel(id);
    }

    private NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public void scheduleNotification(long id, String body, long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", id);
        intent.putExtra("body", body);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }

    public void cancelScheduledNotification(long id, String body) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", id);
        intent.putExtra("body", body);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}
