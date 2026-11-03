package com.bbau.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents system notifications sent to patrons regarding book transactions,
 * due date warnings, fines, and payment confirmations.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_ISSUE = "ISSUE_CONFIRMATION";
    public static final String TYPE_RETURN = "RETURN_CONFIRMATION";
    public static final String TYPE_DUE_REMINDER = "DUE_DATE_REMINDER";
    public static final String TYPE_OVERDUE = "OVERDUE_ALERT";
    public static final String TYPE_FINE = "FINE_NOTICE";
    public static final String TYPE_PAYMENT = "PAYMENT_CONFIRMATION";

    private int id;
    private String userId;
    private String type; // one of the constants above
    private String title;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
        this.type = TYPE_ISSUE;
    }

    public Notification(int id, String userId, String type, String title, String message) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(int id, String userId, String message) {
        this(id, userId, TYPE_ISSUE, "Notification", message);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
