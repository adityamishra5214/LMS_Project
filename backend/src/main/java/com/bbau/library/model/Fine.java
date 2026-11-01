package com.bbau.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ============================================================================
 * OOP CONCEPT: ENCAPSULATION & DATA INTEGRITY
 * ============================================================================
 * Represents a fine assessed against a user for late returns or lost books.
 */
public class Fine implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String userId;
    private int bookId;
    private int amount; // in whole INR currency (₹)
    private String reason;
    private boolean paid;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    // View helper attributes
    private String bookTitle;
    private String userName;

    public Fine() {
        this.createdAt = LocalDateTime.now();
        this.paid = false;
    }

    public Fine(int id, String userId, int bookId, int amount, String reason) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        setAmount(amount);
        this.reason = reason;
        this.paid = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsPaid() {
        this.paid = true;
        this.paidAt = LocalDateTime.now();
    }

    // ========================================================================
    // GETTERS & SETTERS
    // ========================================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Fine amount cannot be negative.");
        }
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fine fine)) return false;
        return id == fine.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

