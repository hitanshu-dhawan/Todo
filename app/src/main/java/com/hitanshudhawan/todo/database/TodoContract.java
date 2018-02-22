package com.hitanshudhawan.todo.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hitanshu on 23/2/18.
 */

public final class TodoContract {

    private TodoContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.hitanshudhawan.todo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TODOS = "todos";

    public static final class TodoEntry implements BaseColumns {

        //  The content URI to access the todos data in the provider.
        // content://com.hitanshudhawan.todo/todos
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TODOS);

        // The MIME type for a list of todos.
        // vnd.android.cursor.dir/com.hitanshudhawan.todo/todos
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODOS;

        // The MIME type for a single todo.
        // vnd.android.cursor.item/com.hitanshudhawan.todo/todos
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODOS;


        // Name of database table for todos.
        public final static String TABLE_NAME = "TodosTable";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TODO_TITLE = "title";
        public final static String COLUMN_TODO_DATE_TIME = "date_time";
        public final static String COLUMN_TODO_DONE = "todo_done";
        public static final int TODO_NOT_DONE = 0;
        public static final int TODO_DONE = 1;

    }

}
