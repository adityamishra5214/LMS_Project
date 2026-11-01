package com.bbau.library.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * ============================================================================
 * OOP CONCEPT: COMPOSITION & DELEGATION
 * ============================================================================
 * The Loan class demonstrates composition: it brings together a User and a Book
 * alongside transaction temporal state (issue date, due date, return date).
 * It delegates fine computation dynamically to the associated User's polymorphic rate.
 */
public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String userId;
    private int bookId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null if actively on loan

    // Composition references (may be populated for convenient view rendering)
    private transient User user;
    private transient Book book;

    // Cached metadata for display when objects are not eagerly joined
    private String bookTitle;
    private String userName;

    public Loan() {
    }

    public Loan(int id, String userId, int bookId, LocalDate issueDate, LocalDate dueDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public Loan(int id, User user, Book book, int loanDays) {
        this.id = id;
        this.user = Objects.requireNonNull(user, "User cannot be null");
        this.book = Objects.requireNonNull(book, "Book cannot be null");
        this.userId = user.getId();
        this.bookId = book.getId();
        this.userName = user.getName();
        this.bookTitle = book.getTitle();
        this.issueDate = LocalDate.now();
        this.dueDate = this.issueDate.plusDays(loanDays);
        this.returnDate = null;
    }

    // ========================================================================
    // DOMAIN BEHAVIOR
    // ========================================================================

    /**
     * Checks if the loan is currently active (not yet returned).
     */
    public boolean isActive() {
        return this.returnDate == null;
    }

    /**
     * Checks if this loan has exceeded its due date without being returned.
     */
    public boolean isOverdue() {
        if (!isActive()) {
            return false;
        }
        return LocalDate.now().isAfter(this.dueDate);
    }

    /**
     * Returns the count of days this book is overdue.
     */
    public long getOverdueDays() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(this.dueDate, LocalDate.now());
    }

    /**
     * Calculates the pending fine based on the borrower's polymorphic daily rate.
     */
    public double calculateFine(User borrower) {
        if (!isOverdue() || borrower == null) {
            return 0.0;
        }
        long overdueDays = getOverdueDays();
        return overdueDays * borrower.getDailyFineRate();
    }

    /**
     * Marks the book as returned on the given date.
     */
    public void markReturned(LocalDate date) {
        this.returnDate = (date != null) ? date : LocalDate.now();
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

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
            this.userName = user.getName();
        }
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
        if (book != null) {
            this.bookId = book.getId();
            this.bookTitle = book.getTitle();
        }
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
        if (!(o instanceof Loan loan)) return false;
        return id == loan.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

