# BBAU Library Management System (Java OOP)

A Maven WAR starter project using Java 17, Jakarta Servlets, JSP, HTML/CSS, and Apache Tomcat 10.1.

## Run
1. Install JDK 17+, Maven, and Apache Tomcat 10.1.
2. From this folder run: `mvn clean package`
3. Copy `backend/target/bbau-library.war` to Tomcat's `webapps/` folder.
4. Start Tomcat and open `http://localhost:8080/bbau-library/`.
5. Demo login: `admin / admin123`, `student01 / stu123`, `faculty01 / fac123`.

## Important
This starter stores demo records in memory, so data resets when the server restarts. Do not use demo passwords or this in-memory store for a real deployment. The `database/schema.sql`, `DBConnection.java`, and DAO package are included as the next step for a persistent MySQL implementation. No real payments are processed; fine payment is simulated.

## OOP concepts
- Encapsulation: private fields and getters/setters in model classes.
- Inheritance: `Student` and `Faculty` extend `User`.
- Polymorphism: `User.getRole()` is overridden by subclasses.
- Abstraction: service interfaces and DAO interfaces.
- Composition: `Loan` refers to a `Book` and a `User`.
- Separation of concerns: servlet (HTTP), service (rules), model (entities), DAO (persistence boundary).
