package com.learning.firhan.todo.Interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.learning.firhan.todo.Models.TodoItem;

public interface IMainActivity {
    void addTodoToList(TodoItem todoItem);
    void rePopulateTodoList();
    public void setToolbarTitle(String title);
    public void hasBackButton(boolean hasBack);
    public void setFragment(String tag, boolean addToBackStack, Bundle bundle);
}
