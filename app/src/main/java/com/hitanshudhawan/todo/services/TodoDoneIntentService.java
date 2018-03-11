package com.hitanshudhawan.todo.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.hitanshudhawan.todo.database.TodoContract;
import com.hitanshudhawan.todo.utils.NotificationHelper;
import com.hitanshudhawan.todo.utils.WidgetHelper;

/**
 * Created by hitanshu on 11/3/18.
 */

public class TodoDoneIntentService extends IntentService {

    public TodoDoneIntentService() {
        super(TodoDoneIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        long id = intent.getLongExtra("id", 0);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DONE, TodoContract.TodoEntry.TODO_DONE);
        getContentResolver().update(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, id), contentValues, null, null);

        WidgetHelper.updateWidget(TodoDoneIntentService.this);

        new NotificationHelper(TodoDoneIntentService.this).dismissNotification((int) id);

        Toast.makeText(TodoDoneIntentService.this, "Todo Done.", Toast.LENGTH_SHORT).show();
    }
}
