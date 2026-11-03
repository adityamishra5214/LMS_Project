package com.bbau.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a community discussion thread or student query about a book.
 */
public class Discussion implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int bookId;
    private String bookTitle;
    private String userId;
    private String userName;
    private UserRole userRole;
    private String title;
    private String question;
    private LocalDateTime createdAt;
    private List<DiscussionReply> replies = new ArrayList<>();

    public Discussion() {
        this.createdAt = LocalDateTime.now();
    }

    public Discussion(int id, int bookId, String bookTitle, String userId, String userName, UserRole userRole, String title, String question) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.title = title;
        this.question = question;
        this.createdAt = LocalDateTime.now();
    }

    public void addReply(DiscussionReply reply) {
        if (reply != null) {
            this.replies.add(reply);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public UserRole getUserRole() { return userRole; }
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<DiscussionReply> getReplies() { return replies; }
    public void setReplies(List<DiscussionReply> replies) { this.replies = replies; }
}

