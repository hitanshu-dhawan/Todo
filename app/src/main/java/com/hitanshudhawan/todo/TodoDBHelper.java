package com.hitanshudhawan.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hitanshu on 19/7/17.
 */

public class TodoDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database.db";
    public static final String TABLE_NAME = "TodoTable";
    public static final String _ID = "id";
    public static final String TODO_TITLE = "title";
    public static final String TODO_DATE = "date";
    public static final String TODO_DONE = "todo_done";

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public TodoDBHelper(Context context) {
        super(context, TodoDBHelper.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TODO_TITLE + " TEXT, "
                + TODO_DATE + " INTEGER, "
                + TODO_DONE + " INTEGER )";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }


}
