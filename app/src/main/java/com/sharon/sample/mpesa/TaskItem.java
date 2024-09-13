package com.sharon.sample.mpesa;

public class TaskItem {
    String id;
    String description;
    String name;
    String Time;


    public TaskItem(String description, String name, String time) {
        this.description = description;
        this.name = name;
        Time = time;
    }

    public TaskItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return Time;
    }
}
