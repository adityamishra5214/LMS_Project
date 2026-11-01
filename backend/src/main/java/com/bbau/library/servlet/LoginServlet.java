package com.bbau.library.servlet;

import com.bbau.library.model.User;
import com.bbau.library.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller servlet handling patron and staff authentication.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", ""})
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        Optional<User> userOpt = authService.authenticate(userId, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setAttribute("flashMessage", "Welcome back, " + user.getName() + "!");
            session.setAttribute("flashType", "success");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Invalid user ID or password. Please try again.");
            request.setAttribute("enteredUserId", userId);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}

