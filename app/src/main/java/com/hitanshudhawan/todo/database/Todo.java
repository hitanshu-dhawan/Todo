package com.hitanshudhawan.todo.database;

import android.database.Cursor;

import java.util.Calendar;

/**
 * Created by hitanshu on 17/7/17.
 */

public class Todo {

    private Long id;
    private String title;
    private Calendar dateTime;
    private Boolean isDone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public static Todo fromCursor(Cursor cursor) {
        Todo todo = new Todo();
        todo.setId(cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry._ID)));
        todo.setTitle(cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_TITLE)));
        Calendar dateTime = Calendar.getInstance();
        dateTime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME)));
        todo.setDateTime(dateTime);
        if (cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_DONE)) == TodoContract.TodoEntry.TODO_NOT_DONE)
            todo.setDone(false);
        else
            todo.setDone(true);

        return todo;
    }
}
