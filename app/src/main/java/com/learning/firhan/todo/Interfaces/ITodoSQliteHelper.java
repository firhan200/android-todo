package com.learning.firhan.todo.Interfaces;

import com.learning.firhan.todo.Models.TodoItem;

public interface ITodoSQliteHelper {
    void deleteTodo(int todoId);
    void editTodo(TodoItem todoItem);
}
