package org.example.softwaredesign.mvc.model;

import java.time.LocalDate;
import java.util.List;

public class TodoList {
    private LocalDate atDate;
    private List<TodoListItem> items;

    public TodoList() {
    }

    public TodoList(LocalDate atDate, List<TodoListItem> items) {
        this.atDate = atDate;
        this.items = items;
    }

    public LocalDate getAtDate() {
        return atDate;
    }

    public void setAtDate(LocalDate atDate) {
        this.atDate = atDate;
    }

    public List<TodoListItem> getItems() {
        return items;
    }

    public void setItems(List<TodoListItem> items) {
        this.items = items;
    }
}
