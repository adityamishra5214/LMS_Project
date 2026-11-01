package com.bbau.library.dao;

import com.bbau.library.model.Book;
import com.bbau.library.model.Loan;
import com.bbau.library.model.User;
import com.bbau.library.service.LibraryStore;
import com.bbau.library.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete DAO implementation for Loan operations.
 */
public class LoanDAOImpl implements LoanDAO {
    private static final Logger LOGGER = Logger.getLogger(LoanDAOImpl.class.getName());
    private static final LoanDAOImpl INSTANCE = new LoanDAOImpl();

    private final LibraryStore store = LibraryStore.getInstance();

    private LoanDAOImpl() {
    }

    public static LoanDAOImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Loan> findById(int id) {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM loans WHERE id = ?";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToLoan(rs));
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database loan query failed: {0}", e.getMessage());
            }
        }
        Loan loan = store.getLoans().get(id);
        enrichLoanMetadata(loan);
        return Optional.ofNullable(loan);
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        if (userId == null) return Collections.emptyList();
        List<Loan> list = store.getLoans().values().stream()
                .filter(l -> userId.equalsIgnoreCase(l.getUserId()))
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .toList();
        list.forEach(this::enrichLoanMetadata);
        return list;
    }

    @Override
    public List<Loan> findActiveLoansByUserId(String userId) {
        if (userId == null) return Collections.emptyList();
        return findByUserId(userId).stream()
                .filter(Loan::isActive)
                .toList();
    }

    @Override
    public List<Loan> findByBookId(int bookId) {
        List<Loan> list = store.getLoans().values().stream()
                .filter(l -> l.getBookId() == bookId)
                .toList();
        list.forEach(this::enrichLoanMetadata);
        return list;
    }

    @Override
    public List<Loan> findAllActiveLoans() {
        return findAll().stream()
                .filter(Loan::isActive)
                .toList();
    }

    @Override
    public List<Loan> findAll() {
        List<Loan> list = new ArrayList<>(store.getLoans().values());
        list.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        list.forEach(this::enrichLoanMetadata);
        return list;
    }

    @Override
    public boolean save(Loan loan) {
        if (loan == null) return false;

        if (loan.getId() <= 0) {
            loan.setId(store.nextLoanId());
        }
        enrichLoanMetadata(loan);
        store.getLoans().put(loan.getId(), loan);

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "INSERT INTO loans (id, user_id, book_id, issue_date, due_date, return_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE return_date = VALUES(return_date)";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, loan.getId());
                stmt.setString(2, loan.getUserId());
                stmt.setInt(3, loan.getBookId());
                stmt.setDate(4, Date.valueOf(loan.getIssueDate()));
                stmt.setDate(5, Date.valueOf(loan.getDueDate()));
                stmt.setDate(6, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
                stmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database save loan failed: {0}", e.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean update(Loan loan) {
        return save(loan);
    }

    private void enrichLoanMetadata(Loan loan) {
        if (loan == null) return;
        if (loan.getBookTitle() == null || loan.getBookTitle().isBlank()) {
            Book book = store.getBooks().get(loan.getBookId());
            if (book != null) {
                loan.setBookTitle(book.getTitle());
            }
        }
        if (loan.getUserName() == null || loan.getUserName().isBlank()) {
            User user = store.getUsers().get(loan.getUserId());
            if (user != null) {
                loan.setUserName(user.getName());
            }
        }
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setUserId(rs.getString("user_id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setIssueDate(rs.getDate("issue_date").toLocalDate());
        loan.setDueDate(rs.getDate("due_date").toLocalDate());
        Date retDate = rs.getDate("return_date");
        if (retDate != null) {
            loan.setReturnDate(retDate.toLocalDate());
        }
        enrichLoanMetadata(loan);
        return loan;
    }
}

