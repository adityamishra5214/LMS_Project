package com.bbau.library.service;

import com.bbau.library.dao.BookDAO;
import com.bbau.library.dao.LoanDAO;
import com.bbau.library.dao.UserDAO;
import com.bbau.library.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT: POLYMORPHISM & BUSINESS RULE ORCHESTRATION
 * ============================================================================
 * LoanService enforces lending rules by dynamically dispatching to the
 * polymorphic methods of User subclasses (Student, Faculty, Admin):
 * - user.canBorrow(book): Student cannot borrow faculty-only reserves.
 * - user.getMaxBooksAllowed(): Borrow quota limits.
 * - user.getMaxLoanDays(): Dynamic loan duration.
 * - user.getDailyFineRate(): Dynamic overdue fine rate.
 */
public class LoanService {

    private final LoanDAO loanDAO;
    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final FineService fineService;

    public LoanService() {
        this(LoanDAO.getDefault(), BookDAO.getDefault(), UserDAO.getDefault(), new FineService());
    }

    public LoanService(LoanDAO loanDAO, BookDAO bookDAO, UserDAO userDAO, FineService fineService) {
        this.loanDAO = loanDAO;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;
        this.fineService = fineService;
    }

    /**
     * Issues a book to a user enforcing role-based polymorphic rules.
     *
     * @param userId Primary ID of the borrower
     * @param bookId Primary ID of the book
     * @return The newly created Loan
     * @throws IllegalStateException    if book is unavailable or limit reached
     * @throws IllegalArgumentException if user or book not found
     * @throws SecurityException        if user is not authorized to borrow this book
     */
    public Loan issueBook(String userId, int bookId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

        return issueBook(user, book);
    }

    // ========================================================================
    // OOP CONCEPT: METHOD OVERLOADING (Compile-time Polymorphism)
    // ========================================================================

    /**
     * Overloaded issueBook taking User and Book entity references directly.
     */
    public Loan issueBook(User user, Book book) {
        // Dynamic dispatch: use polymorphic loan duration for the user type
        return issueBook(user, book, user.getMaxLoanDays());
    }

    /**
     * Core issue method supporting customized loan durations.
     */
    public Loan issueBook(User user, Book book, int loanDays) {
        // 1. Verify availability (Encapsulated check)
        if (!book.isAvailable()) {
            throw new IllegalStateException("Sorry, no copies of '" + book.getTitle() + "' are currently available.");
        }

        // 2. POLYMORPHIC CHECK: Can this specific user type borrow this book?
        // Student will return false for faculty-only books; Faculty returns true.
        if (!user.canBorrow(book)) {
            throw new SecurityException("Access Restricted: '" + book.getTitle() +
                    "' is reserved for Faculty members only. Students cannot borrow this item.");
        }

        // 3. POLYMORPHIC CHECK: Verify active loan quota
        List<Loan> activeLoans = loanDAO.findActiveLoansByUserId(user.getId());
        if (user.hasReachedBorrowLimit(activeLoans.size())) {
            throw new IllegalStateException("Loan limit reached: " + user.getRole().getDisplayName() +
                    " accounts are limited to " + user.getMaxBooksAllowed() + " simultaneous loans.");
        }

        // 4. Update book inventory (Encapsulated mutation)
        book.borrowCopy();
        bookDAO.update(book);

        // 5. Create loan record (Composition)
        Loan loan = new Loan(0, user, book, loanDays);
        loanDAO.save(loan);

        return loan;
    }

    /**
     * Processes return of a borrowed book, updates inventory, and automatically
     * assesses overdue fines if applicable using polymorphic daily fine rates.
     *
     * @param loanId The loan ID to return
     * @return The updated Loan object
     */
    public Loan returnBook(int loanId) {
        Loan loan = loanDAO.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan record #" + loanId + " not found."));

        if (!loan.isActive()) {
            throw new IllegalStateException("This loan has already been closed on " + loan.getReturnDate());
        }

        // 1. Mark returned
        loan.markReturned(LocalDate.now());

        // 2. Return copy to book inventory (Encapsulated domain logic)
        bookDAO.findById(loan.getBookId()).ifPresent(book -> {
            book.returnCopy();
            bookDAO.update(book);
        });

        // 3. POLYMORPHIC FINE ASSESSMENT: Check if overdue
        userDAO.findById(loan.getUserId()).ifPresent(user -> {
            if (loan.isOverdue()) {
                double fineAmount = loan.calculateFine(user);
                if (fineAmount > 0) {
                    fineService.createFine(
                            user.getId(),
                            loan.getBookId(),
                            (int) Math.round(fineAmount),
                            "Overdue return: " + loan.getOverdueDays() + " days late (Due: " + loan.getDueDate() + ")"
                    );
                }
            }
        });

        loanDAO.update(loan);
        return loan;
    }

    /**
     * Returns all active loans for a given user.
     */
    public List<Loan> getActiveLoans(String userId) {
        return loanDAO.findActiveLoansByUserId(userId);
    }

    /**
     * Returns the full loan history for a user.
     */
    public List<Loan> getUserLoans(String userId) {
        return loanDAO.findByUserId(userId);
    }

    /**
     * Returns all loans across the library (Administrator view).
     */
    public List<Loan> getAllLoans() {
        return loanDAO.findAll();
    }
}

