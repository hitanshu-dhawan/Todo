package com.hitanshudhawan.todo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hitanshudhawan.todo.R;
import com.hitanshudhawan.todo.models.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by hitanshu on 17/7/17.
 */

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

    private Context mContext;
    private ArrayList<Todo> mTodos;
    private ArrayList<Todo> mSelectedTodos;

    public TodoListAdapter(Context context, ArrayList<Todo> todos, ArrayList<Todo> selectedTodos) {
        mContext = context;
        mTodos = todos;
        mSelectedTodos = selectedTodos;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.todo_list_item,parent,false);
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        Todo todo = mTodos.get(position);
        holder.mTodoTitleTextView.setText(todo.getTitle().replaceAll("\n"," "));
        holder.mTodoDateTextView.setText(getStringDate(todo.getDate()));
        if(holder.mTodoDateTextView.getText().toString().equals("Overdue"))
            holder.mTodoDateTextView.setTextColor(Color.RED);
        else
            holder.mTodoDateTextView.setTextColor(Color.GRAY);
        if(mSelectedTodos.contains(mTodos.get(position)))
            holder.mTodoListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTodoSelected));
        else
            holder.mTodoListItem.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return mTodos.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mTodoListItem;
        public TextView mTodoTitleTextView;
        public TextView mTodoDateTextView;

        public TodoViewHolder(View itemView) {
            super(itemView);
            mTodoListItem = (RelativeLayout) itemView.findViewById(R.id.todoListItem);
            mTodoTitleTextView = (TextView) itemView.findViewById(R.id.todoTitleTextView);
            mTodoDateTextView = (TextView) itemView.findViewById(R.id.todoDateTextView);
        }
    }

    private String getStringDate(Calendar todoDateTime) {

        if(todoDateTime.getTimeInMillis() == Long.MIN_VALUE)
            return "";

        Calendar calendar;

        calendar = Calendar.getInstance();
        if(todoDateTime.getTimeInMillis() < calendar.getTimeInMillis())
            return "Overdue";

        calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) == todoDateTime.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == todoDateTime.get(Calendar.DAY_OF_YEAR)) {
            SimpleDateFormat simpleDateFormat;
            if(DateFormat.is24HourFormat(mContext))
                simpleDateFormat = new SimpleDateFormat("h:mm");
            else
                simpleDateFormat = new SimpleDateFormat("h:mm a");
            return "Today, " +simpleDateFormat.format(todoDateTime.getTime());
        }

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        if(calendar.get(Calendar.YEAR) == todoDateTime.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == todoDateTime.get(Calendar.DAY_OF_YEAR)) {
            SimpleDateFormat simpleDateFormat;
            if(DateFormat.is24HourFormat(mContext))
                simpleDateFormat = new SimpleDateFormat("h:mm");
            else
                simpleDateFormat = new SimpleDateFormat("h:mm a");
            return "Tomorrow, " +simpleDateFormat.format(todoDateTime.getTime());
        }

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
