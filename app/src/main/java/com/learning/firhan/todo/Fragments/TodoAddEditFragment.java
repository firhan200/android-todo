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
import android.widget.Toast;

import com.learning.firhan.todo.Helpers.TodoDatabaseHelper;
import com.learning.firhan.todo.Interfaces.IMainActivity;
import com.learning.firhan.todo.Models.TodoItem;
import com.learning.firhan.todo.R;

public class TodoAddEditFragment extends BottomSheetDialogFragment {
    TodoDatabaseHelper todoDatabaseHelper;
    EditText todoTitle, todoDescription;
    Button submitBtn;
    IMainActivity iMainActivity;
    TodoItem todoItem;
    Boolean isEdit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iMainActivity = (IMainActivity)getActivity();
    }

    private void checkEditOrAddItem(){
        //check argument
        Bundle args = getArguments();
        if(args!=null){
            //get todo
            todoItem = args.getParcelable("todoItem");
            if(todoItem!=null){
                isEdit = true;
                populateDetail(todoItem);
            }
        }
    }

    private void populateDetail(TodoItem todoItem){
        todoTitle.setText(todoItem.getTitle());
        todoDescription.setText(todoItem.getDesciption());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_todo, container, false);

        //set to add
        isEdit = false;

        initIds(view);
        initHelpers();
        setSubmitBtnListener();

        checkEditOrAddItem();

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
        long newRowId = 0;
        if(!isEdit){
            //add new item
            todoItem = new TodoItem(0, title, description);
        }else{
            todoItem.setTitle(title);
            todoItem.setDesciption(description);
        }

        newRowId = saveIntoDatabase(isEdit, todoItem);

        if(newRowId > 0){
            todoItem.setId((int) newRowId);

            //dismiss bottom sheet
            this.dismiss();

            if(!isEdit){
                Toast.makeText(getContext(), "Add Item Success", Toast.LENGTH_SHORT).show();

                //add new item
                //add to list
                todoItem.setSelected(false);
                todoItem.setCollapsed(true);
                iMainActivity.addTodoToList(todoItem);
            }else{
                Toast.makeText(getContext(), "Edit Item Success", Toast.LENGTH_SHORT).show();

                //update item
                iMainActivity.rePopulateTodoList();
            }
        }
    }

    private long saveIntoDatabase(Boolean isEdit, TodoItem todoItem){
        long newRowId = 0;
        //save into database
        if(!isEdit){
            //add
            newRowId = todoDatabaseHelper.insertData(todoItem);
        }else{
            //edit
            newRowId = todoDatabaseHelper.updateData(todoItem);
        }

        return newRowId;
    }
}
