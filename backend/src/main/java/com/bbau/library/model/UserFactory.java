package com.bbau.library.model;

/**
 * ============================================================================
 * OOP CONCEPT: FACTORY PATTERN & POLYMORPHIC INSTANTIATION
 * ============================================================================
 * Factory class responsible for creating concrete instances of User subtypes
 * without exposing the creation logic directly to the caller.
 */
public class UserFactory {

    private UserFactory() {
        // Utility factory - prevent direct instantiation
    }

    /**
     * Creates a concrete User instance based on the role enum.
     */
    public static User createUser(UserRole role, String id, String name, String passwordHash) {
        if (role == null) {
            role = UserRole.STUDENT;
        }
        return switch (role) {
            case STUDENT -> new Student(id, name, passwordHash);
            case FACULTY -> new Faculty(id, name, passwordHash);
            case ADMIN -> new Admin(id, name, passwordHash);
        };
    }

    /**
     * Overloaded factory method accepting role string.
     */
    public static User createUser(String roleStr, String id, String name, String passwordHash) {
        UserRole role = UserRole.fromString(roleStr);
        return createUser(role, id, name, passwordHash);
    }
}

