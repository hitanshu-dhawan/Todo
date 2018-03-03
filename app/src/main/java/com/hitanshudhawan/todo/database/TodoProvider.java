package com.hitanshudhawan.todo.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by hitanshu on 23/2/18.
 */

public class TodoProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int TODOS = 1001;
    private static final int TODO_ID = 1002;

    static {

        // content://com.hitanshudhawan.todo/todos
        sUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY, TodoContract.PATH_TODOS, TODOS);

        // content://com.hitanshudhawan.todo/todos/2
        sUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY, TodoContract.PATH_TODOS + "/#", TODO_ID);
    }

    private TodoDBHelper mTodoDBHelper;

    @Override
    public boolean onCreate() {
        mTodoDBHelper = new TodoDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mTodoDBHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case TODOS:
                cursor = database.query(TodoContract.TodoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TODO_ID:
                selection = TodoContract.TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TodoContract.TodoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (sUriMatcher.match(uri)) {
            case TODOS:
                return insertTodo(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTodo(Uri uri, ContentValues contentValues) {

        String todoTitle = contentValues.getAsString(TodoContract.TodoEntry.COLUMN_TODO_TITLE);
        if (todoTitle == null || todoTitle.equals("")) {
            throw new IllegalArgumentException();
        }

        Long todoDateTime = contentValues.getAsLong(TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME);
        if (todoDateTime == null || todoDateTime < 0) {
            throw new IllegalArgumentException();
        }

        Integer todoDone = contentValues.getAsInteger(TodoContract.TodoEntry.COLUMN_TODO_DONE);
        if (todoDone == null || todoDone != TodoContract.TodoEntry.TODO_NOT_DONE && todoDone != TodoContract.TodoEntry.TODO_DONE) {
            throw new IllegalArgumentException();
        }

        SQLiteDatabase database = mTodoDBHelper.getWritableDatabase();

        Long id = database.insert(TodoContract.TodoEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e("TAG", "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case TODOS:
                return updateTodo(uri, contentValues, selection, selectionArgs);
            case TODO_ID:
                selection = TodoContract.TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTodo(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateTodo(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        String todoTitle = contentValues.getAsString(TodoContract.TodoEntry.COLUMN_TODO_TITLE);
        if (todoTitle != null && todoTitle.equals("")) {
            throw new IllegalArgumentException();
        }

        Long todoDateTime = contentValues.getAsLong(TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME);
        if (todoDateTime != null && todoDateTime < 0) {
            throw new IllegalArgumentException();
        }

        Integer todoDone = contentValues.getAsInteger(TodoContract.TodoEntry.COLUMN_TODO_DONE);
        if (todoDone != null && todoDone != TodoContract.TodoEntry.TODO_NOT_DONE && todoDone != TodoContract.TodoEntry.TODO_DONE) {
            throw new IllegalArgumentException();
        }

        if (contentValues.size() == 0) return 0;

        SQLiteDatabase database = mTodoDBHelper.getWritableDatabase();
        int rowsUpdated = database.update(TodoContract.TodoEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mTodoDBHelper.getWritableDatabase();

        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case TODOS:
                rowsDeleted = database.delete(TodoContract.TodoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:
                selection = TodoContract.TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TodoContract.TodoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TODOS:
                return TodoContract.TodoEntry.CONTENT_LIST_TYPE;
            case TODO_ID:
                return TodoContract.TodoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri);
        }
    }

}
