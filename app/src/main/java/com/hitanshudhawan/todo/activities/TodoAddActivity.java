package com.hitanshudhawan.todo.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.hitanshudhawan.todo.broadcastreceivers.AlarmReceiver;
import com.hitanshudhawan.todo.database.Todo;
import com.hitanshudhawan.todo.database.TodoContract;
import com.hitanshudhawan.todo.widget.TodoWidget;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TodoAddActivity extends AppCompatActivity {

    private EditText mTodoEditText;
    private TextView mTodoDateTimeTextView;

    private String mTodoTitle;
    private Calendar mTodoDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTodoEditText = (EditText) findViewById(R.id.todo_edit_text_todo_add);
        mTodoDateTimeTextView = (TextView) findViewById(R.id.todo_date_time_text_view_todo_add);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.todo_date_time_item_todo_add:
                final Calendar currentDateTime = Calendar.getInstance();
                mTodoDateTime = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(TodoAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mTodoDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mTodoDateTime.set(Calendar.MINUTE, minute);
                        int year, month, dayOfMonth;
                        if (mTodoDateTime.get(Calendar.HOUR_OF_DAY) * 60 + mTodoDateTime.get(Calendar.MINUTE) < currentDateTime.get(Calendar.HOUR_OF_DAY) * 60 + currentDateTime.get(Calendar.MINUTE)) {
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
                        DatePickerDialog datePickerDialog = new DatePickerDialog(TodoAddActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                mTodoDateTime.set(Calendar.YEAR, year);
                                mTodoDateTime.set(Calendar.MONTH, month);
                                mTodoDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                mTodoDateTimeTextView.setText(DateFormat.is24HourFormat(TodoAddActivity.this) ? new SimpleDateFormat("MMMM dd, yyyy  h:mm").format(mTodoDateTime.getTime()) : new SimpleDateFormat("MMMM dd, yyyy  h:mm a").format(mTodoDateTime.getTime()));
                            }
                        }, year, month, dayOfMonth);
                        Calendar minDateTime = Calendar.getInstance();
                        minDateTime.set(year, month, dayOfMonth);
                        datePickerDialog.getDatePicker().setMinDate(minDateTime.getTimeInMillis());
                        datePickerDialog.show();
                    }
                }, currentDateTime.get(Calendar.HOUR_OF_DAY), currentDateTime.get(Calendar.MINUTE), DateFormat.is24HourFormat(TodoAddActivity.this));
                timePickerDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mTodoTitle = mTodoEditText.getText().toString().trim();
        if (!mTodoTitle.equals("")) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_TITLE, mTodoTitle);
            contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME, mTodoDateTime == null ? 0 : mTodoDateTime.getTimeInMillis());
            contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DONE, TodoContract.TodoEntry.TODO_NOT_DONE);
            Uri uri = getContentResolver().insert(TodoContract.TodoEntry.CONTENT_URI, contentValues);
            sendBroadcast(new Intent(TodoAddActivity.this, TodoWidget.class).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
            Toast.makeText(TodoAddActivity.this, "Todo added", Toast.LENGTH_SHORT).show();
            // Notification
            if (mTodoDateTime != null) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                long id = ContentUris.parseId(uri);
                Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, id), null, null, null, null);
                cursor.moveToFirst();
                String body = Todo.fromCursor(cursor).getTitle();
                Intent intent = new Intent(TodoAddActivity.this, AlarmReceiver.class);
                intent.putExtra("id", id);
                intent.putExtra("body", body);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoAddActivity.this, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, mTodoDateTime.getTimeInMillis(), pendingIntent);
            }
        }
        finishAndRemoveTask();
    }
}
