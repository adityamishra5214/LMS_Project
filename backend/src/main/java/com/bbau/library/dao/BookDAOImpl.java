package com.bbau.library.dao;

import com.bbau.library.model.Book;
import com.bbau.library.service.LibraryStore;
import com.bbau.library.util.DBConnection;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete DAO implementation for Book entities with in-memory store and optional JDBC fallback.
 */
public class BookDAOImpl implements BookDAO {
    private static final Logger LOGGER = Logger.getLogger(BookDAOImpl.class.getName());
    private static final BookDAOImpl INSTANCE = new BookDAOImpl();

    private final LibraryStore store = LibraryStore.getInstance();

    private BookDAOImpl() {
    }

    public static BookDAOImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Book> findById(int id) {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM books WHERE id = ?";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToBook(rs));
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database query book failed; falling back to memory store: {0}", e.getMessage());
            }
        }
        return Optional.ofNullable(store.getBooks().get(id));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) return Optional.empty();

        return store.getBooks().values().stream()
                .filter(b -> isbn.equalsIgnoreCase(b.getIsbn()))
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM books ORDER BY title ASC";
            try (conn; Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                List<Book> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapResultSetToBook(rs));
                }
                return list;
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database findAll books failed: {0}", e.getMessage());
            }
        }
        return new ArrayList<>(store.getBooks().values());
    }

    @Override
    public List<Book> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.trim().toLowerCase();
        return findAll().stream()
                .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(q)) ||
                             (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q)) ||
                             (b.getSubject() != null && b.getSubject().toLowerCase().contains(q)) ||
                             (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(q)))
                .toList();
    }

    @Override
    public List<Book> search(String title, String author, String subject) {
        return findAll().stream()
                .filter(b -> (title == null || title.isBlank() || (b.getTitle() != null && b.getTitle().toLowerCase().contains(title.toLowerCase()))) &&
                             (author == null || author.isBlank() || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(author.toLowerCase()))) &&
                             (subject == null || subject.isBlank() || (b.getSubject() != null && b.getSubject().toLowerCase().contains(subject.toLowerCase()))))
                .toList();
    }

    @Override
    public boolean save(Book book) {
        if (book == null) return false;

        if (book.getId() <= 0) {
            book.setId(store.nextBookId());
        }
        store.getBooks().put(book.getId(), book);

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "INSERT INTO books (id, title, author, subject, isbn, total_copies, available_copies, faculty_only, description) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE title=VALUES(title), author=VALUES(author), subject=VALUES(subject), "
                    + "isbn=VALUES(isbn), total_copies=VALUES(total_copies), available_copies=VALUES(available_copies), "
                    + "faculty_only=VALUES(faculty_only), description=VALUES(description)";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, book.getId());
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getAuthor());
                stmt.setString(4, book.getSubject());
                stmt.setString(5, book.getIsbn());
                stmt.setInt(6, book.getTotalCopies());
                stmt.setInt(7, book.getAvailableCopies());
                stmt.setBoolean(8, book.isFacultyOnly());
                stmt.setString(9, book.getDescription());
                stmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database save book failed: {0}", e.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean update(Book book) {
        return save(book);
    }

    @Override
    public boolean delete(int id) {
        store.getBooks().remove(id);

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn != null) {
            String sql = "DELETE FROM books WHERE id = ?";
            try (conn; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Database delete book failed: {0}", e.getMessage());
            }
        }
        return true;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("subject"),
                rs.getString("isbn"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"),
                rs.getBoolean("faculty_only"),
                rs.getString("description")
        );
    }
}

