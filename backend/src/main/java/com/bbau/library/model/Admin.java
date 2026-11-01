package com.bbau.library.model;

/**
 * ============================================================================
 * OOP CONCEPT: INHERITANCE & POLYMORPHISM
 * ============================================================================
 * Admin inherits from User, possessing full administrative system capabilities,
 * unlimited borrowing privileges, and zero late return fines.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String adminCode;
    private boolean superAdmin;

    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }

    public Admin(String id, String name, String passwordHash) {
        super(id, name, passwordHash, UserRole.ADMIN);
        this.adminCode = "ADM-" + id;
        this.superAdmin = true;
    }

    public Admin(String id, String name, String passwordHash, String adminCode, boolean superAdmin) {
        super(id, name, passwordHash, UserRole.ADMIN);
        this.adminCode = adminCode;
        this.superAdmin = superAdmin;
    }

    // ========================================================================
    // POLYMORPHIC IMPLEMENTATIONS
    // ========================================================================

    @Override
    public int getMaxBooksAllowed() {
        return 50;
    }

    @Override
    public int getMaxLoanDays() {
        return 60;
    }

    @Override
    public double getDailyFineRate() {
        return 0.0; // Administrators incur zero fines
    }

    @Override
    public boolean canBorrow(Book book) {
        return book != null;
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    // ========================================================================
    // GETTERS & SETTERS
    // ========================================================================

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }
}

