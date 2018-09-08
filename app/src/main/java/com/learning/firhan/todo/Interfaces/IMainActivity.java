package com.learning.firhan.todo.Interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.learning.firhan.todo.Models.TodoItem;

public interface IMainActivity {
    //Todo List
    void addTodoToList(TodoItem todoItem);
    void addToSelectedItemList(TodoItem todoItem);
    void removeFromSelectedItemList(TodoItem todoItem);
    void rePopulateTodoList();

    //View Layout
    void setTotalSelected(int totalSelected);
    void setSelectedActionBar(Boolean isActive);
    void setToolbarTitle(String title);
    void hasBackButton(boolean hasBack);
    void setFragment(String tag, boolean addToBackStack, Bundle bundle);
}
