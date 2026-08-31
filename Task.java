package com.example.studenttaskmanager;

public class Task {
    public long id;
    public String title;
    public String description;
    public String dueDate;
    public boolean completed;

    public Task(long id, String title, String description, String dueDate, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
    }
}
