// app/src/main/java/com/example/ebookreader/models/Book.java
package com.example.ebookreader.models;

import android.net.Uri;
import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private String filePath;
    private Uri fileUri;
    private BookType type;
    private String coverImage;
    private int lastReadPage;
    private int totalPages;
    private long lastReadTimestamp;

    public Book(String title, String author, String filePath, BookType type) {
        this.title = title;
        this.author = author;
        this.filePath = filePath;
        this.type = type;
        this.lastReadPage = 0;
        this.totalPages = 0;
        this.lastReadTimestamp = System.currentTimeMillis();
    }

    public Book(String title, String author, Uri fileUri, BookType type) {
        this.title = title;
        this.author = author;
        this.fileUri = fileUri;
        if (fileUri != null) {
            this.filePath = fileUri.toString();
        }
        this.type = type;
        this.lastReadPage = 0;
        this.totalPages = 0;
        this.lastReadTimestamp = System.currentTimeMillis();
    }

    // Getter ve Setter metotları
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
        if (filePath != null) {
            try {
                this.fileUri = Uri.parse(filePath);
            } catch (Exception e) {
                this.fileUri = null; // veya hata yönetimi
            }
        }
    }
    public Uri getFileUri() { return fileUri; }
    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
        if (fileUri != null) {
            this.filePath = fileUri.toString();
        }
    }
    public BookType getType() { return type; }
    public void setType(BookType type) { this.type = type; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public int getLastReadPage() { return lastReadPage; }
    public void setLastReadPage(int lastReadPage) { this.lastReadPage = lastReadPage; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public long getLastReadTimestamp() { return lastReadTimestamp; }
    public void setLastReadTimestamp(long lastReadTimestamp) { this.lastReadTimestamp = lastReadTimestamp; }
}
