package com.hitanshudhawan.todo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hitanshu on 19/7/17.
 */

public class TodoDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public TodoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TodoContract.TodoEntry.TABLE_NAME + " ( "
                + TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TodoContract.TodoEntry.COLUMN_TODO_TITLE + " TEXT NOT NULL, "
                + TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME + " INTEGER, "
                + TodoContract.TodoEntry.COLUMN_TODO_DONE + " INTEGER DEFAULT 0)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //
    }


}
