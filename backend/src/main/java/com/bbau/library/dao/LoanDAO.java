package com.bbau.library.dao;

import com.bbau.library.model.Loan;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT: ABSTRACTION (Data Access Object Pattern)
 * ============================================================================
 * Defines persistence contracts for Loan operations.
 */
public interface LoanDAO {

    /**
     * Finds a loan transaction by ID.
     */
    Optional<Loan> findById(int id);

    /**
     * Retrieves all loans associated with a specific user.
     */
    List<Loan> findByUserId(String userId);

    /**
     * Retrieves all active (unreturned) loans for a user.
     */
    List<Loan> findActiveLoansByUserId(String userId);

    /**
     * Retrieves all loans for a given book.
     */
    List<Loan> findByBookId(int bookId);

    /**
     * Retrieves all active loans across the entire library system.
     */
    List<Loan> findAllActiveLoans();

    /**
     * Retrieves the complete loan audit history.
     */
    List<Loan> findAll();

    /**
     * Saves a new loan record.
     */
    boolean save(Loan loan);

    /**
     * Updates an existing loan record (e.g. marking return date).
     */
    boolean update(Loan loan);

    /**
     * Default factory accessor returning the active DAO implementation.
     */
    static LoanDAO getDefault() {
        return LoanDAOImpl.getInstance();
    }
}
