package com.bbau.library.service;

import com.bbau.library.dao.FineDAO;
import com.bbau.library.model.Fine;
import com.bbau.library.model.User;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT: SINGLE RESPONSIBILITY & BUSINESS INTEGRITY
 * ============================================================================
 * FineService manages fine ledgering, simulated payments, and administrative waivers.
 */
public class FineService {

    private final FineDAO fineDAO;

    public FineService() {
        this(FineDAO.getDefault());
    }

    public FineService(FineDAO fineDAO) {
        this.fineDAO = fineDAO;
    }

    /**
     * Records a new fine entry.
     */
    public Fine createFine(String userId, int bookId, int amount, String reason) {
        Fine fine = new Fine(0, userId, bookId, amount, reason);
        fineDAO.save(fine);
        return fine;
    }

    /**
     * Retrieves all fines for a specific user.
     */
    public List<Fine> getFinesForUser(String userId) {
        return fineDAO.findByUserId(userId);
    }

    /**
     * Retrieves outstanding (unpaid) fines for a specific user.
     */
    public List<Fine> getUnpaidFinesForUser(String userId) {
        return fineDAO.findUnpaidByUserId(userId);
    }

    /**
     * Calculates the sum of all pending fines for a user.
     */
    public int getTotalUnpaidAmount(String userId) {
        return getUnpaidFinesForUser(userId).stream()
                .mapToInt(Fine::getAmount)
                .sum();
    }

    /**
     * Simulates fine clearance / payment.
     */
    public boolean payFine(int fineId) {
        Optional<Fine> fineOpt = fineDAO.findById(fineId);
        if (fineOpt.isEmpty()) {
            return false;
        }

        Fine fine = fineOpt.get();
        fine.markAsPaid();
        return fineDAO.update(fine);
    }

    /**
     * Waives an outstanding fine. Restricted to administrators.
     */
    public boolean waiveFine(int fineId, User operator) {
        if (operator == null || !operator.isAdmin()) {
            throw new SecurityException("Unauthorized: Only administrators may waive fines.");
        }

        Optional<Fine> fineOpt = fineDAO.findById(fineId);
        if (fineOpt.isEmpty()) {
            return false;
        }

        Fine fine = fineOpt.get();
        fine.setReason("[WAIVED by " + operator.getName() + "] " + fine.getReason());
        fine.markAsPaid();
        return fineDAO.update(fine);
    }

    /**
     * Retrieves all fines system-wide for administration.
     */
    public List<Fine> getAllFines() {
        return fineDAO.findAll();
    }
}

