package com.bbau.library.servlet;

import com.bbau.library.model.*;
import com.bbau.library.service.BookService;
import com.bbau.library.service.FineService;
import com.bbau.library.service.LoanService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Controller servlet handling state-mutating actions (borrowing, returns, payments, admin CRUD).
 */
@WebServlet(name = "LibraryActionServlet", urlPatterns = {"/action"})
public class LibraryActionServlet extends HttpServlet {

    private final BookService bookService = new BookService();
    private final LoanService loanService = new LoanService();
    private final FineService fineService = new FineService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processAction(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processAction(request, response);
    }

    private void processAction(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        String action = request.getParameter("action");

        // Handle logout
        if ("logout".equalsIgnoreCase(action)) {
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Verify active session for all other mutations
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String redirectTab = "catalog";

        try {
            if ("borrow".equalsIgnoreCase(action)) {
                int bookId = Integer.parseInt(request.getParameter("bookId"));
                Loan loan = loanService.issueBook(currentUser.getId(), bookId);
                session.setAttribute("flashMessage", "Success! '" + loan.getBookTitle() +
                        "' issued. Due return date is " + loan.getDueDate() + ".");
                session.setAttribute("flashType", "success");
                redirectTab = "loans";

            } else if ("return".equalsIgnoreCase(action)) {
                int loanId = Integer.parseInt(request.getParameter("loanId"));
                Loan loan = loanService.returnBook(loanId);
                session.setAttribute("flashMessage", "Book return processed successfully.");
                session.setAttribute("flashType", "success");
                redirectTab = "loans";

            } else if ("payFine".equalsIgnoreCase(action)) {
                int fineId = Integer.parseInt(request.getParameter("fineId"));
                fineService.payFine(fineId);
                session.setAttribute("flashMessage", "Fine payment recorded successfully. Receipt simulated.");
                session.setAttribute("flashType", "success");
                redirectTab = "fines";

            } else if ("waiveFine".equalsIgnoreCase(action)) {
                int fineId = Integer.parseInt(request.getParameter("fineId"));
                fineService.waiveFine(fineId, currentUser);
                session.setAttribute("flashMessage", "Fine successfully waived by admin.");
                session.setAttribute("flashType", "success");
                redirectTab = "admin";

            } else if ("addBook".equalsIgnoreCase(action)) {
                String title = request.getParameter("title");
                String author = request.getParameter("author");
                String subject = request.getParameter("subject");
                String isbn = request.getParameter("isbn");
                int copies = Integer.parseInt(request.getParameter("copies"));
                boolean facultyOnly = Boolean.parseBoolean(request.getParameter("facultyOnly"));
                String description = request.getParameter("description");

                Book newBook = new Book(0, title, author, subject, isbn, copies, copies, facultyOnly, description);
                bookService.addBook(newBook, currentUser);
                session.setAttribute("flashMessage", "New book '" + title + "' added to catalog successfully.");
                session.setAttribute("flashType", "success");
                redirectTab = "catalog";

            } else if ("deleteBook".equalsIgnoreCase(action)) {
                int bookId = Integer.parseInt(request.getParameter("bookId"));
                bookService.deleteBook(bookId, currentUser);
                session.setAttribute("flashMessage", "Book record removed from library inventory.");
                session.setAttribute("flashType", "success");
                redirectTab = "catalog";
            }
        } catch (SecurityException e) {
            session.setAttribute("flashMessage", e.getMessage());
            session.setAttribute("flashType", "danger");
        } catch (IllegalStateException | IllegalArgumentException e) {
            session.setAttribute("flashMessage", e.getMessage());
            session.setAttribute("flashType", "warning");
        } catch (Exception e) {
            session.setAttribute("flashMessage", "Error processing request: " + e.getMessage());
            session.setAttribute("flashType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/dashboard?tab=" + redirectTab);
    }
}

