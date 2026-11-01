package com.bbau.library.model;

/**
 * ============================================================================
 * OOP CONCEPT: INHERITANCE & POLYMORPHISM
 * ============================================================================
 * Faculty inherits from User with higher borrow quotas, longer loan windows,
 * reduced fine rates, and authorization to borrow faculty-only reserved books.
 */
public class Faculty extends User {
    private static final long serialVersionUID = 1L;

    // Faculty-specific encapsulated attributes
    private String department;
    private String designation;

    public Faculty() {
        super();
        setRole(UserRole.FACULTY);
    }

    public Faculty(String id, String name, String passwordHash) {
        super(id, name, passwordHash, UserRole.FACULTY);
        this.department = "Academic Faculty";
        this.designation = "Assistant Professor";
    }

    public Faculty(String id, String name, String passwordHash, String department, String designation) {
        super(id, name, passwordHash, UserRole.FACULTY);
        this.department = department;
        this.designation = designation;
    }

    // ========================================================================
    // POLYMORPHIC IMPLEMENTATIONS
    // ========================================================================

    @Override
    public int getMaxBooksAllowed() {
        // Faculty members may borrow up to 10 books concurrently
        return 10;
    }

    @Override
    public int getMaxLoanDays() {
        // Extended faculty loan duration of 30 days
        return 30;
    }

    @Override
    public double getDailyFineRate() {
        // Subsidized academic fine rate of ₹1 per day
        return 1.0;
    }

    @Override
    public boolean canBorrow(Book book) {
        // Faculty members have unrestricted borrowing clearance
        return book != null;
    }

    // ========================================================================
    // GETTERS & SETTERS
    // ========================================================================

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}

