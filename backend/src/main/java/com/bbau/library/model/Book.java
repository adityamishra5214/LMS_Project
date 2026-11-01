package com.bbau.library.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * ============================================================================
 * OOP CONCEPT: ENCAPSULATION & DOMAIN INVARIANTS
 * ============================================================================
 * The Book model encapsulates all attributes of a library item and enforces
 * operational rules directly within its domain boundaries (e.g. inventory control).
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated state
    private int id;
    private String title;
    private String author;
    private String subject;
    private String isbn;
    private int totalCopies;
    private int availableCopies;
    private boolean facultyOnly;
    private String description;

    public Book() {
        this.totalCopies = 1;
        this.availableCopies = 1;
    }

    public Book(int id, String title, String author, String subject, String isbn,
                int totalCopies, int availableCopies, boolean facultyOnly, String description) {
        this.id = id;
        setTitle(title);
        setAuthor(author);
        setSubject(subject);
        setIsbn(isbn);
        setCopies(totalCopies, availableCopies);
        this.facultyOnly = facultyOnly;
        this.description = description;
    }

    // ========================================================================
    // DOMAIN BEHAVIOR (Self-contained Business Rules)
    // ========================================================================

    /**
     * Checks if at least one copy of this book is currently on shelf.
     */
    public boolean isAvailable() {
        return this.availableCopies > 0;
    }

    /**
     * Atomically decrements available copies when borrowed.
     * Throws IllegalStateException if no copies remain.
     */
    public synchronized void borrowCopy() {
        if (!isAvailable()) {
            throw new IllegalStateException("No available copies of '" + title + "' to borrow.");
        }
        this.availableCopies--;
    }

    /**
     * Atomically increments available copies when a borrowed book is returned.
     * Ensures available copies do not exceed total inventory.
     */
    public synchronized void returnCopy() {
        if (this.availableCopies >= this.totalCopies) {
            throw new IllegalStateException("All copies of '" + title + "' are already accounted for.");
        }
        this.availableCopies++;
    }

    /**
     * Synchronizes total and available copy counts safely.
     */
    public void setCopies(int totalCopies, int availableCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative.");
        }
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new IllegalArgumentException("Available copies must be between 0 and total copies (" + totalCopies + ").");
        }
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // ========================================================================
    // GETTERS & SETTERS (Strict Encapsulation)
    // ========================================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be blank.");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be blank.");
        }
        this.author = author.trim();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = (subject == null) ? "General" : subject.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = (isbn == null) ? "" : isbn.trim();
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative.");
        }
        this.totalCopies = totalCopies;
        if (this.availableCopies > totalCopies) {
            this.availableCopies = totalCopies;
        }
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0 || availableCopies > this.totalCopies) {
            throw new IllegalArgumentException("Available copies must be between 0 and total copies.");
        }
        this.availableCopies = availableCopies;
    }

    public boolean isFacultyOnly() {
        return facultyOnly;
    }

    public void setFacultyOnly(boolean facultyOnly) {
        this.facultyOnly = facultyOnly;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", available=" + availableCopies + "/" + totalCopies +
                ", facultyOnly=" + facultyOnly +
                '}';
    }
}

