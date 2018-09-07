package com.learning.firhan.todo.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.firhan.todo.Helpers.TodoDatabaseHelper;
import com.learning.firhan.todo.Interfaces.IMainActivity;
import com.learning.firhan.todo.MainActivity;
import com.learning.firhan.todo.Models.TodoItem;
import com.learning.firhan.todo.R;

public class TodoDetailFragment extends Fragment {
    TodoItem todoItem;
    int position;
    TodoDatabaseHelper todoDatabaseHelper;
    TextView todoTitle, todoDescription;
    IMainActivity iMainActivity;
    ImageButton deleteButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iMainActivity = (MainActivity) getActivity();
        todoDatabaseHelper = new TodoDatabaseHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_detail, container, false);

        //set toolbar
        iMainActivity.hasBackButton(true);
        iMainActivity.setToolbarTitle("Details");

        //init id
        todoTitle = (TextView)view.findViewById(R.id.todoTitle);
        todoDescription = (TextView)view.findViewById(R.id.todoDescription);
        deleteButton = getActivity().findViewById(R.id.delete_button);

        //check argument
        Bundle args = getArguments();
        if(args!=null){
            //get todo
            todoItem = args.getParcelable("todo");
            if(todoItem!=null){
                populateDetail();
            }
        }

        return view;
    }

    private void populateDetail(){
        todoTitle.setText(todoItem.getTitle());
        todoDescription.setText(todoItem.getDesciption());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme_Dialog));
                builder.setMessage("Delete "+todoItem.getTitle())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                todoDatabaseHelper.deleteData(todoItem.getId());

                                //make toast
                                Toast.makeText(getContext() ,"Delete data success!", Toast.LENGTH_SHORT).show();

                                //set fragment
                                iMainActivity.setFragment(getString(R.string.todo_list_fragment), false, null);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        }).show();
            }
        });
    }
}
