<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BBAU Central Library - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <style>
        .banner-text-box {
            background: linear-gradient(135deg, #811515, #5f0d0d);
            color: #fff;
            padding: 30px 20px;
            text-align: center;
        }
        .banner-text-box h1 {
            margin: 0;
            font-size: 32px;
            letter-spacing: 1.5px;
            font-family: Georgia, serif;
        }
        .banner-text-box p {
            margin: 6px 0 0;
            opacity: 0.9;
            font-size: 14px;
            letter-spacing: 2px;
            text-transform: uppercase;
        }
        .flash-box {
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 18px;
            font-size: 14px;
        }
        .flash-danger {
            background-color: #fde8e8;
            color: #b00020;
            border: 1px solid #f8b4b4;
        }
        .flash-success {
            background-color: #def7ec;
            color: #03543f;
            border: 1px solid #bcf0da;
        }
        .demo-pill {
            display: inline-block;
            font-size: 11px;
            background: #eee;
            padding: 2px 6px;
            border-radius: 4px;
            margin-top: 4px;
            color: #555;
        }
        .signup-toggle-section {
            margin-top: 15px;
        }
    </style>
</head>
<body>

    <header class="university-header">
        <div class="banner-text-box">
            <h1>Babasaheb Bhimrao Ambedkar University</h1>
            <p>Central University, Lucknow • Gautam Buddha Central Library</p>
        </div>
    </header>

    <main class="welcome-section">
        <p class="eyebrow">WELCOME TO</p>
        <h1>BBAU Central Library</h1>
        <h2>Library Management System</h2>
        <p class="welcome-description">
            A comprehensive, Object-Oriented Library Management System built with Java backend architecture,
            Jakarta Servlets, and role-based polymorphic lending policies.
        </p>

        <!-- Role Demonstration Cards -->
        <div class="login-options">
            <div class="login-card" onclick="fillCredentials('student01', 'stu123')">
                <span class="card-icon">&#127891;</span>
                <strong>Student Portal</strong>
                <span>Max 3 books &bull; 14 days &bull; &#8377;5/day fine</span>
                <span class="demo-pill">Click to auto-fill (student01)</span>
            </div>

            <div class="login-card" onclick="fillCredentials('faculty01', 'fac123')">
                <span class="card-icon">&#128104;&#8205;&#127979;</span>
                <strong>Faculty Portal</strong>
                <span>Max 10 books &bull; 30 days &bull; Reserve Access</span>
                <span class="demo-pill">Click to auto-fill (faculty01)</span>
            </div>

            <div class="login-card" onclick="fillCredentials('admin', 'admin123')">
                <span class="card-icon">&#128220;</span>
                <strong>Admin Console</strong>
                <span>Catalog Management &bull; System Ledgers &bull; Auditing</span>
                <span class="demo-pill">Click to auto-fill (admin)</span>
            </div>
        </div>

        <!-- Login Form Panel -->
        <div class="panel login-panel">
            <h2>Account Sign In</h2>

            <% if (request.getAttribute("error") != null) { %>
                <div class="flash-box flash-danger">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <% if (request.getAttribute("signupError") != null) { %>
                <div class="flash-box flash-danger">
                    <%= request.getAttribute("signupError") %>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
                <label for="userId">University ID / Username</label>
                <input type="text" id="userId" name="userId" required
                       value="<%= request.getAttribute("enteredUserId") != null ? request.getAttribute("enteredUserId") : "" %>"
                       placeholder="e.g. student01, faculty01, admin">

                <label for="password">Password</label>
                <input type="password" id="password" name="password" required placeholder="Enter password">

                <button type="submit" class="primary-button">Log In to Library</button>
            </form>

            <div class="signup-prompt">
                <span>New student or faculty member?</span>
                <button type="button" class="text-button" onclick="toggleSignupForm()">Create an Account</button>
            </div>

            <!-- Self-registration form -->
            <div id="signupFormSection" class="signup-toggle-section" style="display:none; border-top: 1px dashed var(--border); padding-top: 15px; margin-top: 15px;">
                <h3>New Patron Registration</h3>
                <form action="${pageContext.request.contextPath}/signup" method="post">
                    <label for="regRole">Role</label>
                    <select id="regRole" name="role" style="width:100%; padding:10px; border-radius:5px; border:1px solid #d5d0ca; margin-bottom:10px;">
                        <option value="student">Student (3 books, 14 days)</option>
                        <option value="faculty">Faculty Member (10 books, 30 days)</option>
                    </select>

                    <label for="regUserId">Desired User ID</label>
                    <input type="text" id="regUserId" name="userId" required placeholder="e.g. stu2026_01">

                    <label for="regName">Full Name</label>
                    <input type="text" id="regName" name="name" required placeholder="e.g. Rahul Sharma">

                    <label for="regDept">Department</label>
                    <input type="text" id="regDept" name="department" placeholder="e.g. Computer Science">

                    <label for="regPassword">Password</label>
                    <input type="password" id="regPassword" name="password" required placeholder="Choose a secure password">

                    <button type="submit" class="primary-button" style="background:#2b6cb0;">Register &amp; Enter</button>
                </form>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2026 Babasaheb Bhimrao Ambedkar University, Lucknow. All rights reserved.</p>
        <p><small>Java OOP Backend &bull; Jakarta Servlets &bull; Encapsulation, Inheritance, Polymorphism &amp; Abstraction</small></p>
    </footer>

    <script>
        function fillCredentials(user, pass) {
            document.getElementById('userId').value = user;
            document.getElementById('password').value = pass;
            document.getElementById('userId').focus();
        }

        function toggleSignupForm() {
            var el = document.getElementById('signupFormSection');
            el.style.display = (el.style.display === 'none' || el.style.display === '') ? 'block' : 'none';
        }
    </script>
</body>
</html>

