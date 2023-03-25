package org.example.softwaredesign.mvc.dao;

import org.example.softwaredesign.mvc.model.TodoListItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoListDao {
    void addNewItem(TodoListItem todo, LocalDate atDate);

    void removeItem(long id);

    List<TodoListItem> getTodoList(LocalDate atDate);

    Optional<TodoListItem> findTodoListItem(long id);

    void switchIsDoneFlag(long id);
}
