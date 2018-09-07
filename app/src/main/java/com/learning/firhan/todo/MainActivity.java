package com.learning.firhan.todo;

import android.content.Intent;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.learning.firhan.todo.Constant.SettingNames;
import com.learning.firhan.todo.Fragments.TodoAddEditFragment;
import com.learning.firhan.todo.Fragments.TodoDetailFragment;
import com.learning.firhan.todo.Fragments.TodoListFragment;
import com.learning.firhan.todo.Helpers.SettingDatabaseHelper;
import com.learning.firhan.todo.Helpers.TodoDatabaseHelper;
import com.learning.firhan.todo.Interfaces.IMainActivity;
import com.learning.firhan.todo.Models.Setting;
import com.learning.firhan.todo.Models.TodoItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IMainActivity {
    private static final String TAG = "MainActivity";

    TodoListFragment todoListFragment;
    TodoDetailFragment todoDetailFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView toolbarTitle;
    ImageButton backButton, deleteButton, settingButton;
    FloatingActionButton addTodoButton;
    MenuItem menuItem;

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
        todoDetailFragment = new TodoDetailFragment();

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
        }else if(tag==getString(R.string.todo_detail_fragment)){
            fragment = todoDetailFragment;
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
}
