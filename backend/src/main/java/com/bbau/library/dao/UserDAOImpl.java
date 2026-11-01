package com.bbau.library.dao;

import com.bbau.library.model.*;
import com.bbau.library.service.LibraryStore;
import com.bbau.library.util.DBConnection;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete DAO implementation supporting in-memory storage with transparent
 * JDBC MySQL persistence when a database connection is available.
 */
public class UserDAOImpl implements UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());
    private static final UserDAOImpl INSTANCE = new UserDAOImpl();

    private final LibraryStore store = LibraryStore.getInstance();

    private UserDAOImpl() {
    }

    public static UserDAOImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null) return Optional.empty();

        // Check live database if connected
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "SELECT id, name, password_hash, role FROM users WHERE id = ?";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = UserFactory.createUser(
                                rs.getString("role"),
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("password_hash")
                        );
                        return Optional.of(user);
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database query failed; falling back to memory store: {0}", e.getMessage());
            }
        }

        // In-memory fallback
        return Optional.ofNullable(store.getUsers().get(id));
    }

    @Override
    public List<User> findAll() {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "SELECT id, name, password_hash, role FROM users ORDER BY name ASC";
            try (conn; Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                List<User> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(UserFactory.createUser(
                            rs.getString("role"),
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("password_hash")
                    ));
                }
                return list;
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database findAll failed; falling back to memory store: {0}", e.getMessage());
            }
        }
        return new ArrayList<>(store.getUsers().values());
    }

    @Override
    public List<User> findByRole(UserRole role) {
        if (role == null) return findAll();
        return findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }

    @Override
    public boolean save(User user) {
        if (user == null) return false;

        store.getUsers().put(user.getId(), user);

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "INSERT INTO users (id, name, password_hash, role) VALUES (?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE name = VALUES(name), password_hash = VALUES(password_hash), role = VALUES(role)";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getId());
                stmt.setString(2, user.getName());
                stmt.setString(3, user.getPasswordHash());
                stmt.setString(4, user.getRole().getCode());
                stmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database save user failed: {0}", e.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean update(User user) {
        return save(user);
    }

    @Override
    public boolean delete(String id) {
        if (id == null) return false;
        store.getUsers().remove(id);

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "DELETE FROM users WHERE id = ?";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database delete user failed: {0}", e.getMessage());
            }
        }
        return true;
    }
}

