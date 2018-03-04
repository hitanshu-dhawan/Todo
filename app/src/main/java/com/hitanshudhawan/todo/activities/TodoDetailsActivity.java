package com.hitanshudhawan.todo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hitanshudhawan.todo.R;
import com.hitanshudhawan.todo.database.Todo;
import com.hitanshudhawan.todo.database.TodoContract;
import com.hitanshudhawan.todo.utils.Constant;
import com.hitanshudhawan.todo.widget.TodoWidget;

import java.util.Calendar;

public class TodoDetailsActivity extends AppCompatActivity {

    private EditText todoEditText;
    private TextView todoDateTimeTextView;

    private Long todoId;
    private Todo todo;
    private String todoTitle;
    private Calendar todoDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        todoEditText = (EditText) findViewById(R.id.todo_edit_text_todo_details);
        todoDateTimeTextView = (TextView) findViewById(R.id.todo_date_time_text_view_todo_details);

        todoId = getIntent().getLongExtra(Constant.TODO_ID, -1);
        if (todoId == -1)
            finishAndRemoveTask();
        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, todoId), null, null, null, null);
        cursor.moveToFirst();
        todo = Todo.fromCursor(cursor);
        todoTitle = todo.getTitle();
        todoDateTime = todo.getDateTime();

        todoEditText.setText(todoTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.todo_date_time_item_todo_details:
                final Calendar currentDateTime = Calendar.getInstance();
                // todoDateTime = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(TodoDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        todoDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        todoDateTime.set(Calendar.MINUTE, minute);
                        int year, month, dayOfMonth;
                        if (todoDateTime.get(Calendar.HOUR_OF_DAY) * 60 + todoDateTime.get(Calendar.MINUTE) < currentDateTime.get(Calendar.HOUR_OF_DAY) * 60 + currentDateTime.get(Calendar.MINUTE)) {
                            currentDateTime.add(Calendar.DATE, 1);
                            year = currentDateTime.get(Calendar.YEAR);
                            month = currentDateTime.get(Calendar.MONTH);
                            dayOfMonth = currentDateTime.get(Calendar.DAY_OF_MONTH);
                            currentDateTime.add(Calendar.DATE, -1);
                        } else {
                            year = currentDateTime.get(Calendar.YEAR);
                            month = currentDateTime.get(Calendar.MONTH);
                            dayOfMonth = currentDateTime.get(Calendar.DAY_OF_MONTH);
                        }
                        DatePickerDialog datePickerDialog = new DatePickerDialog(TodoDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                todoDateTime.set(Calendar.YEAR, year);
                                todoDateTime.set(Calendar.MONTH, month);
                                todoDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            }
                        }, year, month, dayOfMonth);
                        Calendar minDateTime = Calendar.getInstance();
                        minDateTime.set(year, month, dayOfMonth);
                        datePickerDialog.getDatePicker().setMinDate(minDateTime.getTimeInMillis());
                        datePickerDialog.show();
                    }
                }, currentDateTime.get(Calendar.HOUR_OF_DAY), currentDateTime.get(Calendar.MINUTE), DateFormat.is24HourFormat(TodoDetailsActivity.this));
                timePickerDialog.show();
                break;
            case R.id.todo_done_item_todo_add:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TodoDetailsActivity.this);
                alertDialogBuilder.setTitle("Todo Done?");
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DONE, TodoContract.TodoEntry.TODO_DONE);
                        getContentResolver().update(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, todoId), contentValues, null, null);
                        sendBroadcast(new Intent(TodoDetailsActivity.this, TodoWidget.class).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
                        Toast.makeText(TodoDetailsActivity.this, "Todo Done.", Toast.LENGTH_SHORT).show();
                        finishAndRemoveTask();
                    }
                });
                alertDialogBuilder.create().show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        todoTitle = todoEditText.getText().toString().trim();
        if (!todoTitle.equals("")) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_TITLE, todoTitle);
            contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME, todoDateTime == null ? 0 : todoDateTime.getTimeInMillis());
            contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DONE, TodoContract.TodoEntry.TODO_NOT_DONE);
            getContentResolver().update(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, todoId), contentValues, null, null);
            sendBroadcast(new Intent(TodoDetailsActivity.this, TodoWidget.class).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
        }
        finishAndRemoveTask();
    }
}