package com.bbau.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ============================================================================
 * OOP CONCEPT: ABSTRACTION & ENCAPSULATION
 * ============================================================================
 * User serves as an Abstract Base Class for all library patrons and staff.
 * - Abstraction: Exposes high-level business contracts (e.g. getMaxBooksAllowed(),
 *   getDailyFineRate(), canBorrow(book)) without coupling the system to a specific user type.
 * - Encapsulation: Protects internal states (id, name, password hash, role) with private
 *   fields and validated accessors/mutators.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated private attributes
    private String id;
    private String name;
    private String passwordHash;
    private UserRole role;
    private LocalDateTime createdAt;

    /**
     * Default constructor for serialization / JavaBean compatibility.
     */
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Parameterized constructor.
     */
    public User(String id, String name, String passwordHash, UserRole role) {
        setId(id);
        setName(name);
        setPasswordHash(passwordHash);
        setRole(role);
        this.createdAt = LocalDateTime.now();
    }

    // ========================================================================
    // ABSTRACT METHODS (Subclasses must provide polymorphic implementations)
    // ========================================================================

    /**
     * Maximum number of books this user is permitted to hold on loan simultaneously.
     */
    public abstract int getMaxBooksAllowed();

    /**
     * Standard duration (in days) allowed for each book borrowed.
     */
    public abstract int getMaxLoanDays();

    /**
     * Overdue fine incurred per day after due date in INR (₹).
     */
    public abstract double getDailyFineRate();

    /**
     * Evaluates whether this specific user type has borrowing clearance for the given book.
     * For example, students cannot borrow books flagged as faculty-only.
     *
     * @param book The target book to inspect
     * @return true if borrowing is permissible, false otherwise
     */
    public abstract boolean canBorrow(Book book);

    // ========================================================================
    // CONCRETE OOP BEHAVIOR & POLYMORPHIC HOOKS
    // ========================================================================

    /**
     * Checks if this user has administrator privileges.
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    /**
     * Checks if user has reached their maximum quota of active loans.
     */
    public boolean hasReachedBorrowLimit(int currentActiveLoansCount) {
        return currentActiveLoansCount >= getMaxBooksAllowed();
    }

    // ========================================================================
    // GETTERS & SETTERS (Strict Encapsulation)
    // ========================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty.");
        }
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = Objects.requireNonNull(role, "User role cannot be null.");
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}

