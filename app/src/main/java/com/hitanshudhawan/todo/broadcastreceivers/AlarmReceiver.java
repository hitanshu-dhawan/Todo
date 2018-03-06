package com.hitanshudhawan.todo.broadcastreceivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hitanshudhawan.todo.activities.TodoDetailsActivity;
import com.hitanshudhawan.todo.utils.Constant;
import com.hitanshudhawan.todo.utils.NotificationHelper;

/**
 * Created by hitanshu on 6/3/18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        long id = intent.getLongExtra("id", 0);
        String title = "Todo Pending...";
        String body = intent.getStringExtra("body");
        Intent activityIntent = new Intent(context, TodoDetailsActivity.class);
        activityIntent.putExtra(Constant.TODO_ID,id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)id, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationHelper.notify((int)id, title, body, pendingIntent);
    }
}
