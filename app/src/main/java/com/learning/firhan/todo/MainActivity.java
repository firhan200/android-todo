package com.learning.firhan.todo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learning.firhan.todo.Fragments.TodoAddEditFragment;
import com.learning.firhan.todo.Fragments.TodoListFragment;
import com.learning.firhan.todo.Interfaces.IMainActivity;
import com.learning.firhan.todo.Interfaces.ITodoSQliteHelper;
import com.learning.firhan.todo.Models.TodoItem;

public class MainActivity extends AppCompatActivity implements IMainActivity, ITodoSQliteHelper {
    private static final String TAG = "MainActivity";

    TodoListFragment todoListFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView toolbarTitle, totalSelected, deleteSelected;
    ImageButton backButton, deleteButton, settingButton;
    FloatingActionButton addTodoButton;
    RelativeLayout defaultToolbar,selectedActionToolbar;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode==200){
                //reload list
                //set fragment
                Log.d(TAG, "onActivityResult: populate todo list");
                todoListFragment.populateTodoList();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initIds();
        setToolbar();
        initFragments();
        setAddTodoButtonListener();
        setSettingButtonListener();
        setDeleteSelectedListener();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getSupportFragmentManager().popBackStack();
                }catch (Exception ex){
                    Log.d(TAG, "onClick: "+ex.getMessage());
                }
            }
        });

        //set fragment
        setFragment(getString(R.string.todo_list_fragment), false, null);
    }

    private void initIds(){
        //init id
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        backButton = (ImageButton)findViewById(R.id.back_button);
        deleteButton = (ImageButton)findViewById(R.id.delete_button);
        settingButton = (ImageButton)findViewById(R.id.setting_button);
        addTodoButton = (FloatingActionButton) findViewById(R.id.fab);
        defaultToolbar = (RelativeLayout)findViewById(R.id.defaultToolbar);
        selectedActionToolbar = (RelativeLayout)findViewById(R.id.selectedActionToolbar);
        totalSelected = (TextView)findViewById(R.id.totalSelected);
        deleteSelected = (TextView)findViewById(R.id.deleteSelected);
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch (Exception ex){
            Log.d(TAG, "onCreate: "+ex.getMessage());
        }
    }

    private void initFragments(){
        //init fragment attribute
        todoListFragment = new TodoListFragment();

        fragmentManager = getSupportFragmentManager();
    }

    private void setAddTodoButtonListener(){
        addTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoAddEditFragment todoAddEditFragment = new TodoAddEditFragment();
                todoAddEditFragment.show(getSupportFragmentManager(), "AddEditTodo");
            }
        });
    }

    private void setSettingButtonListener(){
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goto setting
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

    private void setDeleteSelectedListener(){
        deleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme_Dialog));
                deleteAlert.setMessage("Delete Selected Items?");
                deleteAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        todoListFragment.deleteSelectedItems();
                    }
                });
                deleteAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setSelectedActionBar(false);
                    }
                });
                deleteAlert.create().show();
            }
        });
    }

    @Override
    public void setSelectedActionBar(Boolean isActive){
        if(isActive){
            //activate
            defaultToolbar.setVisibility(View.GONE);
            selectedActionToolbar.setVisibility(View.VISIBLE);
        }else{
            //deactivate
            //activate
            selectedActionToolbar.setVisibility(View.GONE);
            defaultToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void setFragment(String tag, boolean addToBackStack, Bundle bundle){
        fragmentManager.popBackStack();
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = null;
        if(tag==getString(R.string.todo_list_fragment)){
            fragment = todoListFragment;
        }

        fragmentTransaction.replace(R.id.fragments_container, fragment, tag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if(addToBackStack){
            fragmentTransaction.addToBackStack(tag);
        }

        //check bundle
        if(bundle!=null){
            fragment.setArguments(bundle);
        }

        fragmentTransaction.commit();
    }

    @Override
    public void addTodoToList(TodoItem todoItem) {
        todoListFragment.addTodo(todoItem);
    }

    @Override
    public void addToSelectedItemList(TodoItem todoItem) {
        todoListFragment.addSelectedTodoItem(todoItem);
    }

    @Override
    public void removeFromSelectedItemList(TodoItem todoItem) {
        todoListFragment.removeSelectedTodoItem(todoItem);
    }

    @Override
    public void rePopulateTodoList() {
        todoListFragment.populateTodoList();
    }

    @Override
    public void setTotalSelected(int totalSelected) {
        String total =  String.valueOf(totalSelected);
        String selectedLabel = total + " Selected";
        this.totalSelected.setText(selectedLabel);
    }

    @Override
    public void setToolbarTitle(String title) {
        try{
            toolbarTitle.setText(title);
        }catch (Exception ex){
            Log.d(TAG, "setToolbarTitle: ");
        }
    }

    @Override
    public void hasBackButton(boolean hasBack) {
        if(hasBack){
            //set back button true
            backButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            settingButton.setVisibility(View.GONE);
        }else{
            backButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            settingButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void deleteTodo(int todoId) {
        todoListFragment.deleteTodo(todoId);
    }

    @Override
    public void editTodo(TodoItem todoItem) {
        TodoAddEditFragment todoAddEditFragment = new TodoAddEditFragment();

        //set bundle arguments
        Bundle bundle = new Bundle();
        bundle.putParcelable("todoItem", todoItem);
        todoAddEditFragment.setArguments(bundle);

        todoAddEditFragment.show(getSupportFragmentManager(), "AddEditTodo");
    }

    @Override
    public void onBackPressed() {
        if(selectedActionToolbar.getVisibility()==View.VISIBLE){
            setSelectedActionBar(false);
            todoListFragment.unselectAllSelectedItem();
        }else{
            super.onBackPressed();
        }
    }
}
