package com.bbau.library.servlet;

import com.bbau.library.model.*;
import com.bbau.library.service.AuthService;
import com.bbau.library.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Controller servlet handling user account self-registration.
 */
@WebServlet(name = "SignupServlet", urlPatterns = {"/signup"})
public class SignupServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/#signup");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String roleStr = request.getParameter("role");
        String userId = request.getParameter("userId");
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String department = request.getParameter("department");

        if (userId == null || userId.isBlank() || password == null || password.isBlank() || name == null || name.isBlank()) {
            request.setAttribute("signupError", "All required fields (User ID, Name, Password) must be filled.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        UserRole role = UserRole.fromString(roleStr);
        String hashed = PasswordUtil.hash(password);
        User newUser = UserFactory.createUser(role, userId.trim(), name.trim(), hashed);

        if (newUser instanceof Student student) {
            student.setDepartment(department != null && !department.isBlank() ? department.trim() : "General");
            student.setRollNumber(userId.trim());
        } else if (newUser instanceof Faculty faculty) {
            faculty.setDepartment(department != null && !department.isBlank() ? department.trim() : "Faculty Dept");
            faculty.setDesignation("Faculty Member");
        }

        boolean registered = authService.register(newUser);
        if (registered) {
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", newUser);
            session.setAttribute("flashMessage", "Registration successful! Welcome to BBAU Library, " + newUser.getName() + ".");
            session.setAttribute("flashType", "success");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("signupError", "User ID '" + userId + "' is already registered. Please choose another or login.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}

