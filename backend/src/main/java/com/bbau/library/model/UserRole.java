package com.bbau.library.model;

/**
 * Enumeration representing user roles in the Library Management System.
 */
public enum UserRole {
    STUDENT("student", "Student"),
    FACULTY("faculty", "Faculty Member"),
    ADMIN("admin", "Library Administrator");

    private final String code;
    private final String displayName;

    UserRole(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parse string to UserRole safely.
     */
    public static UserRole fromString(String roleStr) {
        if (roleStr == null) {
            return STUDENT;
        }
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(roleStr) || role.code.equalsIgnoreCase(roleStr)) {
                return role;
            }
        }
        return STUDENT;
    }
}

