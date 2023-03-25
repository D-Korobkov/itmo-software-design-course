package org.example.softwaredesign.mvc.model;

public class TodoListItem {
    private long id;
    private String description;
    private boolean isDone;

    public TodoListItem() {
    }

    public TodoListItem(long id, final String description, boolean isDone) {
        this.id = id;
        this.description = description;
        this.isDone = isDone;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsDone(boolean done) {
        isDone = done;
    }
}
