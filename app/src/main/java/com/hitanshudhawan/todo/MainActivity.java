package com.hitanshudhawan.todo;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.hitanshudhawan.todo.database.TodoContract;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView mTodoRecyclerView;
    TodoCursorAdapter mTodoCursorAdapter;
    LinearLayout mEmptyView;

    FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTodoRecyclerView = (RecyclerView) findViewById(R.id.todo_recycler_view);
        mTodoCursorAdapter = new TodoCursorAdapter(MainActivity.this);
        mTodoRecyclerView.setAdapter(mTodoCursorAdapter);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        setTitle("Todos");

        getSupportLoaderManager().initLoader(0, null, MainActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.about_item:
                // for testing purpose...
                ContentValues contentValues = new ContentValues();
                contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_TITLE,"Hitanshu");
                contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME,0);
                contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DONE, TodoContract.TodoEntry.TODO_NOT_DONE);
                getContentResolver().insert(TodoContract.TodoEntry.CONTENT_URI,contentValues);
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TodoContract.TodoEntry._ID,
                TodoContract.TodoEntry.COLUMN_TODO_TITLE,
                TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME,
                TodoContract.TodoEntry.COLUMN_TODO_DONE};
        return new CursorLoader(MainActivity.this,
                TodoContract.TodoEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTodoCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTodoCursorAdapter.swapCursor(null);
    }
}
