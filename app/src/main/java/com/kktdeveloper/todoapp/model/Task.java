package com.kktdeveloper.todoapp.model;

public class Task {

    private String id;
    private String title;
    private String description;
    private String dateTime;
    private long timestamp;

    public Task() {
        // Required empty constructor for Firebase
    }

    public Task(String id, String title, String description, String dateTime, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
