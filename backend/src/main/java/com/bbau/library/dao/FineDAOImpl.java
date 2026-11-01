package com.bbau.library.dao;

import com.bbau.library.model.Book;
import com.bbau.library.model.Fine;
import com.bbau.library.model.User;
import com.bbau.library.service.LibraryStore;
import com.bbau.library.util.DBConnection;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete DAO implementation for Fine management.
 */
public class FineDAOImpl implements FineDAO {
    private static final Logger LOGGER = Logger.getLogger(FineDAOImpl.class.getName());
    private static final FineDAOImpl INSTANCE = new FineDAOImpl();

    private final LibraryStore store = LibraryStore.getInstance();

    private FineDAOImpl() {
    }

    public static FineDAOImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Fine> findById(int id) {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM fines WHERE id = ?";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToFine(rs));
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database fine query failed: {0}", e.getMessage());
            }
        }
        Fine fine = store.getFines().get(id);
        enrichFineMetadata(fine);
        return Optional.ofNullable(fine);
    }

    @Override
    public List<Fine> findByUserId(String userId) {
        if (userId == null) return Collections.emptyList();
        List<Fine> list = store.getFines().values().stream()
                .filter(f -> userId.equalsIgnoreCase(f.getUserId()))
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .toList();
        list.forEach(this::enrichFineMetadata);
        return list;
    }

    @Override
    public List<Fine> findUnpaidByUserId(String userId) {
        if (userId == null) return Collections.emptyList();
        return findByUserId(userId).stream()
                .filter(f -> !f.isPaid())
                .toList();
    }

    @Override
    public List<Fine> findAll() {
        List<Fine> list = new ArrayList<>(store.getFines().values());
        list.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        list.forEach(this::enrichFineMetadata);
        return list;
    }

    @Override
    public boolean save(Fine fine) {
        if (fine == null) return false;

        if (fine.getId() <= 0) {
            fine.setId(store.nextFineId());
        }
        enrichFineMetadata(fine);
        store.getFines().put(fine.getId(), fine);

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "INSERT INTO fines (id, user_id, book_id, amount, reason, paid) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE amount = VALUES(amount), paid = VALUES(paid)";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, fine.getId());
                stmt.setString(2, fine.getUserId());
                stmt.setInt(3, fine.getBookId());
                stmt.setInt(4, fine.getAmount());
                stmt.setString(5, fine.getReason());
                stmt.setBoolean(6, fine.isPaid());
                stmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database save fine failed: {0}", e.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean update(Fine fine) {
        return save(fine);
    }

    private void enrichFineMetadata(Fine fine) {
        if (fine == null) return;
        if (fine.getBookTitle() == null || fine.getBookTitle().isBlank()) {
            Book book = store.getBooks().get(fine.getBookId());
            if (book != null) {
                fine.setBookTitle(book.getTitle());
            }
        }
        if (fine.getUserName() == null || fine.getUserName().isBlank()) {
            User user = store.getUsers().get(fine.getUserId());
            if (user != null) {
                fine.setUserName(user.getName());
            }
        }
    }

    private Fine mapResultSetToFine(ResultSet rs) throws SQLException {
        Fine fine = new Fine();
        fine.setId(rs.getInt("id"));
        fine.setUserId(rs.getString("user_id"));
        fine.setBookId(rs.getInt("book_id"));
        fine.setAmount(rs.getInt("amount"));
        fine.setReason(rs.getString("reason"));
        fine.setPaid(rs.getBoolean("paid"));
        enrichFineMetadata(fine);
        return fine;
    }
}

