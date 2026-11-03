# Pull Request: Complete Java OOP Backend, Watermelon UI Frontend & Beginner's Guide

- **Source Branch:** `feature/oop-backend-and-watermelon-ui`
- **Target Branch:** `main`

---

## 📌 Summary of Changes

This Pull Request provides a full rewrite of the Library Management System (LMS) into a structured **Java Backend** implementing core **Object-Oriented Programming (OOP)** principles, accompanied by an interactive **Watermelon UI** frontend and a dedicated **Beginner's Guide**.

---

## 🏛️ Key Implementation Highlights

### 1. Java OOP Backend Architecture
- **Abstraction**:
  - `User` abstract base class defining polymorphic contracts: `getMaxBooksAllowed()`, `getMaxLoanDays()`, `getDailyFineRate()`, `canBorrow(Book)`.
  - DAO interfaces (`UserDAO`, `BookDAO`, `LoanDAO`, `FineDAO`) isolating business logic from storage implementations.
- **Encapsulation**:
  - Private entity fields with validated accessors/mutators.
  - Domain invariants: `Book.borrowCopy()` and `Book.returnCopy()` guard against inventory over/under-allocation.
  - `Loan.isOverdue()` and `Loan.getOverdueDays()` manage temporal business logic internally.
- **Inheritance**:
  - `Student`, `Faculty`, and `Admin` subclasses extend `User`, inheriting base identity and credentials while introducing role-specific attributes (`rollNumber`, `designation`, `adminCode`).
- **Polymorphism**:
  - Dynamic method dispatch enforces role-specific loan limits (Student: 3, Faculty: 10, Admin: 50), loan durations (14 vs 30 vs 60 days), and fine rates (₹5/day vs ₹1/day vs ₹0).
  - Access clearance: `student.canBorrow(book)` blocks Faculty Reserve books, while `faculty.canBorrow(book)` grants access.
  - Method overloading in `BookService.search(...)` and `LoanService.issueBook(...)`.
- **Composition & Association**:
  - `Loan` associates `User` and `Book` entities with lending transaction lifecycle states.
  - `Fine` ledger records associated with user accounts.
- **Design Patterns**:
  - **Factory Pattern**: `UserFactory.createUser(...)` for polymorphic instantiation.
  - **Singleton Pattern**: Double-checked locking singletons for `LibraryStore` and `DBConnection`.
  - **DAO Pattern**: Clean persistence layer supporting in-memory store and live MySQL schema integration.

### 2. Dual Backend Execution Modes
- **Mode A (Standalone CLI / Runner)**: Direct execution via `com.bbau.library.Main` for instant OOP verification and interactive CLI testing without requiring a web server.
- **Mode B (Jakarta Servlet 6.0 Webapp)**: Complete deployment descriptor (`web.xml`), Servlets (`LoginServlet`, `SignupServlet`, `DashboardServlet`, `LibraryActionServlet`), and JSP views for Apache Tomcat 10.1+.

### 3. Watermelon UI Frontend
- Styled using the **Watermelon UI** design system on Tailwind CSS (rose, rind emerald, glassmorphism cards, Bento grid layout).
- Dynamic interactive sandbox with real-time role switching (**Student**, **Faculty**, **Admin**).
- Real-time catalog filtering, borrow validations, active loan tracking, and simulated fine payments.

### 4. Comprehensive Documentation & Testing
- **`BEGINNER_GUIDE.md`**: Illustrated step-by-step beginner guide with real-world analogies, codebase tour, and request lifecycle diagrams.
- **`OOPConceptsTest.java`**: Formal JUnit 5 test suite asserting inheritance, polymorphic dynamic dispatch, inventory encapsulation invariants, and composition.

---

## 🧪 Verification & Testing Performed
- [x] All 47 Java files verified for syntax, packaging, and brace balance.
- [x] Standalone verification engine tested via `Main.java`.
- [x] Watermelon UI frontend served and validated on `http://localhost:3000`.
- [x] Verified student vs faculty polymorphic borrow restrictions on faculty-reserve books.
- [x] Automated unit test suite prepared with JUnit 5 assertions.

---

## 📂 Changed Files
- `BEGINNER_GUIDE.md` (New beginner guide)
- `README.md` (Updated comprehensive documentation)
- `backend/database/schema.sql` (MySQL schema definition)
- `backend/pom.xml` (Jakarta EE 10 / Servlet 6 Maven POM)
- `backend/src/main/java/com/bbau/library/Main.java` (CLI & OOP runner)
- `backend/src/main/java/com/bbau/library/model/*` (`User`, `Student`, `Faculty`, `Admin`, `Book`, `Loan`, `Fine`, `UserFactory`, etc.)
- `backend/src/main/java/com/bbau/library/dao/*` (`UserDAO`, `BookDAO`, `LoanDAO`, `FineDAO` + implementations)
- `backend/src/main/java/com/bbau/library/service/*` (`AuthService`, `BookService`, `LoanService`, `FineService`, `LibraryStore`)
- `backend/src/main/java/com/bbau/library/servlet/*` (`LoginServlet`, `SignupServlet`, `DashboardServlet`, `LibraryActionServlet`, `EncodingFilter`)
- `backend/src/main/webapp/*` (`web.xml`, `index.jsp`, `dashboard.jsp`, `style.css`)
- `backend/src/test/java/com/bbau/library/OOPConceptsTest.java` (Automated tests)
- `frontend/pages/index.html` & `index.html` (Watermelon UI frontend)
- `server.js` (Lightweight Node server)

