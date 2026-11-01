package com.bbau.library;

import com.bbau.library.model.*;
import com.bbau.library.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Formal test suite validating OOP principles across the BBAU Library backend.
 */
public class OOPConceptsTest {

    private AuthService authService;
    private BookService bookService;
    private LoanService loanService;
    private FineService fineService;

    @BeforeEach
    public void setUp() {
        authService = new AuthService();
        bookService = new BookService();
        fineService = new FineService();
        loanService = new LoanService();
    }

    @Test
    @DisplayName("OOP Concept: Inheritance - Subclasses instantiate properly and inherit base User attributes")
    public void testInheritance() {
        User student = UserFactory.createUser(UserRole.STUDENT, "stu_test", "Student Name", "hash");
        User faculty = UserFactory.createUser(UserRole.FACULTY, "fac_test", "Faculty Name", "hash");
        User admin = UserFactory.createUser(UserRole.ADMIN, "adm_test", "Admin Name", "hash");

        assertTrue(student instanceof User, "Student must inherit from User");
        assertTrue(faculty instanceof User, "Faculty must inherit from User");
        assertTrue(admin instanceof User, "Admin must inherit from User");

        assertEquals("stu_test", student.getId());
        assertEquals("fac_test", faculty.getId());
        assertEquals("adm_test", admin.getId());
    }

    @Test
    @DisplayName("OOP Concept: Polymorphism - Dynamic method dispatch applies role-specific limits")
    public void testPolymorphicLimits() {
        User student = new Student("s1", "Student", "p");
        User faculty = new Faculty("f1", "Faculty", "p");
        User admin = new Admin("a1", "Admin", "p");

        // Max books allowed polymorphism
        assertEquals(3, student.getMaxBooksAllowed());
        assertEquals(10, faculty.getMaxBooksAllowed());
        assertEquals(50, admin.getMaxBooksAllowed());

        // Max loan days polymorphism
        assertEquals(14, student.getMaxLoanDays());
        assertEquals(30, faculty.getMaxLoanDays());
        assertEquals(60, admin.getMaxLoanDays());

        // Daily fine rate polymorphism
        assertEquals(5.0, student.getDailyFineRate(), 0.001);
        assertEquals(1.0, faculty.getDailyFineRate(), 0.001);
        assertEquals(0.0, admin.getDailyFineRate(), 0.001);
    }

    @Test
    @DisplayName("OOP Concept: Polymorphism - canBorrow() enforces role-based clearance")
    public void testPolymorphicBorrowingClearance() {
        User student = new Student("s1", "Student", "p");
        User faculty = new Faculty("f1", "Faculty", "p");

        Book generalBook = new Book(1, "Java Programming", "Author", "CS", "123", 2, 2, false, "Intro");
        Book reserveBook = new Book(2, "Advanced Research Only", "Author", "Res", "456", 2, 2, true, "Faculty reserve");

        // Both can borrow general book
        assertTrue(student.canBorrow(generalBook));
        assertTrue(faculty.canBorrow(generalBook));

        // Only faculty can borrow faculty-only book
        assertFalse(student.canBorrow(reserveBook), "Student should NOT be able to borrow faculty-only book");
        assertTrue(faculty.canBorrow(reserveBook), "Faculty should be able to borrow faculty-only book");
    }

    @Test
    @DisplayName("OOP Concept: Encapsulation - Domain invariants protect book inventory state")
    public void testEncapsulationAndInvariants() {
        Book book = new Book(10, "Design Patterns", "GoF", "CS", "999", 2, 1, false, "Classic");

        assertTrue(book.isAvailable());
        book.borrowCopy();
        assertEquals(0, book.getAvailableCopies());
        assertFalse(book.isAvailable());

        // Attempting to borrow when none available should throw IllegalStateException
        assertThrows(IllegalStateException.class, book::borrowCopy);

        // Returning restores copy count
        book.returnCopy();
        assertEquals(1, book.getAvailableCopies());
        assertTrue(book.isAvailable());

        // Cannot return more copies than total inventory
        book.returnCopy();
        assertEquals(2, book.getAvailableCopies());
        assertThrows(IllegalStateException.class, book::returnCopy);
    }

    @Test
    @DisplayName("OOP Concept: Composition - Loan associates User and Book correctly")
    public void testComposition() {
        User student = new Student("s100", "Alice", "p");
        Book book = new Book(50, "Operating Systems", "Tanenbaum", "CS", "111", 5, 5, false, "OS");

        Loan loan = new Loan(999, student, book, student.getMaxLoanDays());

        assertEquals(student.getId(), loan.getUserId());
        assertEquals(book.getId(), loan.getBookId());
        assertTrue(loan.isActive());
        assertEquals(loan.getIssueDate().plusDays(14), loan.getDueDate());
    }

    @Test
    @DisplayName("OOP Concept: Method Overloading - Search operations")
    public void testMethodOverloading() {
        assertNotNull(bookService.search("Algorithms"));
        assertNotNull(bookService.search("Algorithms", "Cormen", "Computer Science"));
    }
}

