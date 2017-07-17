package com.hitanshudhawan.todo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

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
        holder.mTodoDescriptionTextView.setText(todo.getDescription());
    }

    @Override
    public int getItemCount() {
        return mTodos.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        public TextView mTodoTitleTextView;
        public TextView mTodoDescriptionTextView;
        public TextView mTodoDateTextView;

        public TodoViewHolder(View itemView) {
            super(itemView);
            mTodoTitleTextView = itemView.findViewById(R.id.todoTitleTextView);
            mTodoDescriptionTextView = itemView.findViewById(R.id.todoDescriptionTextView);
            mTodoDateTextView = itemView.findViewById(R.id.todoDateTextView);
        }
    }

}
