package com.learning.firhan.todo.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.learning.firhan.todo.Fragments.TodoDetailFragment;
import com.learning.firhan.todo.MainActivity;
import com.learning.firhan.todo.Models.TodoItem;
import com.learning.firhan.todo.R;

import java.util.ArrayList;

public class TodoRecyclerAdapter extends RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder> {
    private static final String TAG = "TodoRecyclerAdapter";

    ArrayList<TodoItem> todoItems;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout todoItemLayout;
        public TextView todoTitle, todoDescription;
        public ViewHolder(View v) {
            super(v);
            todoItemLayout = (LinearLayout)v.findViewById(R.id.todoItemLayout);
            todoTitle = (TextView)v.findViewById(R.id.todoTitle);
            todoDescription = (TextView)v.findViewById(R.id.todoDescription);
        }
    }

    public TodoRecyclerAdapter(Context applicationContext, ArrayList<TodoItem> todoItems) {
        this.todoItems = todoItems;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public TodoRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int i) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_item_todo, parent, false);

        TodoRecyclerAdapter.ViewHolder vh = new TodoRecyclerAdapter.ViewHolder(itemView);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final TodoItem todoItem = todoItems.get(i);
        todoItem.setSelected(false);
        viewHolder.todoTitle.setText(todoItem.getTitle());
        viewHolder.todoDescription.setText(todoItem.getDesciption());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("todo", todoItem);
                ((MainActivity)context).setFragment(context.getString(R.string.todo_detail_fragment), true, bundle);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(todoItem.getSelected()){
                    //unselect
                    todoItem.setSelected(false);
                    viewHolder.todoItemLayout.setBackground(context.getResources().getDrawable(R.drawable.shapes_todo_list));
                }else{
                    //select
                    todoItem.setSelected(true);
                    viewHolder.todoItemLayout.setBackgroundColor(context.getResources().getColor(R.color.todoListSelectedBackground));
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        int total = todoItems.size();
        return total;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d(TAG, "onAttachedToRecyclerView: ");
    }
}
