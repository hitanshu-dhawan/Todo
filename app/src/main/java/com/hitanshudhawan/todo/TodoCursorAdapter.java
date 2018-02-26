package com.hitanshudhawan.todo;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitanshudhawan.todo.database.Todo;

/**
 * Created by hitanshu on 17/7/17.
 */

public class TodoCursorAdapter extends RecyclerViewCursorAdapter<TodoCursorAdapter.TodoViewHolder> {

    Context context;

    public TodoCursorAdapter(Context context) {
        this.context = context;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TodoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_todo,parent,false));
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, Cursor cursor) {
        Todo todo = Todo.fromCursor(cursor);
        holder.todoTitleTextView.setText(todo.getTitle());
        holder.todoDateTimeTextView.setText(todo.getDateTime().getTimeInMillis()+"");
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        public TextView todoTitleTextView;
        public TextView todoDateTimeTextView;

        public TodoViewHolder(View itemView) {
            super(itemView);
            todoTitleTextView = (TextView) itemView.findViewById(R.id.todo_title_text_view_item);
            todoDateTimeTextView = (TextView) itemView.findViewById(R.id.todo_date_time_text_view_item);
        }
    }

}
