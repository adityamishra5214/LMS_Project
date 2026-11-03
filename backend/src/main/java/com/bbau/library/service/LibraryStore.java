package com.bbau.library.service;

import com.bbau.library.model.*;
import com.bbau.library.util.PasswordUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * OOP CONCEPT: SINGLETON PATTERN & COMPOSITION
 * ============================================================================
 * Thread-safe central in-memory store simulating the university library database.
 * Pre-populates realistic data reflecting several months of university library operations:
 * - Student, Faculty, and Admin accounts
 * - Diverse technical categories requested by user
 * - Historical returned loans and active/overdue loans
 * - Fines (Late return, damaged book ₹200, lost book ₹500, paid and pending)
 * - Wishlists, Book Reviews (1-5 stars), Discussions & Replies, Notifications
 */
public class LibraryStore {

    private static volatile LibraryStore instance;

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Book> books = new ConcurrentHashMap<>();
    private final Map<Integer, Loan> loans = new ConcurrentHashMap<>();
    private final Map<Integer, Fine> fines = new ConcurrentHashMap<>();
    private final Map<Integer, Review> reviews = new ConcurrentHashMap<>();
    private final Map<Integer, Discussion> discussions = new ConcurrentHashMap<>();
    private final Map<Integer, Notification> notifications = new ConcurrentHashMap<>();
    private final Set<String> wishlist = ConcurrentHashMap.newKeySet(); // "userId:bookId"

    private final AtomicInteger bookIdSeq = new AtomicInteger(100);
    private final AtomicInteger loanIdSeq = new AtomicInteger(500);
    private final AtomicInteger fineIdSeq = new AtomicInteger(700);
    private final AtomicInteger reviewIdSeq = new AtomicInteger(800);
    private final AtomicInteger discussionIdSeq = new AtomicInteger(1000);
    private final AtomicInteger replyIdSeq = new AtomicInteger(2000);
    private final AtomicInteger notificationIdSeq = new AtomicInteger(900);

    private LibraryStore() {
        seedInitialData();
    }

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

    private void seedInitialData() {
        // --------------------------------------------------------------------
        // 1. SEED USERS (Students, Faculty, Admin)
        // --------------------------------------------------------------------
        Admin admin = (Admin) UserFactory.createUser(UserRole.ADMIN, "admin", "Dr. A. K. Sharma (Chief Librarian)", PasswordUtil.hash("admin123"));
        admin.setAdminCode("ADM-BBAU-01");
        users.put(admin.getId(), admin);

        Student student1 = (Student) UserFactory.createUser(UserRole.STUDENT, "student01", "Aarav Gupta", PasswordUtil.hash("stu123"));
        student1.setDepartment("Computer Science & Engineering");
        student1.setRollNumber("BBAU-CS-2024-042");
        users.put(student1.getId(), student1);

        Student student2 = (Student) UserFactory.createUser(UserRole.STUDENT, "student02", "Priya Singh", PasswordUtil.hash("stu123"));
        student2.setDepartment("Information Technology");
        student2.setRollNumber("BBAU-IT-2024-019");
        users.put(student2.getId(), student2);

        Student student3 = (Student) UserFactory.createUser(UserRole.STUDENT, "student03", "Rohan Verma", PasswordUtil.hash("stu123"));
        student3.setDepartment("Electronics & Communication");
        student3.setRollNumber("BBAU-EC-2024-088");
        users.put(student3.getId(), student3);

        Faculty faculty1 = (Faculty) UserFactory.createUser(UserRole.FACULTY, "faculty01", "Prof. Sunita Verma", PasswordUtil.hash("fac123"));
        faculty1.setDepartment("Computer Science & Engineering");
        faculty1.setDesignation("Associate Professor");
        users.put(faculty1.getId(), faculty1);

        Faculty faculty2 = (Faculty) UserFactory.createUser(UserRole.FACULTY, "faculty02", "Dr. Rajesh Kumar", PasswordUtil.hash("fac123"));
        faculty2.setDepartment("Electrical & Electronics Engineering");
        faculty2.setDesignation("Professor & Dean");
        users.put(faculty2.getId(), faculty2);

        // --------------------------------------------------------------------
        // 2. SEED BOOKS (Across all required user categories)
        // --------------------------------------------------------------------
        // Core Java
        addBook(101, "Effective Java (3rd Edition)", "Joshua Bloch", "Former Chief Java Architect at Google.",
                "Core Java", "978-0134685991", 6, 4, false,
                "The essential guide for writing robust, maintainable, and idiomatic Java code with 90 best-practice items.", "from-amber-600 to-amber-800");

        addBook(102, "Java: The Complete Reference (12th Ed)", "Herbert Schildt", "Prolific computer science author.",
                "Core Java", "978-1260440232", 5, 3, false,
                "Comprehensive reference covering the entire Java programming language syntax, libraries, and runtime architecture.", "from-orange-600 to-orange-800");

        // C and C++
        addBook(103, "The C Programming Language (ANSI C)", "Brian W. Kernighan, Dennis M. Ritchie", "Creators of C and Unix at Bell Labs.",
                "C and C++", "978-0131103627", 5, 3, false,
                "The definitive handbook on the C language by its inventors. Covers pointers, memory management, and system calls.", "from-blue-600 to-blue-800");

        addBook(104, "The C++ Programming Language (4th Ed)", "Bjarne Stroustrup", "Creator of C++.",
                "C and C++", "978-0321563842", 4, 2, false,
                "Exhaustive guide to modern C++ standard, generic programming, move semantics, and the Standard Template Library (STL).", "from-indigo-600 to-indigo-800");

        // Data Structures and Algorithms
        addBook(105, "Introduction to Algorithms (CLRS 4th Ed)", "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein", "MIT & Dartmouth Professors.",
                "Data Structures and Algorithms", "978-0262046305", 8, 5, false,
                "The global gold standard textbook on algorithms, graph theory, dynamic programming, and complexity analysis.", "from-emerald-600 to-emerald-800");

        addBook(106, "Data Structures & Algorithm Analysis in C++", "Mark Allen Weiss", "Professor at Florida International University.",
                "Data Structures and Algorithms", "978-0132847377", 4, 3, false,
                "In-depth analysis of trees, priority queues, sorting algorithms, and hash tables with practical C++ implementations.", "from-teal-600 to-teal-800");

        // DBMS
        addBook(107, "Database System Concepts (7th Edition)", "Abraham Silberschatz, Henry F. Korth, S. Sudarshan", "Pioneering database researchers.",
                "DBMS", "978-0078022159", 6, 4, false,
                "Foundational textbook exploring relational algebra, SQL optimization, transactions, concurrency, and distributed databases.", "from-cyan-600 to-cyan-800");

        addBook(108, "Fundamentals of Database Systems", "Ramez Elmasri, Shamkant B. Navathe", "University of Texas & Georgia Tech faculty.",
                "DBMS", "978-0133970777", 4, 2, false,
                "Rigorous guide to ER-modeling, normalization, indexing structures (B-Trees), and modern NoSQL systems.", "from-sky-600 to-sky-800");

        // Operating Systems
        addBook(109, "Operating System Concepts (Silberschatz Dinosaur Book)", "Abraham Silberschatz, Peter B. Galvin, Greg Gagne", "World-renowned systems authors.",
                "Operating Systems", "978-1119800361", 7, 5, false,
                "Fundamental guide covering processes, threads, CPU scheduling, deadlocks, virtual memory management, and file systems.", "from-purple-600 to-purple-800");

        addBook(110, "Modern Operating Systems (4th Edition)", "Andrew S. Tanenbaum, Herbert Bos", "Pioneer behind MINIX.",
                "Operating Systems", "978-0133591620", 4, 3, false,
                "Explores operating system architecture, security vulnerabilities, multiprocessor scheduling, and virtualization.", "from-violet-600 to-violet-800");

        // Computer Networks
        addBook(111, "Computer Networking: A Top-Down Approach", "James F. Kurose, Keith W. Ross", "UMass Amherst and NYU professors.",
                "Computer Networks", "978-0136681557", 6, 4, false,
                "Teaches computer networks from Application Layer down through Transport, Network, and Link layers with Wireshark labs.", "from-rose-600 to-rose-800");

        addBook(112, "Computer Networks (5th Edition)", "Andrew S. Tanenbaum, David J. Wetherall", "Vrije Universiteit & University of Washington.",
                "Computer Networks", "978-0132126953", 4, 2, false,
                "Classic text detailing OSI reference model, routing protocols, TCP/IP congestion control, and wireless networks.", "from-pink-600 to-pink-800");

        // Digital Electronics
        addBook(113, "Digital Electronics: Principles & Integrated Circuits", "Anil K. Maini", "Distinguished scientist and defense electronics authority.",
                "Digital Electronics", "978-8126514663", 5, 4, false,
                "Comprehensive resource on Boolean logic, logic gates, combinational and sequential circuits, and IC families.", "from-red-600 to-red-800");

        // Digital Design
        addBook(114, "Digital Design: With Introduction to Verilog HDL", "M. Morris Mano, Michael D. Ciletti", "Emeritus professors in electrical engineering.",
                "Digital Design", "978-0132774208", 6, 4, false,
                "The quintessential university textbook for digital hardware synthesis, state machines, registers, and HDL coding.", "from-amber-700 to-amber-900");

        // Engineering Mathematics
        addBook(115, "Higher Engineering Mathematics (44th Ed)", "B. S. Grewal", "Renowned engineering mathematics professor.",
                "Engineering Mathematics", "978-8174091955", 10, 7, false,
                "Indispensable handbook for differential equations, Laplace transforms, Fourier series, linear algebra, and complex numbers.", "from-emerald-700 to-emerald-900");

        // Artificial Intelligence
        addBook(116, "Artificial Intelligence: A Modern Approach (4th Ed)", "Stuart Russell, Peter Norvig", "UC Berkeley & Google Research Directors.",
                "Artificial Intelligence", "978-0134610993", 6, 4, false,
                "Comprehensive survey of rational agents, search heuristics, probabilistic reasoning, reinforcement learning, and neural models.", "from-fuchsia-600 to-fuchsia-800");

        // General Academic
        addBook(117, "To Kill a Mockingbird", "Harper Lee", "Pulitzer Prize winning novelist.",
                "General Academic", "978-0060935467", 5, 5, false,
                "Classic literary exploration of social ethics, legal integrity, and human empathy in twentieth-century society.", "from-stone-600 to-stone-800");

        addBook(118, "Atomic Habits: Proven Framework", "James Clear", "Behavioral design researcher.",
                "General Academic", "978-0735211292", 6, 5, false,
                "Practical scientific blueprint for continuous micro-improvements, habit loops, and personal productivity.", "from-slate-600 to-slate-800");

        // Faculty-Only Section (Advanced Reference Materials)
        addBook(119, "Advanced Research Methodology in Higher Education", "Dr. V. K. Rao", "UGC Chair on Educational Research.",
                "Faculty-Only Reserve", "978-9351341234", 3, 3, true,
                "[FACULTY ONLY] Doctoral research guidelines, citation ethics, peer-review standards, and quantitative statistical validation.", "from-yellow-600 to-yellow-800");

        addBook(120, "Quantum Computation and Quantum Information", "Michael A. Nielsen, Isaac L. Chuang", "Pioneers at Los Alamos & MIT.",
                "Faculty-Only Reserve", "978-1107002173", 3, 2, true,
                "[FACULTY ONLY] Advanced doctoral reference on qubits, quantum algorithms (Shor & Grover), quantum cryptography, and error correction.", "from-yellow-700 to-yellow-900");

        addBook(121, "Microprocessor Architecture, Programming & Applications", "Ramesh S. Gaonkar", "SUNY Onondaga faculty authority.",
                "Faculty-Only Reserve", "978-8187972884", 2, 2, true,
                "[FACULTY ONLY] Faculty laboratory reference on 8085/8086 bus timing diagrams, interrupt architecture, and assembly instruction sets.", "from-yellow-800 to-amber-950");

        // --------------------------------------------------------------------
        // 3. SEED PREVIOUS BORROWING RECORDS (Operating for several months)
        // --------------------------------------------------------------------
        // Past returned loans
        addHistoricalLoan(501, "student01", 103, "The C Programming Language (ANSI C)", LocalDate.now().minusMonths(3), LocalDate.now().minusMonths(3).plusDays(14), LocalDate.now().minusMonths(3).plusDays(12));
        addHistoricalLoan(502, "student02", 107, "Database System Concepts (7th Edition)", LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(2).plusDays(14), LocalDate.now().minusMonths(2).plusDays(10));
        addHistoricalLoan(503, "faculty01", 119, "Advanced Research Methodology in Higher Education", LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(2).plusDays(30), LocalDate.now().minusMonths(2).plusDays(25));
        addHistoricalLoan(504, "student01", 115, "Higher Engineering Mathematics (44th Ed)", LocalDate.now().minusWeeks(6), LocalDate.now().minusWeeks(4), LocalDate.now().minusWeeks(4));

        // Active active loans
        addActiveLoan(505, "student01", 101, "Effective Java (3rd Edition)", LocalDate.now().minusDays(6), LocalDate.now().plusDays(8));
        addActiveLoan(506, "student02", 105, "Introduction to Algorithms (CLRS 4th Ed)", LocalDate.now().minusDays(4), LocalDate.now().plusDays(10));
        addActiveLoan(507, "faculty01", 120, "Quantum Computation and Quantum Information", LocalDate.now().minusDays(12), LocalDate.now().plusDays(18));

        // OVERDUE LOAN (Generates late return fine)
        Loan overdueLoan = new Loan(loanIdSeq.incrementAndGet(), "student01", 109, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
        overdueLoan.setBookTitle("Operating System Concepts (Silberschatz Dinosaur Book)");
        overdueLoan.setUserName("Aarav Gupta");
        loans.put(overdueLoan.getId(), overdueLoan);

        // --------------------------------------------------------------------
        // 4. SEED FINES (Late return, damaged book ₹200, lost book ₹500)
        // --------------------------------------------------------------------
        // Fine 1: Overdue return fine (₹5 * 6 days = ₹30)
        Fine overdueFine = new Fine(fineIdSeq.incrementAndGet(), "student01", 109, 30, "LATE_RETURN", "Overdue book loan: 6 days past due date (Due: " + overdueLoan.getDueDate() + ")");
        overdueFine.setBookTitle("Operating System Concepts");
        overdueFine.setUserName("Aarav Gupta");
        fines.put(overdueFine.getId(), overdueFine);

        // Fine 2: Damaged book fine (₹200) - Unpaid
        Fine damagedFine = new Fine(fineIdSeq.incrementAndGet(), "student01", 103, Fine.FINE_DAMAGED_BOOK, "DAMAGED_BOOK", "Damaged book spine and water damage reported on return.");
        damagedFine.setBookTitle("The C Programming Language (ANSI C)");
        damagedFine.setUserName("Aarav Gupta");
        fines.put(damagedFine.getId(), damagedFine);

        // Fine 3: Lost book fine (₹500) - Paid with simulated payment gateway
        Fine lostFinePaid = new Fine(fineIdSeq.incrementAndGet(), "student01", 112, Fine.FINE_LOST_BOOK, "LOST_BOOK", "Lost book replacement charge for Computer Networks.");
        lostFinePaid.setBookTitle("Computer Networks (5th Edition)");
        lostFinePaid.setUserName("Aarav Gupta");
        lostFinePaid.markAsPaid("UPI (PhonePe / GPay)", "TXN-BBAU-892173");
        fines.put(lostFinePaid.getId(), lostFinePaid);

        // Fine 4: Paid overdue fine from 2 months ago
        Fine paidOldFine = new Fine(fineIdSeq.incrementAndGet(), "student02", 107, 15, "LATE_RETURN", "Late return: 3 days late.");
        paidOldFine.setBookTitle("Database System Concepts");
        paidOldFine.setUserName("Priya Singh");
        paidOldFine.markAsPaid("Debit Card (HDFC)", "TXN-BBAU-332145");
        fines.put(paidOldFine.getId(), paidOldFine);

        // --------------------------------------------------------------------
        // 5. SEED WISHLISTS
        // --------------------------------------------------------------------
        wishlist.add("student01:105"); // CLRS
        wishlist.add("student01:116"); // AI Russell & Norvig
        wishlist.add("faculty01:121"); // Microprocessor

        // --------------------------------------------------------------------
        // 6. SEED REVIEWS (1-5 Stars)
        // --------------------------------------------------------------------
        addReview(reviewIdSeq.incrementAndGet(), "student01", "Aarav Gupta", 101, "Effective Java (3rd Edition)", 5,
                "Essential reading for Java developers", "The tips on generics, immutability, and builders fundamentally improved my Java code quality.");

        addReview(reviewIdSeq.incrementAndGet(), "student02", "Priya Singh", 105, "Introduction to Algorithms (CLRS 4th Ed)", 5,
                "Tough but unbeatably thorough", "Pseudocode is precise and clear. Best textbook for mastering graph algorithms and dynamic programming.");

        addReview(reviewIdSeq.incrementAndGet(), "faculty01", "Prof. Sunita Verma", 109, "Operating System Concepts (Silberschatz Dinosaur Book)", 4,
                "Standard curriculum textbook", "Highly recommended for second-year OS courses. Chapter on synchronization primitives is especially strong.");

        addReview(reviewIdSeq.incrementAndGet(), "student03", "Rohan Verma", 113, "Digital Electronics: Principles & Integrated Circuits", 5,
                "Very lucid explanations", "Helped me easily pass my semester exam on multiplexers and flip-flops.");

        // --------------------------------------------------------------------
        // 7. SEED DISCUSSIONS & REPLIES
        // --------------------------------------------------------------------
        Discussion disc1 = new Discussion(discussionIdSeq.incrementAndGet(), 105, "Introduction to Algorithms (CLRS 4th Ed)",
                "student01", "Aarav Gupta", UserRole.STUDENT,
                "Difference between Dijkstra and Bellman-Ford for negative weights?",
                "Can someone clarify why Dijkstra fails with negative edge weights while Bellman-Ford succeeds?");
        disc1.addReply(new DiscussionReply(replyIdSeq.incrementAndGet(), disc1.getId(), "faculty01", "Prof. Sunita Verma", UserRole.FACULTY,
                "Dijkstra assumes that once a node is marked visited, its shortest distance is finalized (greedy choice). Negative weights violate this sub-problem optimality. Bellman-Ford relaxes all edges |V|-1 times, properly handling negative edges."));
        disc1.addReply(new DiscussionReply(replyIdSeq.incrementAndGet(), disc1.getId(), "student02", "Priya Singh", UserRole.STUDENT,
                "Thank you Prof. Verma! That makes total sense when tracing through a cycle diagram."));
        discussions.put(disc1.getId(), disc1);

        Discussion disc2 = new Discussion(discussionIdSeq.incrementAndGet(), 107, "Database System Concepts (7th Edition)",
                "student02", "Priya Singh", UserRole.STUDENT,
                "3NF vs BCNF real-world trade-off?",
                "When should we accept a 3NF schema instead of strictly normalizing to BCNF in production systems?");
        disc2.addReply(new DiscussionReply(replyIdSeq.incrementAndGet(), disc2.getId(), "faculty01", "Prof. Sunita Verma", UserRole.FACULTY,
                "We prefer 3NF when BCNF normalization would lose dependency preservation. Preserving functional dependencies avoids expensive inter-table join assertions."));
        discussions.put(disc2.getId(), disc2);

        // --------------------------------------------------------------------
        // 8. SEED NOTIFICATIONS (All required types)
        // --------------------------------------------------------------------
        addNotif("student01", Notification.TYPE_ISSUE, "Book Issued Successfully", "You have successfully checked out 'Effective Java (3rd Edition)'. Due back on " + LocalDate.now().plusDays(8) + ".");
        addNotif("student01", Notification.TYPE_DUE_REMINDER, "Upcoming Due Date Reminder", "Your borrowed book 'Operating System Concepts' is due soon. Please return on time to avoid ₹5/day fine.");
        addNotif("student01", Notification.TYPE_OVERDUE, "Overdue Notice & Fine Warning", "URGENT: 'Operating System Concepts' is 6 days overdue! An overdue fine of ₹30 has accrued.");
        addNotif("student01", Notification.TYPE_FINE, "Damaged Book Fine Assessed", "A fine of ₹200 was recorded for book spine damage on 'The C Programming Language'.");
        addNotif("student01", Notification.TYPE_PAYMENT, "Payment Confirmation Received", "Payment of ₹500 for Lost Book replacement received via UPI. Reference: TXN-BBAU-892173.");
        addNotif("student01", Notification.TYPE_RETURN, "Book Return Receipt", "Return confirmed for 'Higher Engineering Mathematics'. Thank you for returning in good condition.");
        addNotif("faculty01", Notification.TYPE_ISSUE, "Faculty Checkout Confirmed", "Checked out 'Quantum Computation and Quantum Information' for 30-day extended academic loan.");
    }

    private void addBook(int id, String title, String author, String authorInfo, String subject,
                         String isbn, int total, int available, boolean facultyOnly, String desc, String color) {
        Book book = new Book(id, title, author, authorInfo, subject, isbn, total, available, facultyOnly, desc, color);
        books.put(id, book);
    }

    private void addHistoricalLoan(int id, String userId, int bookId, String title, LocalDate issue, LocalDate due, LocalDate ret) {
        Loan loan = new Loan(id, userId, bookId, issue, due);
        loan.setReturnDate(ret);
        loan.setBookTitle(title);
        User u = users.get(userId);
        if (u != null) loan.setUserName(u.getName());
        loans.put(id, loan);
    }

    private void addActiveLoan(int id, String userId, int bookId, String title, LocalDate issue, LocalDate due) {
        Loan loan = new Loan(id, userId, bookId, issue, due);
        loan.setBookTitle(title);
        User u = users.get(userId);
        if (u != null) loan.setUserName(u.getName());
        loans.put(id, loan);
    }

    private void addReview(int id, String userId, String userName, int bookId, String bookTitle, int rating, String title, String body) {
        Review r = new Review(id, userId, bookId, rating, title, body);
        r.setUserName(userName);
        r.setBookTitle(bookTitle);
        reviews.put(id, r);
    }

    private void addNotif(String userId, String type, String title, String message) {
        Notification n = new Notification(notificationIdSeq.incrementAndGet(), userId, type, title, message);
        notifications.put(n.getId(), n);
    }

    // Accessors
    public Map<String, User> getUsers() { return users; }
    public Map<Integer, Book> getBooks() { return books; }
    public Map<Integer, Loan> getLoans() { return loans; }
    public Map<Integer, Fine> getFines() { return fines; }
    public Map<Integer, Review> getReviews() { return reviews; }
    public Map<Integer, Discussion> getDiscussions() { return discussions; }
    public Map<Integer, Notification> getNotifications() { return notifications; }
    public Set<String> getWishlist() { return wishlist; }

    public int nextBookId() { return bookIdSeq.incrementAndGet(); }
    public int nextLoanId() { return loanIdSeq.incrementAndGet(); }
    public int nextFineId() { return fineIdSeq.incrementAndGet(); }
    public int nextReviewId() { return reviewIdSeq.incrementAndGet(); }
    public int nextDiscussionId() { return discussionIdSeq.incrementAndGet(); }
    public int nextReplyId() { return replyIdSeq.incrementAndGet(); }
    public int nextNotificationId() { return notificationIdSeq.incrementAndGet(); }
}
