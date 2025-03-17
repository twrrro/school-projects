package com.example.ebookapplication;

public class Book {
    private String title;
    private String filePath;

    public Book(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }
}

