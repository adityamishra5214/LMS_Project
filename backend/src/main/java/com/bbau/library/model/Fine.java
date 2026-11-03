package com.bbau.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ============================================================================
 * OOP CONCEPT: ENCAPSULATION & DATA INTEGRITY
 * ============================================================================
 * Represents a fine assessed against a user for late returns (₹5/day),
 * damaged books (₹200), or lost books (₹500), complete with simulated payment gateway attributes.
 */
public class Fine implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int FINE_DAMAGED_BOOK = 200;
    public static final int FINE_LOST_BOOK = 500;
    public static final int FINE_LATE_PER_DAY = 5;

    private int id;
    private String userId;
    private int bookId;
    private int amount; // in INR (₹)
    private String fineType; // "LATE_RETURN", "DAMAGED_BOOK", "LOST_BOOK"
    private String reason;
    private boolean paid;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String paymentMethod; // "UPI", "Debit Card", "Net Banking", "Campus Card"
    private String transactionId; // Gateway transaction reference ID

    // View helper attributes
    private String bookTitle;
    private String userName;

    public Fine() {
        this.createdAt = LocalDateTime.now();
        this.paid = false;
        this.fineType = "LATE_RETURN";
    }

    public Fine(int id, String userId, int bookId, int amount, String fineType, String reason) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        setAmount(amount);
        this.fineType = fineType;
        this.reason = reason;
        this.paid = false;
        this.createdAt = LocalDateTime.now();
    }

    public Fine(int id, String userId, int bookId, int amount, String reason) {
        this(id, userId, bookId, amount, "LATE_RETURN", reason);
    }

    public void markAsPaid(String paymentMethod, String transactionId) {
        this.paid = true;
        this.paidAt = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
    }

    public void markAsPaid() {
        markAsPaid("UPI Payment Gateway", "TXN-" + System.currentTimeMillis());
    }

    // ========================================================================
    // GETTERS & SETTERS
    // ========================================================================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Fine amount cannot be negative.");
        }
        this.amount = amount;
    }

    public String getFineType() { return fineType; }
    public void setFineType(String fineType) { this.fineType = fineType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

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
