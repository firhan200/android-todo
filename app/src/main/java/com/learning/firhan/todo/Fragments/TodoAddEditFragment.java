package com.learning.firhan.todo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.learning.firhan.todo.Helpers.TodoDatabaseHelper;
import com.learning.firhan.todo.Interfaces.IMainActivity;
import com.learning.firhan.todo.Models.TodoItem;
import com.learning.firhan.todo.R;

public class TodoAddEditFragment extends BottomSheetDialogFragment {
    TodoDatabaseHelper todoDatabaseHelper;
    EditText todoTitle, todoDescription;
    Button submitBtn;
    IMainActivity iMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iMainActivity = (IMainActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_todo, container, false);

        initIds(view);
        initHelpers();
        setSubmitBtnListener();

        return view;
    }

    private void initIds(View view){
        //init id
        todoTitle = (EditText) view.findViewById(R.id.todoTitleEdit);
        todoDescription = (EditText) view.findViewById(R.id.todoDecriptionEdit);
        submitBtn = (Button) view.findViewById(R.id.submitBtn);
    }

    private void initHelpers(){
        todoDatabaseHelper = new TodoDatabaseHelper(getContext());
    }

    private void setSubmitBtnListener(){
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem(v, todoTitle.getText().toString(), todoDescription.getText().toString());
            }
        });
    }

    public void saveItem(View v, String title,String description){
        if(title.isEmpty()){
            Snackbar.make(v, "Title cannot be empty.",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(description.isEmpty()){
            Snackbar.make(v,"Description cannot be empty.",Snackbar.LENGTH_SHORT).show();
            return;
        }

        //save into SQLite
        TodoItem todoItem = new TodoItem(0, title, description);
        boolean isSuccess = saveIntoDatabase(todoItem);
        if(isSuccess){
            //dismiss bottom sheet
            this.dismiss();

            //add to list
            iMainActivity.addTodoToList(todoItem);
        }
    }

    private boolean saveIntoDatabase(TodoItem todoItem){
        //save into database
        long newRowId = todoDatabaseHelper.insertData(todoItem);
        if(newRowId > 0){
            return true;
        }else{
            return false;
        }
    }
}
