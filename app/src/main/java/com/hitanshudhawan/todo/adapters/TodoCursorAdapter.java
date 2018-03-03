package com.hitanshudhawan.todo.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitanshudhawan.todo.R;
import com.hitanshudhawan.todo.activities.TodoDetailsActivity;
import com.hitanshudhawan.todo.database.Todo;
import com.hitanshudhawan.todo.utils.Constant;
import com.hitanshudhawan.todo.utils.RecyclerViewCursorAdapter;

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
        return new TodoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false));
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, Cursor cursor) {
        final Todo todo = Todo.fromCursor(cursor);
        holder.todoTitleTextView.setText(todo.getTitle().replace("\n", " "));
        holder.todoDateTimeTextView.setText(todo.getDateTime().getTimeInMillis() + "");
        holder.todoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TodoDetailsActivity.class);
                intent.putExtra(Constant.TODO_ID, todo.getId());
                context.startActivity(intent);
            }
        });
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout todoLayout;
        public TextView todoTitleTextView;
        public TextView todoDateTimeTextView;

        public TodoViewHolder(View itemView) {
            super(itemView);
            todoLayout = (ConstraintLayout) itemView.findViewById(R.id.todo_layout_item);
            todoTitleTextView = (TextView) itemView.findViewById(R.id.todo_title_text_view_item);
            todoDateTimeTextView = (TextView) itemView.findViewById(R.id.todo_date_time_text_view_item);
        }
    }

}
