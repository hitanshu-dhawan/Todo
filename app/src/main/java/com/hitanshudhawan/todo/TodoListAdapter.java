package com.hitanshudhawan.todo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.Annotation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by hitanshu on 17/7/17.
 */

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

    private Context mContext;
    private ArrayList<Todo> mTodos;

    public TodoListAdapter(Context context, ArrayList<Todo> todos) {
        mContext = context;
        mTodos = todos;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.todo_list_item,parent,false);
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        Todo todo = mTodos.get(position);
        holder.mTodoTitleTextView.setText(todo.getTitle());
        holder.mTodoDateTextView.setText(getStringDate(todo.getDate()));
    }

    @Override
    public int getItemCount() {
        return mTodos.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        public TextView mTodoTitleTextView;
        public TextView mTodoDateTextView;

        public TodoViewHolder(View itemView) {
            super(itemView);
            mTodoTitleTextView = itemView.findViewById(R.id.todoTitleTextView);
            mTodoDateTextView = itemView.findViewById(R.id.todoDateTextView);
        }
    }

    private String getStringDate(Calendar todoDateTime) {

        if(todoDateTime.getTimeInMillis() == Long.MIN_VALUE)
            return "!...!";

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
