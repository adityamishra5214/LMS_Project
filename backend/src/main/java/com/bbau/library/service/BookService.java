package com.bbau.library.service;

import com.bbau.library.dao.BookDAO;
import com.bbau.library.model.Book;
import com.bbau.library.model.User;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT: SINGLE RESPONSIBILITY & METHOD OVERLOADING
 * ============================================================================
 * BookService coordinates book catalog operations, inventory modifications,
 * and search filtering. It guards administrative actions using caller role checks.
 */
public class BookService {

    private final BookDAO bookDAO;

    public BookService() {
        this(BookDAO.getDefault());
    }

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Retrieves all books in library repository.
     */
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    /**
     * Retrieves a book by primary key ID.
     */
    public Optional<Book> getBookById(int id) {
        return bookDAO.findById(id);
    }

    // ========================================================================
    // OOP CONCEPT: METHOD OVERLOADING (Compile-time Polymorphism)
    // ========================================================================

    /**
     * Search books by keyword across title, author, subject, and ISBN.
     */
    public List<Book> search(String keyword) {
        return bookDAO.search(keyword);
    }

    /**
     * Overloaded search filtering by specific catalog fields.
     */
    public List<Book> search(String title, String author, String subject) {
        return bookDAO.search(title, author, subject);
    }

    /**
     * Returns only books currently available for borrowing.
     */
    public List<Book> getAvailableBooks() {
        return getAllBooks().stream()
                .filter(Book::isAvailable)
                .toList();
    }

    /**
     * Adds a new book to the catalog. Requires administrator authorization.
     */
    public boolean addBook(Book book, User operator) {
        if (operator == null || !operator.isAdmin()) {
            throw new SecurityException("Unauthorized: Only library administrators can add books.");
        }
        if (book == null) {
            return false;
        }
        return bookDAO.save(book);
    }

    /**
     * Updates an existing book. Requires administrator authorization.
     */
    public boolean updateBook(Book book, User operator) {
        if (operator == null || !operator.isAdmin()) {
            throw new SecurityException("Unauthorized: Only library administrators can update books.");
        }
        if (book == null) {
            return false;
        }
        return bookDAO.update(book);
    }

    /**
     * Deletes a book from the catalog. Requires administrator authorization.
     */
    public boolean deleteBook(int id, User operator) {
        if (operator == null || !operator.isAdmin()) {
            throw new SecurityException("Unauthorized: Only library administrators can delete books.");
        }
        return bookDAO.delete(id);
    }
}

