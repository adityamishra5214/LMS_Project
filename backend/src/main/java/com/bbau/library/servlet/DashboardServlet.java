package com.bbau.library.servlet;

import com.bbau.library.dao.UserDAO;
import com.bbau.library.model.*;
import com.bbau.library.service.BookService;
import com.bbau.library.service.FineService;
import com.bbau.library.service.LoanService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet for patron and staff dashboard views.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final BookService bookService = new BookService();
    private final LoanService loanService = new LoanService();
    private final FineService fineService = new FineService();
    private final UserDAO userDAO = UserDAO.getDefault();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");

        // Flash message handling
        String flashMessage = (String) session.getAttribute("flashMessage");
        String flashType = (String) session.getAttribute("flashType");
        if (flashMessage != null) {
            request.setAttribute("flashMessage", flashMessage);
            request.setAttribute("flashType", flashType != null ? flashType : "info");
            session.removeAttribute("flashMessage");
            session.removeAttribute("flashType");
        }

        // Active tab
        String tab = request.getParameter("tab");
        if (tab == null || tab.isBlank()) {
            tab = "catalog";
        }
        request.setAttribute("activeTab", tab);

        // Books catalog & search
        String searchQuery = request.getParameter("q");
        List<Book> books;
        if (searchQuery != null && !searchQuery.isBlank()) {
            books = bookService.search(searchQuery);
            request.setAttribute("searchQuery", searchQuery);
        } else {
            books = bookService.getAllBooks();
        }
        request.setAttribute("books", books);

        // Current user specific records
        List<Loan> activeLoans = loanService.getActiveLoans(user.getId());
        List<Loan> allUserLoans = loanService.getUserLoans(user.getId());
        List<Fine> userFines = fineService.getFinesForUser(user.getId());
        int totalUnpaidFine = fineService.getTotalUnpaidAmount(user.getId());

        request.setAttribute("activeLoans", activeLoans);
        request.setAttribute("allUserLoans", allUserLoans);
        request.setAttribute("userFines", userFines);
        request.setAttribute("totalUnpaidFine", totalUnpaidFine);

        // Admin-only metrics and datasets
        if (user.isAdmin()) {
            List<User> allUsers = userDAO.findAll();
            List<Loan> allLoans = loanService.getAllLoans();
            List<Fine> allFines = fineService.getAllFines();

            request.setAttribute("allUsers", allUsers);
            request.setAttribute("allLoans", allLoans);
            request.setAttribute("allFines", allFines);

            long totalActiveLoans = allLoans.stream().filter(Loan::isActive).count();
            int totalFineAmount = allFines.stream().mapToInt(Fine::getAmount).sum();

            request.setAttribute("totalBooksCount", books.size());
            request.setAttribute("totalUsersCount", allUsers.size());
            request.setAttribute("totalActiveLoansCount", totalActiveLoans);
            request.setAttribute("totalFineAmount", totalFineAmount);
        }

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

