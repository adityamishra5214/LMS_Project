# BBAU Library Management System (Java OOP Backend)

A complete university Library Management System (LMS) backend rewritten in Java following rigorous **Object-Oriented Programming (OOP)** principles and SOLID design patterns.

> 💡 **New to Java or Object-Oriented Programming?**  
> Check out the dedicated [**Beginner's Guide (BEGINNER_GUIDE.md)**](file:///c:/Users/adity/Desktop/LMS_Project/BEGINNER_GUIDE.md) for plain-English analogies, flowcharts, and a guided tour of the backend!

The application supports both:
1. **Interactive Standalone CLI & OOP Test Runner** (`com.bbau.library.Main`) for instant execution and evaluation.
2. **Jakarta Servlet 6.0 Web Application** deployable to Apache Tomcat 10.1+.

---

## 🏛️ Project Architecture & OOP Concepts

This backend is designed from the ground up to showcase the four pillars of OOP and enterprise software design patterns:

### 1. Abstraction
- **Abstract Base Classes**: `User` acts as an abstract base contract declaring abstract methods such as `getMaxBooksAllowed()`, `getMaxLoanDays()`, `getDailyFineRate()`, and `canBorrow(Book book)`.
- **Interface Segregation & DAO Pattern**: `UserDAO`, `BookDAO`, `LoanDAO`, and `FineDAO` define strict persistence contracts, completely isolating business logic from storage mechanisms.

### 2. Encapsulation
- **Data Hiding**: All entity attributes (`Book`, `User`, `Loan`, `Fine`, `Review`, `Notification`) are declared `private`.
- **Domain Invariants**: Business validation is enforced directly within entities:
  - `Book.borrowCopy()` and `Book.returnCopy()` guard available vs. total inventory counts.
  - Setters validate non-empty names, non-negative amounts, and valid date intervals.
  - `Loan.isOverdue()` and `Loan.getOverdueDays()` manage temporal logic internally.

### 3. Inheritance
- `Student`, `Faculty`, and `Admin` inherit shared fields (`id`, `name`, `passwordHash`, `role`, `createdAt`) from the abstract `User` class.
- Specialized fields are added to subclasses:
  - `Student`: `department`, `rollNumber`.
  - `Faculty`: `department`, `designation`.
  - `Admin`: `adminCode`, `superAdmin`.

### 4. Polymorphism
- **Dynamic Method Dispatch (Runtime Polymorphism)**:
  - **Borrowing Quotas**: `Student` can borrow up to 3 books; `Faculty` up to 10 books; `Admin` up to 50 books.
  - **Loan Windows**: `Student` gets 14 days; `Faculty` gets 30 days; `Admin` gets 60 days.
  - **Fine Calculations**: `Student` incurs ₹5/day; `Faculty` incurs ₹1/day (subsidized); `Admin` incurs ₹0/day.
  - **Access Clearances**: `student.canBorrow(book)` rejects books flagged as `facultyOnly = true`; `faculty.canBorrow(book)` allows access to all materials.
- **Method Overloading (Compile-Time Polymorphism)**:
  - `BookService.search(keyword)` vs `BookService.search(title, author, subject)`.
  - `LoanService.issueBook(userId, bookId)` vs `LoanService.issueBook(user, book)` vs `LoanService.issueBook(user, book, days)`.
  - `UserFactory.createUser(role, id, name, hash)` with enum and String variants.

### 5. Composition & Association
- `Loan` is composed of references to `User` and `Book`, combining entity relationships with transaction state (`issueDate`, `dueDate`, `returnDate`).
- `Fine` associates borrower identity with overdue books and payment tracking.
- `LibraryStore` coordinates thread-safe collections (`ConcurrentHashMap`) representing the entire library repository.

### 6. Design Patterns
- **Factory Pattern**: `UserFactory` encapsulates the polymorphic instantiation of `Student`, `Faculty`, and `Admin`.
- **Singleton Pattern**: `LibraryStore.getInstance()` and `DBConnection.getInstance()` manage singleton lifecycles with double-checked locking.
- **DAO Pattern**: Concrete persistence implementations (`UserDAOImpl`, `BookDAOImpl`, `LoanDAOImpl`, `FineDAOImpl`) support both in-memory store and live MySQL schema integration.

---

## 📂 Project Structure

```
LMS_Project/
├── pom.xml                                   # Root Maven parent POM
├── style.css                                 # University shared stylesheet
├── schema.sql                                # MySQL relational schema
├── README.md                                 # Project documentation
├── backend/
│   ├── pom.xml                               # Backend Maven POM (Jakarta Servlet 6, JSTL, MySQL, JUnit 5)
│   ├── database/
│   │   └── schema.sql                        # Relational database definition
│   └── src/
│       ├── main/
│       │   ├── java/com/bbau/library/
│       │   │   ├── Main.java                 # Standalone CLI & OOP Test Runner
│       │   │   ├── model/
│       │   │   │   ├── UserRole.java         # Role enum (STUDENT, FACULTY, ADMIN)
│       │   │   │   ├── User.java             # Abstract base class
│       │   │   │   ├── Student.java          # Student subclass (Inheritance & Polymorphism)
│       │   │   │   ├── Faculty.java          # Faculty subclass (Inheritance & Polymorphism)
│       │   │   │   ├── Admin.java            # Admin subclass (Inheritance & Polymorphism)
│       │   │   │   ├── UserFactory.java      # Factory Pattern for User subtypes
│       │   │   │   ├── Book.java             # Encapsulated Book domain entity
│       │   │   │   ├── Loan.java             # Composition of User & Book
│       │   │   │   ├── Fine.java             # Fine ledger entity
│       │   │   │   ├── Review.java           # Review entity
│       │   │   │   └── Notification.java     # User notification entity
│       │   │   ├── dao/
│       │   │   │   ├── UserDAO.java          # DAO Interface (Abstraction)
│       │   │   │   ├── UserDAOImpl.java      # DAO Implementation
│       │   │   │   ├── BookDAO.java          # Book DAO Interface
│       │   │   │   ├── BookDAOImpl.java      # Book DAO Implementation
│       │   │   │   ├── LoanDAO.java          # Loan DAO Interface
│       │   │   │   ├── LoanDAOImpl.java      # Loan DAO Implementation
│       │   │   │   ├── FineDAO.java          # Fine DAO Interface
│       │   │   │   └── FineDAOImpl.java      # Fine DAO Implementation
│       │   │   ├── service/
│       │   │   │   ├── LibraryStore.java     # Thread-safe Singleton in-memory store
│       │   │   │   ├── AuthService.java      # Authentication & Registration
│       │   │   │   ├── BookService.java      # Catalog & Search operations
│       │   │   │   ├── LoanService.java      # Lending rules & Dynamic dispatch
│       │   │   │   └── FineService.java      # Ledgering & Fine management
│       │   │   ├── servlet/
│       │   │   │   ├── LoginServlet.java     # User authentication handler
│       │   │   │   ├── SignupServlet.java    # Registration handler
│       │   │   │   ├── DashboardServlet.java # Patron & Admin dashboard
│       │   │   │   ├── LibraryActionServlet.java # Borrowing, returns, payments
│       │   │   │   └── EncodingFilter.java   # UTF-8 filter
│       │   │   └── util/
│       │   │       ├── DBConnection.java     # Singleton JDBC connector
│       │   │       └── PasswordUtil.java     # SHA-256 hashing utility
│       │   └── webapp/
│       │       ├── WEB-INF/
│       │       │   ├── web.xml               # Jakarta 6.0 deployment descriptor
│       │       │   └── views/
│       │       │       └── dashboard.jsp     # Responsive dashboard view
│       │       ├── assets/
│       │       │   └── style.css             # Stylesheet
│       │       └── index.jsp                 # Landing page & login modal
│       └── test/java/com/bbau/library/
│           └── OOPConceptsTest.java          # Automated JUnit 5 tests
└── frontend/                                 # Static preview mockups
    ├── README.md
    ├── pages/index.html
    └── assets/style.css
```

---

## 🚀 How to Run

### Method 1: Standalone CLI / OOP Verification
Run the `Main.java` class directly in any IDE (IntelliJ, Eclipse, VS Code) or compile and run with standard Java:

```bash
# Automated OOP demonstration
java -cp backend/target/classes com.bbau.library.Main

# Interactive CLI mode
java -cp backend/target/classes com.bbau.library.Main --cli
```

### Method 2: Build WAR & Deploy to Apache Tomcat
1. Build the project using Maven:
   ```bash
   mvn clean package
   ```
2. Copy `backend/target/bbau-library.war` into Tomcat's `webapps/` folder.
3. Start Tomcat and visit:
   `http://localhost:8080/bbau-library/`

---

## 🔑 Pre-seeded Demo Credentials

| Role | Username | Password | Quota Limit | Loan Duration | Fine / Day |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | 50 books | 60 days | ₹0.00 |
| **Student** | `student01` | `stu123` | 3 books | 14 days | ₹5.00 |
| **Faculty** | `faculty01` | `fac123` | 10 books | 30 days | ₹1.00 |

---

## 🧪 Testing OOP Implementation

Execute the automated test suite with Maven:
```bash
mvn test
```
The tests formally assert:
- `testInheritance`: Validates class hierarchy and base attribute inheritance.
- `testPolymorphicLimits`: Validates dynamic method dispatch of quotas and fine rates.
- `testPolymorphicBorrowingClearance`: Asserts students are restricted from faculty reserves while faculty have clearance.
- `testEncapsulationAndInvariants`: Asserts available vs total inventory counters cannot be corrupted.
- `testComposition`: Validates `Loan` entity associations.
