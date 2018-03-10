package com.hitanshudhawan.todo.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.text.format.DateFormat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hitanshudhawan.todo.R;
import com.hitanshudhawan.todo.database.Todo;
import com.hitanshudhawan.todo.database.TodoContract;
import com.hitanshudhawan.todo.utils.Constants;

import java.text.SimpleDateFormat;

/**
 * Created by hitanshu on 2/3/18.
 */

public class TodoViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private Cursor cursor;

    public TodoViewsFactory(Context context, Intent intent) {
        this.context = context;
    }

    public void updateCursor() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

        Long identityToken = Binder.clearCallingIdentity(); // what the hell is this ?
        String[] projection = {
                TodoContract.TodoEntry._ID,
                TodoContract.TodoEntry.COLUMN_TODO_TITLE,
                TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME,
                TodoContract.TodoEntry.COLUMN_TODO_DONE};
        cursor = context.getContentResolver().query(TodoContract.TodoEntry.CONTENT_URI, projection, TodoContract.TodoEntry.COLUMN_TODO_DONE + " = " + TodoContract.TodoEntry.TODO_NOT_DONE, null, null);
        Binder.restoreCallingIdentity(identityToken); // what the hell is this ?
    }

    @Override
    public void onCreate() {
        updateCursor();
    }

    @Override
    public void onDataSetChanged() {
        updateCursor();
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item_todo_widget);
        cursor.moveToPosition(position);
        Todo todo = Todo.fromCursor(cursor);
        rv.setTextViewText(R.id.todo_title_text_view_item_widget, todo.getTitle().replace("\n", " "));
        rv.setTextViewText(R.id.todo_date_time_text_view_item_widget, todo.getDateTime().getTimeInMillis() == 0 ? "" : DateFormat.is24HourFormat(context) ? new SimpleDateFormat("MMMM dd, yyyy  h:mm").format(todo.getDateTime().getTime()) : new SimpleDateFormat("MMMM dd, yyyy  h:mm a").format(todo.getDateTime().getTime()));
        rv.setOnClickFillInIntent(R.id.todo_layout_item_widget, new Intent().putExtra(Constants.TODO_ID, todo.getId()));
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if ((cursor != null) && cursor.moveToPosition(position)) {
            return cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        }
        return -1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
