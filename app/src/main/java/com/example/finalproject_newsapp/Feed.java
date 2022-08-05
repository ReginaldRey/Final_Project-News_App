package com.example.finalproject_newsapp;

public class Feed {

    String title;
    String date;
    String description;

    public String getTitle() {
        return title;
    }

    public String setTitle() {
        this.title = title;
        return title;
    }

    public String getDate() {
        return date;
    }

    public String setDate() {
        this.date = date;
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String setDescription() {
        this.description = description;
        return description;
    }

    public String toString() {
        return "Feed [title=" + title + ", date=" + date + ", description=" + description +"]";
    }
}
