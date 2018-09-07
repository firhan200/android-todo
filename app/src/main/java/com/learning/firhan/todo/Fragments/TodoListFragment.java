package com.learning.firhan.todo.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learning.firhan.todo.Helpers.TodoDatabaseHelper;
import com.learning.firhan.todo.Interfaces.IMainActivity;
import com.learning.firhan.todo.MainActivity;
import com.learning.firhan.todo.Models.TodoItem;
import com.learning.firhan.todo.R;
import com.learning.firhan.todo.Adapters.TodoRecyclerAdapter;

import java.util.ArrayList;

public class TodoListFragment extends Fragment {
    private static final String TAG = "TodoListFragment";

    TodoDatabaseHelper todoDatabaseHelper;
    TextView noResultsLabel;
    ArrayList<TodoItem> todoItems;
    IMainActivity iMainActivity;

    //RecyclerView Attributes
    private RecyclerView mRecyclerView;
    TodoRecyclerAdapter todoRecyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        initIds(view);
        initHelpers();
        initTodoListView();
        populateTodoList();

        return view;
    }

    private void initIds(View view){
        noResultsLabel = (TextView)view.findViewById(R.id.noResultsLabel);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.todoRecyclerList);
    }

    private void initHelpers(){
        todoDatabaseHelper = new TodoDatabaseHelper(getActivity());
    }

    private void initTodoListView(){
        //init new list
        todoItems = new ArrayList<TodoItem>();

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        //adapter
        todoRecyclerAdapter = new TodoRecyclerAdapter(getActivity(), todoItems);
        mRecyclerView.setAdapter(todoRecyclerAdapter);
    }

    public void populateTodoList(){
        todoItems.clear();
        getTodoItemsFromDatabase();
        if(todoItems.size() > 0){
            noResultsLabel.setVisibility(View.GONE);
        }else{
            noResultsLabel.setVisibility(View.VISIBLE);
        }
    }

    private void getTodoItemsFromDatabase(){
        //get all data from SQLite
        Cursor result = todoDatabaseHelper.getAllData();
        if(result.getCount() > 0){
            //there is some data
            while(result.moveToNext()){
                todoItems.add(new TodoItem(result.getInt(0) ,result.getString(1), result.getString(2)));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        iMainActivity = (MainActivity)getActivity();
        iMainActivity.setToolbarTitle("Todo");
        iMainActivity.hasBackButton(false);
    }

    public void addTodo(TodoItem todoItem){
        Log.d(TAG, "addTodo: "+todoItems.size());
        todoItems.add(0,todoItem);
        todoRecyclerAdapter.notifyItemInserted(0);
    }
}
