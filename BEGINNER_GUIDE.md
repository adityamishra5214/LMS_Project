# 🎓 Beginner's Complete Guide to the LMS & Java OOP Backend

Welcome! If you are new to Java, Object-Oriented Programming (OOP), or backend development, this guide is designed for you. It explains how this entire project works step-by-step using simple real-world analogies, visual diagrams, and code snippets.

---

## 📚 Table of Contents
1. [What is this Project?](#1-what-is-this-project)
2. [The Real-World Library Analogy](#2-the-real-world-library-analogy)
3. [The 4 Core OOP Concepts Explained Simply](#3-the-4-core-oop-concepts-explained-simply)
   - [Encapsulation (The Protective Bubble)](#encapsulation-the-protective-bubble)
   - [Inheritance (The Family Tree)](#inheritance-the-family-tree)
   - [Polymorphism (One Question, Different Answers)](#polymorphism-one-question-different-answers)
   - [Abstraction (The Restaurant Menu)](#abstraction-the-restaurant-menu)
4. [Backend Codebase Tour (Folder by Folder)](#4-backend-codebase-tour-folder-by-folder)
5. [Step-by-Step: What Happens When a Student Borrows a Book?](#5-step-by-step-what-happens-when-a-student-borrows-a-book)
6. [Design Patterns Made Simple](#6-design-patterns-made-simple)
7. [Glossary of Common Terms](#7-glossary-of-common-terms)

---

## 1. What is this Project?

This project is a **Library Management System (LMS)** for a university (Babasaheb Bhimrao Ambedkar University - BBAU).

It manages:
- **Books**: Title, author, total copies, available copies, and whether a book is reserved for faculty only.
- **Users**: Students, Faculty members, and Library Administrators.
- **Loans**: Who borrowed which book, when it was issued, and when it is due back.
- **Fines**: Automatic late fees if a book is returned after its due date.

It includes:
1. A **Java Backend** that enforces all business rules using OOP.
2. A **Watermelon UI Frontend** built with Tailwind CSS that lets you interact with the system visually in your browser.

---

## 2. The Real-World Library Analogy

Think of how a real university library works:
- Anyone entering has an ID card (**User**).
- A student has different rules than a professor:
  - Students can only take **3 books** for **14 days**.
  - Professors can take **10 books** for **30 days** and can access rare **Faculty Reserve** books.
- The books on the shelves have a strict count (**Inventory**). If 3 copies exist and 3 are borrowed, nobody else can borrow one until a copy is returned.
- If you return a book late, the desk clerk charges a fine based on who you are.

In software, **Object-Oriented Programming (OOP)** is simply writing code that mirrors this real world!

---

## 3. The 4 Core OOP Concepts Explained Simply

```
               THE 4 PILLARS OF OBJECT-ORIENTED PROGRAMMING
 ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
 │  Encapsulation  │ │   Inheritance   │ │  Polymorphism   │ │   Abstraction   │
 │   "Data Safe"   │ │  "Code Reuse"   │ │ "Many Forms"    │ │ "Hide Details"  │
 └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘
```

---

### Encapsulation ("The Protective Bubble")
> **Analogy:** Think of an ATM. You cannot reach inside the machine and grab cash with your hands. You must insert your card, type your PIN, and use official buttons. The machine protects its internal money.

**In Our Code:**
In [`Book.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/model/Book.java), all variables are `private`. Outside code cannot accidentally write `book.availableCopies = -99;`.

Instead, the `Book` class protects its own data with methods:

```java
// Inside Book.java:
public synchronized void borrowCopy() {
    if (this.availableCopies <= 0) {
        throw new IllegalStateException("No available copies to borrow!");
    }
    this.availableCopies--; // Safely decrease by 1
}

public synchronized void returnCopy() {
    if (this.availableCopies >= this.totalCopies) {
        throw new IllegalStateException("Cannot return more copies than total inventory!");
    }
    this.availableCopies++; // Safely increase by 1
}
```
**Why this matters:** Nobody can corrupt the inventory count, even if there are bugs elsewhere.

---

### Inheritance ("The Family Tree")
> **Analogy:** A "Car" and a "Truck" are both types of "Vehicle". They both have wheels, an engine, and brakes. Instead of building those from scratch twice, they inherit from Vehicle.

**In Our Code:**
Every person in the library has an ID, a name, a password, and a role. We write this once in [`User.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/model/User.java):

```java
public abstract class User {
    private String id;
    private String name;
    private String passwordHash;
    // ...
}
```

Then `Student`, `Faculty`, and `Admin` simply **extend** `User`:

```java
public class Student extends User {
    private String rollNumber; // Only students have a roll number
}

public class Faculty extends User {
    private String designation; // Only faculty have a title like "Professor"
}
```

**Why this matters:** We don't duplicate common fields like name, ID, or password.

---

### Polymorphism ("One Question, Different Answers")
> **Analogy:** If you tell a Dog "Speak!", it barks. If you tell a Cat "Speak!", it meows. You gave the *same command* ("Speak!"), but each animal responds according to its nature.

**In Our Code:**
The abstract class [`User.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/model/User.java) asks four questions:
1. `getMaxBooksAllowed()`
2. `getMaxLoanDays()`
3. `getDailyFineRate()`
4. `canBorrow(Book book)`

Each subclass gives its own unique answer:

| Question | `Student` answers: | `Faculty` answers: | `Admin` answers: |
| :--- | :--- | :--- | :--- |
| **Max Books?** | `return 3;` | `return 10;` | `return 50;` |
| **Loan Days?** | `return 14;` | `return 30;` | `return 60;` |
| **Fine per day?**| `return 5.0;` (₹5) | `return 1.0;` (₹1) | `return 0.0;` (₹0) |
| **Can borrow Faculty Reserve?** | `return !book.isFacultyOnly();` (NO) | `return true;` (YES) | `return true;` (YES) |

Look at how elegant [`LoanService.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/service/LoanService.java) is because of polymorphism:
```java
// We don't need clumsy if-else checks like:
// if (user is Student) days = 14; else if (user is Faculty) days = 30;

// Instead, Java automatically calls the right version!
int loanDays = user.getMaxLoanDays(); 
boolean allowed = user.canBorrow(book);
```

---

### Abstraction ("The Restaurant Menu")
> **Analogy:** When you order food at a restaurant, you read the menu ("Pasta - $12"). You don't need to know which brand of stove the chef uses or how the gas pipes are connected. The kitchen details are hidden behind the menu.

**In Our Code:**
The Service layer only talks to **Interfaces** (menus), like [`BookDAO.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/dao/BookDAO.java):

```java
public interface BookDAO {
    Optional<Book> findById(int id);
    List<Book> findAll();
    boolean save(Book book);
}
```

The rest of the system doesn't care whether the books are stored in computer memory (`LibraryStore`) or in a MySQL database. If you change from MySQL to PostgreSQL tomorrow, you only change the DAO implementation—the rest of your application code remains untouched!

---

## 4. Backend Codebase Tour (Folder by Folder)

Here is a map of the `backend/src/main/java/com/bbau/library/` directory:

```
com/bbau/library/
│
├── Main.java                 <-- Standalone runner (run this to test OOP in console)
│
├── model/                    <-- ENTITIES: Plain Java objects holding data & rules
│   ├── User.java             <-- Abstract base class
│   ├── Student.java          <-- Inherits from User
│   ├── Faculty.java          <-- Inherits from User
│   ├── Admin.java            <-- Inherits from User
│   ├── Book.java             <-- Book data & copy inventory logic
│   ├── Loan.java             <-- Connects a User + Book + Due Date
│   ├── Fine.java             <-- Tracks late fees
│   └── UserFactory.java      <-- Creates the right User type cleanly
│
├── dao/                      <-- PERSISTENCE: Data Access Objects
│   ├── BookDAO.java          <-- Interface (the menu)
│   ├── BookDAOImpl.java      <-- Implementation (memory store + MySQL)
│   ├── UserDAO.java / UserDAOImpl.java
│   ├── LoanDAO.java / LoanDAOImpl.java
│   └── FineDAO.java / FineDAOImpl.java
│
├── service/                  <-- BUSINESS LOGIC: The "Brain" of the application
│   ├── AuthService.java      <-- Handles Login & Registration
│   ├── BookService.java      <-- Search and catalog rules
│   ├── LoanService.java      <-- Enforces borrowing limits & due dates
│   ├── FineService.java      <-- Calculates and clears fines
│   └── LibraryStore.java     <-- In-memory simulated database with demo data
│
├── servlet/                  <-- CONTROLLERS: Web endpoints that receive HTTP requests
│   ├── LoginServlet.java     <-- Handles /login
│   ├── SignupServlet.java    <-- Handles /signup
│   ├── DashboardServlet.java <-- Prepares data for dashboard.jsp
│   └── LibraryActionServlet.java <-- Handles borrow, return, pay fine
│
└── util/                     <-- HELPER UTILITIES
    ├── DBConnection.java     <-- Manages MySQL connections
    └── PasswordUtil.java     <-- Hashes passwords with SHA-256
```

---

## 5. Step-by-Step: What Happens When a Student Borrows a Book?

Here is the exact journey through the codebase when someone clicks **"Borrow"**:

```
[ Browser / UI ]
       │
       ▼  1. Submits POST request: action=borrow&bookId=105
[ LibraryActionServlet.java ]
       │
       ▼  2. Calls loanService.issueBook(currentUser, book)
[ LoanService.java ]
       │
       ├──► 3. Checks book.isAvailable()
       │       If 0 copies left ──► Throws "No copies available"
       │
       ├──► 4. Checks user.canBorrow(book)  <-- POLYMORPHIC CHECK!
       │       If Student tries to borrow Faculty Reserve ──► Throws "Access Restricted!"
       │
       ├──► 5. Checks user.hasReachedBorrowLimit(activeLoansCount)
       │       If Student already has 3 books ──► Throws "Loan limit reached!"
       │
       ├──► 6. Calls book.borrowCopy()  <-- ENCAPSULATION!
       │       Decrements copies from 4 to 3.
       │
       ▼  7. Creates new Loan(user, book, user.getMaxLoanDays())
[ LoanDAO.java / LibraryStore ]
       │
       ▼  8. Saves the loan record
[ Browser / UI ]
       ◄── 9. Displays: "Success! Book issued. Due in 14 days."
```

---

## 6. Design Patterns Made Simple

We use three famous software design patterns in this project:

### 1. The Factory Pattern ([`UserFactory.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/model/UserFactory.java))
Instead of writing `new Student(...)` or `new Faculty(...)` everywhere, we ask the factory:
```java
User user = UserFactory.createUser("student", id, name, password);
```
The factory inspects the role string and constructs the right subclass for you.

### 2. The Singleton Pattern ([`LibraryStore.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/service/LibraryStore.java) & [`DBConnection.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/util/DBConnection.java))
We only want **one single copy** of the library data in memory, not a new one created on every web click.
```java
// Always returns the exact same shared instance:
LibraryStore store = LibraryStore.getInstance();
```

### 3. The DAO (Data Access Object) Pattern
Separates *how data is stored* (SQL tables or memory arrays) from *business rules* (who is allowed to borrow).

---

## 7. Glossary of Common Terms

- **POJO (Plain Old Java Object)**: A basic Java class holding data with private fields, getters, and setters (like `Book.java`).
- **Encapsulation**: Keeping fields private and providing controlled methods to change them.
- **Inheritance (`extends`)**: A child class receiving variables and methods from a parent class.
- **Polymorphism**: Calling the same method name on different objects and getting behaviors customized for each.
- **Abstract Class**: A parent class that cannot be created on its own (`new User()` is forbidden); you must create a concrete child like `new Student()`.
- **Interface**: A contract that lists method names without writing their code.
- **Servlet**: A Java class that handles incoming web requests (`doGet`, `doPost`) and returns HTML or redirects.
- **JSP (JavaServer Pages)**: HTML pages with embedded Java tags used to render dynamic server data.

---

## 💡 Quick Tips for Exploring the Code

1. Open [`backend/src/main/java/com/bbau/library/Main.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/main/java/com/bbau/library/Main.java) to see how the classes interact in pure Java without web servers.
2. Open [`backend/src/test/java/com/bbau/library/OOPConceptsTest.java`](file:///c:/Users/adity/Desktop/LMS_Project/backend/src/test/java/com/bbau/library/OOPConceptsTest.java) to see automated unit tests that prove each OOP concept works.
3. Open [`index.html`](file:///c:/Users/adity/Desktop/LMS_Project/index.html) in your browser to try out the interactive visual UI.

