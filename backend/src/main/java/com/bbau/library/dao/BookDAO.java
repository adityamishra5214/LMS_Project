package com.bbau.library.dao;

import com.bbau.library.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT: ABSTRACTION (Data Access Object Pattern)
 * ============================================================================
 * Defines persistence contracts for Book entities.
 */
public interface BookDAO {

    /**
     * Finds a book by primary key ID.
     */
    Optional<Book> findById(int id);

    /**
     * Finds a book by its unique ISBN.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Retrieves all books in catalog.
     */
    List<Book> findAll();

    /**
     * Searches books matching a general query against title, author, or subject.
     */
    List<Book> search(String query);

    /**
     * Overloaded search filtering by specific fields.
     */
    List<Book> search(String title, String author, String subject);

    /**
     * Persists a new book record.
     */
    boolean save(Book book);

    /**
     * Updates an existing book.
     */
    boolean update(Book book);

    /**
     * Deletes a book by ID.
     */
    boolean delete(int id);

    /**
     * Default factory accessor returning the active DAO implementation.
     */
    static BookDAO getDefault() {
        return BookDAOImpl.getInstance();
    }
}
