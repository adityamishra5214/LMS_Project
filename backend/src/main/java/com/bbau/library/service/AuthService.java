package com.bbau.library.service;

import com.bbau.library.dao.UserDAO;
import com.bbau.library.model.*;
import com.bbau.library.util.PasswordUtil;

import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT: SINGLE RESPONSIBILITY & DEPENDENCY INVERSION PRINCIPLE
 * ============================================================================
 * AuthService handles user authentication, session verification, and user registration.
 * It depends exclusively on the UserDAO abstraction, not on concrete persistence.
 */
public class AuthService {

    // Dependency Inversion: depends on DAO abstraction
    private final UserDAO userDAO;

    public AuthService() {
        this(UserDAO.getDefault());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user with given credentials.
     *
     * @param userId   User ID / Username
     * @param password Plain-text password entered by the user
     * @return Optional containing the authenticated User, or empty if invalid
     */
    public Optional<User> authenticate(String userId, String password) {
        if (userId == null || password == null || userId.isBlank() || password.isBlank()) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findById(userId.trim());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        if (PasswordUtil.verify(password, user.getPasswordHash())) {
            return Optional.of(user);
        }

        return Optional.empty();
    }

    /**
     * Registers a new user.
     *
     * @param user The fully constructed User subtype (Student, Faculty, Admin)
     * @return true if registration succeeded, false if user ID already exists
     */
    public boolean register(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        if (userDAO.findById(user.getId()).isPresent()) {
            return false; // User already exists
        }

        return userDAO.save(user);
    }

    /**
     * ========================================================================
     * OOP CONCEPT: METHOD OVERLOADING (Compile-time Polymorphism)
     * ========================================================================
     * Overloaded helper registering a new user from individual form fields.
     */
    public boolean register(String roleStr, String id, String name, String plainPassword) {
        if (id == null || plainPassword == null || id.isBlank() || plainPassword.isBlank()) {
            return false;
        }

        String hashedPassword = PasswordUtil.hash(plainPassword);
        User newUser = UserFactory.createUser(roleStr, id.trim(), name != null ? name.trim() : id.trim(), hashedPassword);
        return register(newUser);
    }

    /**
     * Fetches user profile by ID.
     */
    public Optional<User> getUserById(String userId) {
        return userDAO.findById(userId);
    }
}

