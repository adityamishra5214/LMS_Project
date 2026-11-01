package com.bbau.library.model;

/**
 * ============================================================================
 * OOP CONCEPT: INHERITANCE & POLYMORPHISM
 * ============================================================================
 * Student inherits state and common functionality from User, while supplying
 * concrete, student-specific implementations of the abstract contracts.
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;

    // Student-specific encapsulated attributes
    private String department;
    private String rollNumber;

    public Student() {
        super();
        setRole(UserRole.STUDENT);
    }

    public Student(String id, String name, String passwordHash) {
        super(id, name, passwordHash, UserRole.STUDENT);
        this.rollNumber = id;
        this.department = "General";
    }

    public Student(String id, String name, String passwordHash, String department, String rollNumber) {
        super(id, name, passwordHash, UserRole.STUDENT);
        this.department = department;
        this.rollNumber = rollNumber;
    }

    // ========================================================================
    // POLYMORPHIC IMPLEMENTATIONS
    // ========================================================================

    @Override
    public int getMaxBooksAllowed() {
        // Students may borrow up to 3 books concurrently
        return 3;
    }

    @Override
    public int getMaxLoanDays() {
        // Standard student loan period is 14 days
        return 14;
    }

    @Override
    public double getDailyFineRate() {
        // Students incur standard fine of ₹5 per day
        return 5.0;
    }

    @Override
    public boolean canBorrow(Book book) {
        if (book == null) {
            return false;
        }
        // Students are restricted from borrowing faculty-only reserve books
        return !book.isFacultyOnly();
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

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
}
