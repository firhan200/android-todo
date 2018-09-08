package com.learning.firhan.todo.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.learning.firhan.todo.Interfaces.IMainActivity;
import com.learning.firhan.todo.Interfaces.ITodoSQliteHelper;
import com.learning.firhan.todo.Models.TodoItem;
import com.learning.firhan.todo.R;

import java.util.ArrayList;

public class TodoRecyclerAdapter extends RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder> {
    private static final String TAG = "TodoRecyclerAdapter";

    ArrayList<TodoItem> todoItems;
    Context context;

    ITodoSQliteHelper iTodoSQliteHelper;
    IMainActivity iMainActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout todoItemLayout;
        TextView todoTitle, todoDescription;
        ImageView moreButton;

        public ViewHolder(View v) {
            super(v);
            todoItemLayout = (LinearLayout)v.findViewById(R.id.todoItemLayout);
            todoTitle = (TextView)v.findViewById(R.id.todoTitle);
            todoDescription = (TextView)v.findViewById(R.id.todoDescription);
            moreButton = (ImageView)v.findViewById(R.id.moreButton);
        }
    }

    public TodoRecyclerAdapter(Context applicationContext, ArrayList<TodoItem> todoItems) {
        this.todoItems = todoItems;
        this.context = applicationContext;

        iMainActivity = (IMainActivity)context;
        iTodoSQliteHelper = (ITodoSQliteHelper)context;
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
        Log.d(TAG, "onBindViewHolder: "+todoItem.getTitle()+" = "+todoItem.getSelected());
        viewHolder.todoTitle.setText(todoItem.getTitle());
        viewHolder.todoDescription.setText(todoItem.getDesciption());

        //check if selected or not
        if(!todoItem.getSelected()){
            viewHolder.todoItemLayout.setBackground(context.getResources().getDrawable(R.drawable.shapes_todo_list));
        }else{
            viewHolder.todoItemLayout.setBackgroundColor(context.getResources().getColor(R.color.todoListSelectedBackground));
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(todoItem.getCollapsed()){
                    todoItem.setCollapsed(false);
                    //expand
                    viewHolder.todoTitle.setMaxLines(20);
                    viewHolder.todoDescription.setMaxLines(200);
                }else{
                    todoItem.setCollapsed(true);
                    //collapse
                    viewHolder.todoTitle.setMaxLines(1);
                    viewHolder.todoDescription.setMaxLines(1);
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(todoItem.getSelected()){
                    //unselect
                    iMainActivity.removeFromSelectedItemList(todoItem);
                    setItemSelected(todoItem, viewHolder.todoItemLayout, true);
                }else{
                    //select
                    iMainActivity.addToSelectedItemList(todoItem);
                    setItemSelected(todoItem, viewHolder.todoItemLayout, false);
                }

                return true;
            }
        });

        viewHolder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopupMenu(v, todoItem);
            }
        });
    }

    private void setItemSelected(TodoItem todoItem, LinearLayout layout, Boolean isSelected){
        if(isSelected){
            todoItem.setSelected(false);
            layout.setBackground(context.getResources().getDrawable(R.drawable.shapes_todo_list));
        }else{
            todoItem.setSelected(true);
            iMainActivity.addToSelectedItemList(todoItem);
            layout.setBackgroundColor(context.getResources().getColor(R.color.todoListSelectedBackground));
        }
    }

    @Override
    public int getItemCount() {
        int total = todoItems.size();
        return total;
    }

    private void setPopupMenu(View view, final TodoItem todoItem){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_edit_delete, popupMenu.getMenu());

        //set click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.editItemButton){
                    //edit item
                    iTodoSQliteHelper.editTodo(todoItem);
                }else if(menuItem.getItemId()==R.id.deleteItemButton){
                    //delete item
                    iTodoSQliteHelper.deleteTodo(todoItem.getId());
                }

                return false;
            }
        });

        popupMenu.show();
    }
}
