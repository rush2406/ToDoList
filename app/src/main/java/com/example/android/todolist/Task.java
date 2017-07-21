package com.example.android.todolist;

/**
 * Created by rusha on 21-07-2017.
 */

public class Task {
    String task;
    int priority;
    String date;

    public Task(String task,int priority,String date)
    {
        this.task = task;
        this.priority = priority;
        this.date = date;
    }

    public Task()
    {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTask() {

        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
