package com.hitanshudhawan.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hitanshudhawan.todo.database.TodoDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator;
import jp.wasabeef.recyclerview.animators.FlipInRightYAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ArrayList<Todo> mTodos;
    TodoListAdapter mAdapter;

    SearchView mSearchView;
    FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.todoRecyclerView);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        setTitle("Todos");
        initRecyclerView();
        initFab();
    }

    private void initRecyclerView() {

        mTodos = fetchTodosFromDB();

        mAdapter = new TodoListAdapter(MainActivity.this, mTodos);
        SlideInBottomAnimationAdapter slideInBottomAnimationAdapter = new SlideInBottomAnimationAdapter(mAdapter);
        slideInBottomAnimationAdapter.setDuration(300);
        mRecyclerView.setAdapter(slideInBottomAnimationAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new FadeInRightAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(0);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mFab.show();
                }
                else {
                    mFab.hide();
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                final long id = mTodos.get(position).getId();

                if (direction == ItemTouchHelper.RIGHT) {

                    mTodos.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    updateTodoDoneIntoDB(id,true);

                    Snackbar doneSnackbar = Snackbar.make(viewHolder.itemView, "Todo Done.", Snackbar.LENGTH_LONG);
                    doneSnackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mTodos.add(position,getTodofromIDfromDB(id));
                            mAdapter.notifyItemInserted(position);
                            updateTodoDoneIntoDB(id,false);
                        }
                    });
                    doneSnackbar.show();
                }
                else {
                    // TODO show date dialog..
                    final Calendar currentDateTime = Calendar.getInstance();
                    final Calendar todoDateTime = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, final int year, final int month, final int dayOfMonth) {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                                    todoDateTime.set(year,month,dayOfMonth,hourOfDay,minute);

                                    mTodos.get(position).setDate(todoDateTime);
                                    mAdapter.notifyItemChanged(position);
                                    changeTodoDateIntoDB(id,todoDateTime);
                                }
                            },currentDateTime.get(Calendar.HOUR_OF_DAY),currentDateTime.get(Calendar.MINUTE),DateFormat.is24HourFormat(MainActivity.this));
                            timePickerDialog.show();
                        }
                    },currentDateTime.get(Calendar.YEAR),currentDateTime.get(Calendar.MONTH),currentDateTime.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();

                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX > 0) {
                        Paint p = new Paint();
                        p.setColor(Color.parseColor("#388E3C")); //700
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        c.clipRect(background);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_done_white_48dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(bitmap, null, icon_dest, p);
                        c.restore();
                    } else {
                        Paint p = new Paint();
                        p.setColor(Color.parseColor("#FFA000")); //700
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        c.clipRect(background);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_date_range_white_48dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(bitmap, null, icon_dest, p);
                        c.restore();
                    }
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    private void initFab() {

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder aleartDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                aleartDialogBuilder.setTitle("Add Todo");

                View dialogView = getLayoutInflater().inflate(R.layout.add_dialog_box, null);
                final EditText todoEditText = (EditText) dialogView.findViewById(R.id.todoEditText);
                aleartDialogBuilder.setView(dialogView);

                aleartDialogBuilder.setPositiveButton("Done", null);
                final AlertDialog todoAddAlertDialog = aleartDialogBuilder.create();
                todoAddAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                todoAddAlertDialog.show();
                todoAddAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (todoEditText.getText().toString().trim().isEmpty()) {
                            todoEditText.setError("This field cannot be empty.");
                            return;
                        }

                        long id = insertTodoIntoDB(todoEditText.getText().toString());
                        Todo todo = new Todo(id, todoEditText.getText().toString(),Long.MIN_VALUE);

                        mTodos.add(0,todo);
                        mAdapter.notifyDataSetChanged();

                        // todo add in sorted order.
                        //

                        // todo notification.
                        // if isTimeSet==false set time for notification now()+something.

                        todoAddAlertDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // TODO Search not working.
        // todo hide fab when searching

        MenuItem searchMenuItem = menu.findItem(R.id.search_item);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(MainActivity.this, "onQueryTextChange", Toast.LENGTH_SHORT).show();
                // TODO: search not working.
                mTodos = fetchTodosFromDB();

                if(newText.trim().isEmpty()) {
                    mTodos = fetchTodosFromDB();
                    mAdapter.notifyDataSetChanged();
                    return false;
                }

                for(int i=0; i < mTodos.size(); i++) {
                    if(!mTodos.get(i).getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        mTodos.remove(i);
                        i--;
                    }
                }

                mAdapter.notifyDataSetChanged();

                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MainActivity.this, "onClose", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Todo> fetchTodosFromDB() {
        ArrayList<Todo> todoArrayList = new ArrayList<>();
        TodoDBHelper todoDBHelper = new TodoDBHelper(MainActivity.this);
        SQLiteDatabase database = todoDBHelper.getReadableDatabase();
        Cursor cursor = database.query(TodoDBHelper.TABLE_NAME, null, TodoDBHelper.TODO_DONE + " = " + TodoDBHelper.FALSE, null, null, null, TodoDBHelper._ID + " DESC");
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(TodoDBHelper._ID));
            String todoTitle = cursor.getString(cursor.getColumnIndex(TodoDBHelper.TODO_TITLE));
            long todoDateTime = cursor.getLong(cursor.getColumnIndex(TodoDBHelper.TODO_DATE));
            Calendar todoDate = Calendar.getInstance();
            todoDate.setTimeInMillis(todoDateTime);
            todoArrayList.add(new Todo(id, todoTitle, todoDate));
        }
        return todoArrayList;
    }

    private long insertTodoIntoDB(String title) {

        TodoDBHelper todoDBHelper = new TodoDBHelper(MainActivity.this);
        SQLiteDatabase database = todoDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoDBHelper.TODO_TITLE, title);
        contentValues.put(TodoDBHelper.TODO_DATE, Long.MIN_VALUE);
        contentValues.put(TodoDBHelper.TODO_DONE, TodoDBHelper.FALSE);
        return database.insert(TodoDBHelper.TABLE_NAME, null, contentValues);
    }

    private void updateTodoDoneIntoDB(long id, boolean isDone) {

        TodoDBHelper todoDBHelper = new TodoDBHelper(MainActivity.this);
        SQLiteDatabase database = todoDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        if(isDone)
            contentValues.put(TodoDBHelper.TODO_DONE, TodoDBHelper.TRUE);
        else
            contentValues.put(TodoDBHelper.TODO_DONE, TodoDBHelper.FALSE);

        database.update(TodoDBHelper.TABLE_NAME, contentValues, TodoDBHelper._ID + " = " + id, null);
    }

    private Todo getTodofromIDfromDB(long id) {

        TodoDBHelper todoDBHelper = new TodoDBHelper(MainActivity.this);
        SQLiteDatabase database = todoDBHelper.getReadableDatabase();

        Cursor cursor = database.query(TodoDBHelper.TABLE_NAME, null, TodoDBHelper._ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();

        String todoTitle = cursor.getString(cursor.getColumnIndex(TodoDBHelper.TODO_TITLE));
        Long dateTimeInMillis = cursor.getLong(cursor.getColumnIndex(TodoDBHelper.TODO_DATE));
        Todo todo = new Todo(id, todoTitle, dateTimeInMillis);

        return todo;
    }

    private void changeTodoDateIntoDB(long id, Calendar todoDateTime) {

        TodoDBHelper todoDBHelper = new TodoDBHelper(MainActivity.this);
        SQLiteDatabase database = todoDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoDBHelper.TODO_DATE, todoDateTime.getTimeInMillis());

        database.update(TodoDBHelper.TABLE_NAME, contentValues, TodoDBHelper._ID + " = " + id, null);
    }

}
