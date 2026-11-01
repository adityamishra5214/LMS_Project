package com.bbau.library.dao;

import com.bbau.library.model.User;
import com.bbau.library.model.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT: ABSTRACTION (Data Access Object Pattern)
 * ============================================================================
 * Defines the persistence abstraction contract for User entities, isolating the
 * business domain and service layers from the underlying storage mechanism.
 */
public interface UserDAO {

    /**
     * Finds a user by unique identifier.
     */
    Optional<User> findById(String id);

    /**
     * Retrieves all users registered in the system.
     */
    List<User> findAll();

    /**
     * Retrieves users filtering by role.
     */
    List<User> findByRole(UserRole role);

    /**
     * Persists a new user record.
     */
    boolean save(User user);

    /**
     * Updates an existing user record.
     */
    boolean update(User user);

    /**
     * Removes a user by identifier.
     */
    boolean delete(String id);

    /**
     * Default factory accessor returning the active DAO implementation.
     */
    static UserDAO getDefault() {
        return UserDAOImpl.getInstance();
    }
}
