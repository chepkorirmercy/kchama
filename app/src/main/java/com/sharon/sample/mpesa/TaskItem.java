package com.sharon.sample.mpesa;

public class TaskItem {
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
