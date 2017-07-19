package com.hitanshudhawan.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hitanshudhawan.todo.database.TodoDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.hitanshudhawan.todo.R.id.add;
import static com.hitanshudhawan.todo.R.id.fab;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ArrayList<Todo> mTodos;
    TodoListAdapter mAdapter;

    SearchView mSearchView;
    FloatingActionButton mFab;


    boolean isDateSet = false;
    boolean isTimeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.todoRecyclerView);
        mFab = (FloatingActionButton) findViewById(fab);

        initRecyclerView();
        initFab();
    }

    private void initRecyclerView() {

        mTodos = fetchTodosFromDB();

        mAdapter = new TodoListAdapter(MainActivity.this, mTodos);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    mFab.show();
                else
                    mFab.hide();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    // TODO delete_todo
                    Snackbar.make(viewHolder.itemView,"Todo Deleted",Snackbar.LENGTH_LONG).show();
                } else {
                    // TODO done_todo
                    Snackbar.make(mRecyclerView,"Todo Done",Snackbar.LENGTH_LONG).show();
                }

                mTodos.remove(position);
                mAdapter.notifyItemRemoved(position);
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
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        c.clipRect(background);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_done_white_48dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(bitmap, null, icon_dest, p);
                        c.restore();
                    } else {
                        Paint p = new Paint();
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        c.clipRect(background);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_delete_white_48dp);
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

                isDateSet = false;
                isTimeSet = false;

                AlertDialog.Builder aleartDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                aleartDialogBuilder.setTitle("Add Todo");

                View dialogView = getLayoutInflater().inflate(R.layout.add_dialog_box,null);
                final EditText todoEditText = (EditText) dialogView.findViewById(R.id.todoEditText);
                final ImageButton dateImageButton = (ImageButton) dialogView.findViewById(R.id.datePickerButton);
                final ImageButton timeImageButton = (ImageButton) dialogView.findViewById(R.id.timePickerButton);
                timeImageButton.setEnabled(false);
                timeImageButton.setColorFilter(Color.GRAY);
                final TextView dateTextView = (TextView) dialogView.findViewById(R.id.dateTextView);
                final TextView timeTextView = (TextView) dialogView.findViewById(R.id.timeTextView);

                final Calendar todoDateTime = Calendar.getInstance();

                dateImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar currentDate = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                isDateSet = true;
                                timeImageButton.setEnabled(true);
                                timeImageButton.setColorFilter(Color.parseColor("#D50000"));
                                timeTextView.setText("All Day");
                                todoDateTime.set(year,month,dayOfMonth,9,0);
                                dateTextView.setText(getStringDate(todoDateTime));
                            }
                        },currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),currentDate.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    }
                });

                timeImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar currentTime = Calendar.getInstance();
                        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                isTimeSet = true;
                                todoDateTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                todoDateTime.set(Calendar.MINUTE,minute);
                                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                                timeTextView.setText(sdf.format(todoDateTime.getTime()));
                            }
                        },currentTime.get(Calendar.HOUR_OF_DAY),currentTime.get(Calendar.MINUTE),false);
                        timePickerDialog.show();
                    }
                });


                aleartDialogBuilder.setView(dialogView);
                aleartDialogBuilder.setPositiveButton("Done", null);
                final AlertDialog alertDialog = aleartDialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(todoEditText.getText().toString().trim().isEmpty()) {
                            todoEditText.setError("This field cannot be empty.");
                            return;
                        }

                        long id = insertTodoIntoDB(todoEditText.getText().toString(),todoDateTime,isDateSet);
                        Todo todo;
                        if(isDateSet) {
                            todo = new Todo(id, todoEditText.getText().toString(), todoDateTime);
                        }
                        else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.MAX_VALUE);
                            todo = new Todo(id, todoEditText.getText().toString(),calendar);
                        }

                        boolean added = false;
                        for(int i=0;i<mTodos.size();i++) {
                            if(mTodos.get(i).getDate().getTimeInMillis() >= todo.getDate().getTimeInMillis()) {
                                added = true;
                                mTodos.add(i, todo);
                                mAdapter.notifyItemInserted(i);
                                break;
                            }
                        }
                        if(!added) {
                            mTodos.add(todo);
                            mAdapter.notifyItemInserted(mTodos.size()-1);
                        }
                        
                        // todo notification.
                        // if isTimeSet==false set time for notification now()+something.

                        alertDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search_item);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
        Cursor cursor = database.query(TodoDBHelper.TABLE_NAME, null, null, null, null, null, TodoDBHelper.TODO_DATE);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(TodoDBHelper._ID));
            String todoTitle = cursor.getString(cursor.getColumnIndex(TodoDBHelper.TODO_TITLE));
            long todoDateTime = cursor.getLong(cursor.getColumnIndex(TodoDBHelper.TODO_DATE));
            Calendar todoDate = Calendar.getInstance();
            todoDate.setTimeInMillis(todoDateTime);
            todoArrayList.add(new Todo(id,todoTitle,todoDate));
        }
        return todoArrayList;
    }

    private long insertTodoIntoDB(String title, Calendar todoDateTime, boolean isDateSet) {

        TodoDBHelper todoDBHelper = new TodoDBHelper(MainActivity.this);
        SQLiteDatabase database = todoDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoDBHelper.TODO_TITLE,title);
        if(isDateSet)
            contentValues.put(TodoDBHelper.TODO_DATE,todoDateTime.getTimeInMillis());
        else
            contentValues.put(TodoDBHelper.TODO_DATE,Long.MAX_VALUE);
        return database.insert(TodoDBHelper.TABLE_NAME,null,contentValues);
    }

    private String getStringDate(Calendar todoDateTime) {

        if(todoDateTime.getTimeInMillis() == Long.MAX_VALUE)
            return "";

        Calendar calendar;

        calendar = Calendar.getInstance();
        if(todoDateTime.getTimeInMillis() < calendar.getTimeInMillis())
            return "Overdue";

        calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) == todoDateTime.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == todoDateTime.get(Calendar.DAY_OF_YEAR))
            return "Today";

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        if(calendar.get(Calendar.YEAR) == todoDateTime.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == todoDateTime.get(Calendar.DAY_OF_YEAR))
            return "Tomorrow";

        calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) == todoDateTime.get(Calendar.YEAR) && calendar.get(Calendar.WEEK_OF_YEAR) == todoDateTime.get(Calendar.WEEK_OF_YEAR))
            return "This Week";

        calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR,1);
        if(calendar.get(Calendar.YEAR) == todoDateTime.get(Calendar.YEAR) && calendar.get(Calendar.WEEK_OF_YEAR) == todoDateTime.get(Calendar.WEEK_OF_YEAR))
            return "Next Week";

        calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) == todoDateTime.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == todoDateTime.get(Calendar.MONTH))
            return "This Month";

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        if(calendar.get(Calendar.YEAR) == todoDateTime.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == todoDateTime.get(Calendar.MONTH))
            return "Next Month";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        return sdf.format(todoDateTime.getTime());
    }

}
