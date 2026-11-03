package com.bbau.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a reader's feedback, star rating, and review on a book.
 */
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String userId;
    private int bookId;
    private int rating; // 1 to 5 stars
    private String title;
    private String body;
    private LocalDateTime createdAt;

    // View helper attributes
    private String userName;
    private String bookTitle;

    public Review() {
        this.createdAt = LocalDateTime.now();
        this.rating = 5;
    }

    public Review(int id, String userId, int bookId, int rating, String title, String body) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        setRating(rating);
        this.title = title;
        this.body = body;
        this.createdAt = LocalDateTime.now();
    }

    public Review(int id, String userId, int bookId, String title, String body) {
        this(id, userId, bookId, 5, title, body);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getRating() { return rating; }
    public void setRating(int rating) {
        if (rating < 1) this.rating = 1;
        else if (rating > 5) this.rating = 5;
        else this.rating = rating;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review review)) return false;
        return id == review.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
