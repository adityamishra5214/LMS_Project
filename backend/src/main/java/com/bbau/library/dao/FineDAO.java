package com.bbau.library.dao;

import com.bbau.library.model.Fine;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT: ABSTRACTION (Data Access Object Pattern)
 * ============================================================================
 * Defines persistence contracts for Fine management.
 */
public interface FineDAO {

    /**
     * Finds a fine record by primary key ID.
     */
    Optional<Fine> findById(int id);

    /**
     * Retrieves all fines incurred by a specific user.
     */
    List<Fine> findByUserId(String userId);

    /**
     * Retrieves only unpaid fines for a specific user.
     */
    List<Fine> findUnpaidByUserId(String userId);

    /**
     * Retrieves all fines system-wide.
     */
    List<Fine> findAll();

    /**
     * Records a new fine.
     */
    boolean save(Fine fine);

    /**
     * Updates an existing fine (e.g. marking as paid).
     */
    boolean update(Fine fine);

    /**
     * Default factory accessor returning the active DAO implementation.
     */
    static FineDAO getDefault() {
        return FineDAOImpl.getInstance();
    }
}
