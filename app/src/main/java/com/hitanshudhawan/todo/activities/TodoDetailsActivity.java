package com.hitanshudhawan.todo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.hitanshudhawan.todo.utils.Constants;
import com.hitanshudhawan.todo.utils.NotificationHelper;
import com.hitanshudhawan.todo.utils.WidgetHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TodoDetailsActivity extends AppCompatActivity {

    private EditText mTodoEditText;
    private TextView mTodoDateTimeTextView;

    private Long mTodoId;
    private Todo mTodo;
    private String mTodoTitle;
    private Calendar mTodoDateTime;
    private Boolean mDateTimeChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTodoEditText = findViewById(R.id.todo_edit_text_todo_details);
        mTodoDateTimeTextView = findViewById(R.id.todo_date_time_text_view_todo_details);

        init(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        mTodoId = intent.getLongExtra(Constants.TODO_ID, -1);
        if (mTodoId == -1)
            finishAndRemoveTask();
        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, mTodoId), null, null, null, null);
        cursor.moveToFirst();
        mTodo = Todo.fromCursor(cursor);
        mTodoTitle = mTodo.getTitle();
        mTodoDateTime = mTodo.getDateTime();
        mDateTimeChanged = false;

        mTodoEditText.setText(mTodoTitle);

        mTodoDateTimeTextView.setText(mTodoDateTime.getTimeInMillis() == 0 ? "" : DateFormat.is24HourFormat(TodoDetailsActivity.this) ? new SimpleDateFormat("MMMM dd, yyyy  h:mm").format(mTodoDateTime.getTime()) : new SimpleDateFormat("MMMM dd, yyyy  h:mm a").format(mTodoDateTime.getTime()));
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(TodoDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        DatePickerDialog datePickerDialog = new DatePickerDialog(TodoDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                mTodoDateTime.set(Calendar.YEAR, year);
                                mTodoDateTime.set(Calendar.MONTH, month);
                                mTodoDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                mTodoDateTimeTextView.setText(DateFormat.is24HourFormat(TodoDetailsActivity.this) ? new SimpleDateFormat("MMMM dd, yyyy  h:mm").format(mTodoDateTime.getTime()) : new SimpleDateFormat("MMMM dd, yyyy  h:mm a").format(mTodoDateTime.getTime()));
                                mDateTimeChanged = true;
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
                        getContentResolver().update(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, mTodoId), contentValues, null, null);

                        WidgetHelper.updateWidget(TodoDetailsActivity.this);

                        Toast.makeText(TodoDetailsActivity.this, "Todo Done.", Toast.LENGTH_SHORT).show();

                        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, mTodoId), null, null, null, null);
                        cursor.moveToFirst();
                        new NotificationHelper(TodoDetailsActivity.this).cancelScheduledNotification(mTodoId, Todo.fromCursor(cursor).getTitle());

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
        mTodoTitle = mTodoEditText.getText().toString().trim();
        if (!mTodoTitle.equals("")) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_TITLE, mTodoTitle);
            if (mDateTimeChanged)
                contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DATE_TIME, mTodoDateTime == null ? 0 : mTodoDateTime.getTimeInMillis());
            contentValues.put(TodoContract.TodoEntry.COLUMN_TODO_DONE, TodoContract.TodoEntry.TODO_NOT_DONE);
            getContentResolver().update(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, mTodoId), contentValues, null, null);

            WidgetHelper.updateWidget(TodoDetailsActivity.this);

            if (mTodoDateTime != null && mTodoDateTime.getTimeInMillis() != 0) {
                if (mTodoDateTime.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
                    Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI, mTodoId), null, null, null, null);
                    cursor.moveToFirst();
                    new NotificationHelper(TodoDetailsActivity.this).scheduleNotification(mTodoId, Todo.fromCursor(cursor).getTitle(), mTodoDateTime.getTimeInMillis());
                }
            }
        }
        finishAndRemoveTask();
    }
}
