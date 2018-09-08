package com.learning.firhan.todo.Fragments;

import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.learning.firhan.todo.Helpers.TodoDatabaseHelper;
import com.learning.firhan.todo.Interfaces.IMainActivity;
import com.learning.firhan.todo.Interfaces.ITodoSQliteHelper;
import com.learning.firhan.todo.MainActivity;
import com.learning.firhan.todo.Models.TodoItem;
import com.learning.firhan.todo.R;
import com.learning.firhan.todo.Adapters.TodoRecyclerAdapter;

import java.util.ArrayList;

public class TodoListFragment extends Fragment {
    private static final String TAG = "TodoListFragment";

    TodoDatabaseHelper todoDatabaseHelper;
    TextView noResultsLabel;
    ArrayList<TodoItem> todoItems, selectedTodoItems;
    IMainActivity iMainActivity;

    //RecyclerView Attributes
    private RecyclerView mRecyclerView;
    TodoRecyclerAdapter todoRecyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iMainActivity = (IMainActivity)getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        initIds(view);
        initHelpers();
        initTodoListView();
        initSelectedTodoItems();
        populateTodoList();

        return view;
    }

    private void initSelectedTodoItems(){
        selectedTodoItems = new ArrayList<>();
    }

    private void setTotalSelected(){
        iMainActivity.setTotalSelected(selectedTodoItems.size());
    }

    public void addSelectedTodoItem(TodoItem todoItem){
        //check if first selected
        if(selectedTodoItems.size() < 1){
            //first select - > activate selected toolbar
            iMainActivity.setSelectedActionBar(true);
        }

        if(!selectedTodoItems.contains(todoItem)){
            //list doest not contain this item
            selectedTodoItems.add(todoItem);
            setTotalSelected();
            Log.d(TAG, "addSelectedTodoItem: "+todoItem.getTitle()+" added");
        }
    }

    public void removeSelectedTodoItem(TodoItem todoItem){
        if(selectedTodoItems.contains(todoItem)){
            //item exist in list -> remove ite
            selectedTodoItems.remove(todoItem);
            Log.d(TAG, "removeSelectedTodoItem: "+todoItem.getTitle()+" removed");

            //check if is empty
            if(selectedTodoItems.size() < 1){
                iMainActivity.setSelectedActionBar(false);
            }

            setTotalSelected();
        }
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
        todoRecyclerAdapter = new TodoRecyclerAdapter(getContext(), todoItems);
        mRecyclerView.setAdapter(todoRecyclerAdapter);
    }

    public void populateTodoList(){
        todoItems.clear();
        getTodoItemsFromDatabase();
        setNoResultsLabelVisibility();
        todoRecyclerAdapter.notifyDataSetChanged();
    }

    public void unselectAllSelectedItem(){
        for(TodoItem todoItem: selectedTodoItems){
            todoItem.setSelected(false);
        }
        selectedTodoItems.clear();
        todoRecyclerAdapter.notifyDataSetChanged();
    }

    private void setNoResultsLabelVisibility(){
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
                TodoItem todoItem = new TodoItem(result.getInt(0) ,result.getString(1), result.getString(2));
                todoItem.setSelected(false);
                todoItem.setCollapsed(true);
                todoItems.add(todoItem);
            }
        }
    }

    public void deleteTodo(int todoId){
        //delete from sqlite
        todoDatabaseHelper.deleteData(todoId);
        //get item position
        TodoItem todoItem = getTodoItemById(todoId);
        if(todoItem!=null){
            //get position
            int position = todoItems.indexOf(todoItem);

            //remove from list
            todoItems.remove(todoItem);

            todoRecyclerAdapter.notifyItemRemoved(position);

            setNoResultsLabelVisibility();

            Toast.makeText(getContext(), "Delete Item Sucess", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSelectedItems(){
        if(selectedTodoItems.size() > 0){
            new DeleteSelectedItemsTask().execute();
        }
    }

    private TodoItem getTodoItemById(int todoId){
        TodoItem todoItemResult = null;

        for(TodoItem todoItem : todoItems){
            if(todoItem.getId()==todoId){
                todoItemResult = todoItem;
            }
        }

        return  todoItemResult;
    }

    @Override
    public void onResume() {
        super.onResume();
        iMainActivity.setToolbarTitle("Todo");
        iMainActivity.hasBackButton(false);
    }

    public void addTodo(TodoItem todoItem){
        Log.d(TAG, "addTodo: "+todoItems.size());
        todoItems.add(0,todoItem);
        todoRecyclerAdapter.notifyItemInserted(0);
        setNoResultsLabelVisibility();
    }

    class DeleteSelectedItemsTask extends AsyncTask{
        LoadingDialogFragment loadingDialogFragment;
        int totalItem;

        @Override
        protected void onPreExecute() {
            loadingDialogFragment = new LoadingDialogFragment();
            loadingDialogFragment.show(getFragmentManager(), "loading");

            totalItem = selectedTodoItems.size();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            for(TodoItem selectedTodoItem : selectedTodoItems){
                todoDatabaseHelper.deleteData(selectedTodoItem.getId());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            loadingDialogFragment.dismiss();
            selectedTodoItems.clear();
            iMainActivity.rePopulateTodoList();
            iMainActivity.setSelectedActionBar(false);
            Toast.makeText(getContext(), "Successfully delete "+totalItem+" item", Toast.LENGTH_SHORT);
            super.onPostExecute(o);
        }
    }
}
