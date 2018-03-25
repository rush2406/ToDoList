package com.example.android.todolist;

/**
 * Created by rusha on 21-07-2017.
 */

public class Task {
    String task;
    int priority;
    String date;
    String setTime;
    String photoUrl;
    String id;

    public String getId() {
        return id;
    }

    public String getPhotoUrl() {

        return photoUrl;
    }

    public Task(String task, int priority, String date, String photoUrl, String id, String setTime) {
        this.task = task;
        this.priority = priority;
        this.date = date;
        this.photoUrl = photoUrl;
        this.id = id;
        this.setTime = setTime;

    }


    public Task() {

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

    public String getSetTime() {
        return setTime;
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
