package com.hitanshudhawan.todo.utils;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import com.hitanshudhawan.todo.widget.TodoWidget;

/**
 * Created by hitanshu on 10/3/18.
 */

public class WidgetHelper {

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, TodoWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(intent);
    }
}
