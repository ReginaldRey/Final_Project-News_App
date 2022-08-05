package com.example.finalproject_newsapp;

import java.util.ArrayList;
import java.util.List;

public class StoredFeed {

    final String title;
    final String date;
    final String description;

    final List <Feed> entries = new ArrayList<Feed>();

    public StoredFeed(String title, String date, String description) {
        this.title = title;
        this.date = date;
        this.description = description;
    }

    public List<Feed> getMessages() {
        return entries;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "StoredFeed [title=" + title + ", date=" + date + ", description=" + description +"]";
    }
}
