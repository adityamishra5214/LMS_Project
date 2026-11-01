package com.bbau.library.service;

import com.bbau.library.model.*;
import com.bbau.library.util.PasswordUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * OOP CONCEPT: SINGLETON PATTERN & COMPOSITION
 * ============================================================================
 * Thread-safe central in-memory store simulating the library database.
 * Pre-populates the university library catalog, default demo users, active loans,
 * and fine histories to enable out-of-the-box evaluation without external DB setup.
 */
public class LibraryStore {

    private static volatile LibraryStore instance;

    // Concurrent collections ensuring thread safety across HTTP servlet requests
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Book> books = new ConcurrentHashMap<>();
    private final Map<Integer, Loan> loans = new ConcurrentHashMap<>();
    private final Map<Integer, Fine> fines = new ConcurrentHashMap<>();
    private final Map<Integer, Review> reviews = new ConcurrentHashMap<>();
    private final Map<Integer, Notification> notifications = new ConcurrentHashMap<>();
    private final Set<String> wishlist = ConcurrentHashMap.newKeySet();

    private final AtomicInteger bookIdSeq = new AtomicInteger(100);
    private final AtomicInteger loanIdSeq = new AtomicInteger(500);
    private final AtomicInteger fineIdSeq = new AtomicInteger(700);
    private final AtomicInteger reviewIdSeq = new AtomicInteger(800);
    private final AtomicInteger notificationIdSeq = new AtomicInteger(900);

    private LibraryStore() {
        seedInitialData();
    }

    /**
     * Singleton accessor with Double-Checked Locking.
     */
    public static LibraryStore getInstance() {
        if (instance == null) {
            synchronized (LibraryStore.class) {
                if (instance == null) {
                    instance = new LibraryStore();
                }
            }
        }
        return instance;
    }

    // ========================================================================
    // SEEDING DEFAULT UNIVERSITY DATA
    // ========================================================================
    private void seedInitialData() {
        // 1. Seed Users via UserFactory (Polymorphic instantiation)
        Admin admin = (Admin) UserFactory.createUser(UserRole.ADMIN, "admin", "Dr. A. K. Sharma (Chief Librarian)", PasswordUtil.hash("admin123"));
        admin.setAdminCode("ADM-BBAU-01");
        users.put(admin.getId(), admin);

        Student student = (Student) UserFactory.createUser(UserRole.STUDENT, "student01", "Aarav Gupta", PasswordUtil.hash("stu123"));
        student.setDepartment("Computer Science");
        student.setRollNumber("CS-2024-042");
        users.put(student.getId(), student);

        Faculty faculty = (Faculty) UserFactory.createUser(UserRole.FACULTY, "faculty01", "Prof. Sunita Verma", PasswordUtil.hash("fac123"));
        faculty.setDepartment("Information Technology");
        faculty.setDesignation("Associate Professor");
        users.put(faculty.getId(), faculty);

        // Additional demo users
        Student student2 = (Student) UserFactory.createUser(UserRole.STUDENT, "student02", "Priya Singh", PasswordUtil.hash("stu123"));
        student2.setDepartment("Biotechnology");
        student2.setRollNumber("BT-2024-019");
        users.put(student2.getId(), student2);

        // 2. Seed Books
        addSeedBook("Introduction to Algorithms (CLRS)", "Thomas H. Cormen, Charles E. Leiserson", "Computer Science", "978-0262033848", 5, 4, false, "The definitive algorithms and data structures reference book.");
        addSeedBook("Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", "Computer Science", "978-0132350884", 4, 3, false, "Best practices in software engineering and object-oriented design.");
        addSeedBook("Design Patterns: Elements of Reusable Object-Oriented Software", "Gang of Four", "Computer Science", "978-0201633610", 3, 2, false, "Classic catalogue of 23 object-oriented design patterns.");
        addSeedBook("Database System Concepts", "Abraham Silberschatz, Henry F. Korth", "Computer Science", "978-0078022159", 4, 3, false, "Comprehensive textbook on relational database systems and SQL.");
        addSeedBook("Artificial Intelligence: A Modern Approach", "Stuart Russell, Peter Norvig", "Artificial Intelligence", "978-0136042594", 4, 3, true, "Advanced faculty reserve textbook covering foundational AI principles.");
        addSeedBook("Higher Engineering Mathematics", "B. S. Grewal", "Mathematics", "978-8174091955", 6, 5, false, "Standard university reference for calculus, differential equations, and linear algebra.");
        addSeedBook("Advanced Research Methodology in Higher Education", "Dr. V. K. Rao", "Research", "978-9351341234", 2, 2, true, "Exclusive faculty-only resource on doctoral research design and thesis ethics.");
        addSeedBook("Principles of Neural Science", "Eric R. Kandel", "Neuroscience", "978-0071390118", 3, 2, true, "Faculty reserve classic on molecular, cellular, and cognitive neurobiology.");
        addSeedBook("To Kill a Mockingbird", "Harper Lee", "Literature", "978-0060935467", 5, 5, false, "Classic American literature addressing justice and human empathy.");
        addSeedBook("Atomic Habits", "James Clear", "Personal Development", "978-0735211292", 5, 4, false, "Practical framework for improving every day through small habits.");

        // 3. Seed Active & Historical Loans
        Book book1 = books.get(101);
        if (book1 != null) {
            Loan loan1 = new Loan(loanIdSeq.incrementAndGet(), student.getId(), book1.getId(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(9));
            loan1.setBookTitle(book1.getTitle());
            loan1.setUserName(student.getName());
            loans.put(loan1.getId(), loan1);
        }

        Book book2 = books.get(102);
        if (book2 != null) {
            Loan loan2 = new Loan(loanIdSeq.incrementAndGet(), faculty.getId(), book2.getId(), LocalDate.now().minusDays(10), LocalDate.now().plusDays(20));
            loan2.setBookTitle(book2.getTitle());
            loan2.setUserName(faculty.getName());
            loans.put(loan2.getId(), loan2);
        }

        // An overdue loan to demonstrate fine computation
        Book book3 = books.get(103);
        if (book3 != null) {
            Loan overdueLoan = new Loan(loanIdSeq.incrementAndGet(), student.getId(), book3.getId(), LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
            overdueLoan.setBookTitle(book3.getTitle());
            overdueLoan.setUserName(student.getName());
            loans.put(overdueLoan.getId(), overdueLoan);

            // Create corresponding fine record
            int fineAmount = (int) (6 * student.getDailyFineRate()); // 6 days * 5 = 30 INR
            Fine fine = new Fine(fineIdSeq.incrementAndGet(), student.getId(), book3.getId(), fineAmount, "Late return of 6 days (due: " + overdueLoan.getDueDate() + ")");
            fine.setBookTitle(book3.getTitle());
            fine.setUserName(student.getName());
            fines.put(fine.getId(), fine);
        }

        // 4. Seed Reviews
        Review review1 = new Review(reviewIdSeq.incrementAndGet(), student.getId(), 102, "Must read for every developer", "Clean Code changed the way I write methods and structure classes. Highly recommended!");
        review1.setUserName(student.getName());
        review1.setBookTitle("Clean Code: A Handbook of Agile Software Craftsmanship");
        reviews.put(review1.getId(), review1);

        // 5. Seed Notifications
        Notification notif1 = new Notification(notificationIdSeq.incrementAndGet(), student.getId(), "Welcome to BBAU Central Library! You may borrow up to 3 books for 14 days.");
        notifications.put(notif1.getId(), notif1);

        Notification notif2 = new Notification(notificationIdSeq.incrementAndGet(), faculty.getId(), "Welcome Prof. Sunita! You have 30-day borrowing access to all reserve collections.");
        notifications.put(notif2.getId(), notif2);
    }

    private void addSeedBook(String title, String author, String subject, String isbn,
                             int total, int available, boolean facultyOnly, String desc) {
        int id = bookIdSeq.incrementAndGet();
        Book book = new Book(id, title, author, subject, isbn, total, available, facultyOnly, desc);
        books.put(id, book);
    }

    // ========================================================================
    // REPOSITORY ACCESSORS (Thread-safe)
    // ========================================================================

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<Integer, Book> getBooks() {
        return books;
    }

    public Map<Integer, Loan> getLoans() {
        return loans;
    }

    public Map<Integer, Fine> getFines() {
        return fines;
    }

    public Map<Integer, Review> getReviews() {
        return reviews;
    }

    public Map<Integer, Notification> getNotifications() {
        return notifications;
    }

    public Set<String> getWishlist() {
        return wishlist;
    }

    public int nextBookId() {
        return bookIdSeq.incrementAndGet();
    }

    public int nextLoanId() {
        return loanIdSeq.incrementAndGet();
    }

    public int nextFineId() {
        return fineIdSeq.incrementAndGet();
    }

    public int nextReviewId() {
        return reviewIdSeq.incrementAndGet();
    }

    public int nextNotificationId() {
        return notificationIdSeq.incrementAndGet();
    }
}

