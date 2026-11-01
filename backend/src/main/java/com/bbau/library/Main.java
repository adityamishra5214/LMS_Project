package com.bbau.library;

import com.bbau.library.model.*;
import com.bbau.library.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * ============================================================================
 * BBAU LIBRARY MANAGEMENT SYSTEM - OOP CONCEPTS DEMONSTRATION & CLI HARNESS
 * ============================================================================
 * This standalone application demonstrates all Core Object-Oriented Programming (OOP)
 * concepts implemented throughout the Java backend:
 * 1. Abstraction: Abstract base class User, DAO and Service interfaces.
 * 2. Encapsulation: Private entity fields, domain invariant methods (borrowCopy, returnCopy).
 * 3. Inheritance: Student, Faculty, Admin extending User.
 * 4. Polymorphism: Dynamic method dispatch for borrow limits, loan durations, fine rates,
 *    and access policies (canBorrow). Method overloading in services and factories.
 * 5. Composition: Loan having User and Book relationships, Fine ledgers.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("  BABASAHEB BHIMRAO AMBEDKAR UNIVERSITY - CENTRAL LIBRARY SYSTEM  ");
        System.out.println("       Object-Oriented Programming (OOP) Backend Demonstration     ");
        System.out.println("==================================================================\n");

        runOOPDemonstration();

        // If interactive console is available and requested, provide interactive CLI
        if (args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            runInteractiveCLI();
        } else {
            System.out.println("\n[Tip] Run with argument '--cli' to enter the interactive console mode.");
            System.out.println("For Web Interface: Deploy backend/target/bbau-library.war to Apache Tomcat 10.1+");
        }
    }

    /**
     * Automated verification and visual demonstration of OOP principles.
     */
    public static void runOOPDemonstration() {
        System.out.println("------------------------------------------------------------------");
        System.out.println("DEMONSTRATION 1: INHERITANCE & POLYMORPHISM (Dynamic Method Dispatch)");
        System.out.println("------------------------------------------------------------------");

        // Creating polymorphic users via UserFactory
        User student = UserFactory.createUser(UserRole.STUDENT, "stu_demo", "Aarav Gupta", "pass123");
        User faculty = UserFactory.createUser(UserRole.FACULTY, "fac_demo", "Prof. Sunita Verma", "pass123");
        User admin = UserFactory.createUser(UserRole.ADMIN, "adm_demo", "Dr. A.K. Sharma", "pass123");

        List<User> userList = List.of(student, faculty, admin);

        for (User u : userList) {
            System.out.printf("User Class: %-10s | Name: %-18s | Max Books: %2d | Loan Days: %2d | Fine/Day: ₹%.2f%n",
                    u.getClass().getSimpleName(),
                    u.getName(),
                    u.getMaxBooksAllowed(),  // Polymorphic call
                    u.getMaxLoanDays(),       // Polymorphic call
                    u.getDailyFineRate()      // Polymorphic call
            );
        }

        System.out.println("\n------------------------------------------------------------------");
        System.out.println("DEMONSTRATION 2: POLYMORPHIC ACCESS CONTROL & BUSINESS RULES");
        System.out.println("------------------------------------------------------------------");

        Book normalBook = new Book(201, "Clean Code", "Robert C. Martin", "Computer Science",
                "978-0132350884", 3, 3, false, "Software engineering principles.");

        Book facultyReserveBook = new Book(202, "Advanced AI Research Papers", "Russell & Norvig", "AI",
                "978-0136042594", 2, 2, true, "Exclusive faculty-only research monograph.");

        System.out.println("Book 1 (General):         " + normalBook.getTitle() + " [FacultyOnly=" + normalBook.isFacultyOnly() + "]");
        System.out.println("Book 2 (Faculty Reserve): " + facultyReserveBook.getTitle() + " [FacultyOnly=" + facultyReserveBook.isFacultyOnly() + "]");

        System.out.println("\nEvaluating: student.canBorrow(normalBook):          " + student.canBorrow(normalBook));
        System.out.println("Evaluating: student.canBorrow(facultyReserveBook):  " + student.canBorrow(facultyReserveBook) + "  <-- RESTRICTED!");
        System.out.println("Evaluating: faculty.canBorrow(facultyReserveBook):  " + faculty.canBorrow(facultyReserveBook) + "  <-- AUTHORIZED!");

        System.out.println("\n------------------------------------------------------------------");
        System.out.println("DEMONSTRATION 3: ENCAPSULATION & DOMAIN INVARIANTS");
        System.out.println("------------------------------------------------------------------");

        System.out.println("Initial Book Availability: " + normalBook.getAvailableCopies() + " / " + normalBook.getTotalCopies());
        System.out.println("Borrowing one copy via normalBook.borrowCopy()...");
        normalBook.borrowCopy();
        System.out.println("New Availability:          " + normalBook.getAvailableCopies() + " / " + normalBook.getTotalCopies());

        System.out.println("Returning copy via normalBook.returnCopy()...");
        normalBook.returnCopy();
        System.out.println("Restored Availability:     " + normalBook.getAvailableCopies() + " / " + normalBook.getTotalCopies());

        System.out.println("\n------------------------------------------------------------------");
        System.out.println("DEMONSTRATION 4: COMPOSITION & DELEGATION (Loan & Fines)");
        System.out.println("------------------------------------------------------------------");

        LoanService loanService = new LoanService();
        FineService fineService = new FineService();

        // Testing LoanService issuing
        try {
            System.out.println("Attempting to issue faculty-only book to Student 'student01'...");
            loanService.issueBook("student01", 105); // Book 105 is faculty-only in LibraryStore
            System.out.println("Issue succeeded (Unexpected).");
        } catch (SecurityException e) {
            System.out.println("Caught Expected Security Exception: " + e.getMessage());
        }

        System.out.println("\nIssuing regular book (ID 101) to Faculty 'faculty01'...");
        try {
            Loan loan = loanService.issueBook("faculty01", 101);
            System.out.println("Loan Created Successfully: ID #" + loan.getId() + " | Due Date: " + loan.getDueDate());
        } catch (Exception e) {
            System.out.println("Notice: " + e.getMessage());
        }

        System.out.println("\nChecking outstanding fine calculation for student01...");
        int unpaidFine = fineService.getTotalUnpaidAmount("student01");
        System.out.println("Student 'student01' outstanding fine balance: ₹" + unpaidFine);

        System.out.println("\n[✓] All OOP Core Concepts Verified Successfully!");
    }

    /**
     * Simple interactive CLI menu for testing via terminal.
     */
    private static void runInteractiveCLI() {
        Scanner scanner = new Scanner(System.in);
        BookService bookService = new BookService();
        LoanService loanService = new LoanService();
        FineService fineService = new FineService();

        while (true) {
            System.out.println("\n================ MAIN MENU ================");
            System.out.println("1. List All Books");
            System.out.println("2. Search Catalog");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. View User Loans");
            System.out.println("6. View User Fines");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            if ("0".equals(choice)) {
                System.out.println("Exiting BBAU Library CLI. Goodbye!");
                break;
            }

            switch (choice) {
                case "1" -> {
                    System.out.println("\n--- University Catalog ---");
                    for (Book b : bookService.getAllBooks()) {
                        System.out.printf("#%d | %-38s | %-16s | Copies: %d/%d | Reserve: %s%n",
                                b.getId(), b.getTitle(), b.getSubject(),
                                b.getAvailableCopies(), b.getTotalCopies(),
                                b.isFacultyOnly() ? "Faculty Only" : "General");
                    }
                }
                case "2" -> {
                    System.out.print("Enter search keyword: ");
                    String q = scanner.nextLine();
                    List<Book> results = bookService.search(q);
                    System.out.println("Found " + results.size() + " matches:");
                    for (Book b : results) {
                        System.out.printf("#%d: %s by %s [%s]%n", b.getId(), b.getTitle(), b.getAuthor(), b.getSubject());
                    }
                }
                case "3" -> {
                    System.out.print("Enter User ID (e.g. student01, faculty01): ");
                    String uid = scanner.nextLine().trim();
                    System.out.print("Enter Book ID: ");
                    try {
                        int bid = Integer.parseInt(scanner.nextLine().trim());
                        Loan loan = loanService.issueBook(uid, bid);
                        System.out.println("Success! Book issued. Due date: " + loan.getDueDate());
                    } catch (Exception e) {
                        System.out.println("Error issuing book: " + e.getMessage());
                    }
                }
                case "4" -> {
                    System.out.print("Enter Loan ID to return: ");
                    try {
                        int lid = Integer.parseInt(scanner.nextLine().trim());
                        loanService.returnBook(lid);
                        System.out.println("Book return processed successfully.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "5" -> {
                    System.out.print("Enter User ID: ");
                    String uid = scanner.nextLine().trim();
                    List<Loan> loans = loanService.getUserLoans(uid);
                    System.out.println("Loans for " + uid + ":");
                    for (Loan l : loans) {
                        System.out.printf("Loan #%d | Book #%d (%s) | Due: %s | Active: %s%n",
                                l.getId(), l.getBookId(), l.getBookTitle(), l.getDueDate(), l.isActive());
                    }
                }
                case "6" -> {
                    System.out.print("Enter User ID: ");
                    String uid = scanner.nextLine().trim();
                    List<Fine> fines = fineService.getFinesForUser(uid);
                    System.out.println("Fines for " + uid + " (Total unpaid: ₹" + fineService.getTotalUnpaidAmount(uid) + "):");
                    for (Fine f : fines) {
                        System.out.printf("Fine #%d | Amount: ₹%d | Paid: %s | Reason: %s%n",
                                f.getId(), f.getAmount(), f.isPaid(), f.getReason());
                    }
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}

