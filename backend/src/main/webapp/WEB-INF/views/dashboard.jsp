<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bbau.library.model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String activeTab = (String) request.getAttribute("activeTab");
    if (activeTab == null) activeTab = "catalog";

    String flashMessage = (String) request.getAttribute("flashMessage");
    String flashType = (String) request.getAttribute("flashType");

    @SuppressWarnings("unchecked")
    List<Book> books = (List<Book>) request.getAttribute("books");

    @SuppressWarnings("unchecked")
    List<Loan> activeLoans = (List<Loan>) request.getAttribute("activeLoans");

    @SuppressWarnings("unchecked")
    List<Loan> allUserLoans = (List<Loan>) request.getAttribute("allUserLoans");

    @SuppressWarnings("unchecked")
    List<Fine> userFines = (List<Fine>) request.getAttribute("userFines");

    Integer totalUnpaidFine = (Integer) request.getAttribute("totalUnpaidFine");
    if (totalUnpaidFine == null) totalUnpaidFine = 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BBAU Library - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <style>
        .flash-alert {
            padding: 14px 18px;
            border-radius: 6px;
            margin-bottom: 22px;
            font-size: 15px;
            font-weight: 500;
        }
        .flash-alert-success { background: #def7ec; color: #03543f; border: 1px solid #bcf0da; }
        .flash-alert-warning { background: #fef08a; color: #854d0e; border: 1px solid #fde047; }
        .flash-alert-danger { background: #fde8e8; color: #9b1c1c; border: 1px solid #f8b4b4; }

        .stat-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-bottom: 25px;
        }
        .stat-card {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            border: 1px solid var(--border);
            border-left: 5px solid var(--maroon);
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
        }
        .stat-card h3 { margin: 0; font-size: 26px; color: var(--maroon); }
        .stat-card p { margin: 5px 0 0; color: #666; font-size: 13px; text-transform: uppercase; letter-spacing: 1px; }

        .tag {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
        }
        .tag-faculty { background: #fee2e2; color: #991b1b; }
        .tag-general { background: #e0f2fe; color: #075985; }
        .tag-available { background: #dcfce7; color: #166534; }
        .tag-unavailable { background: #f3f4f6; color: #6b7280; }
        .tag-overdue { background: #ef4444; color: #fff; }

        .btn-action {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 13px;
            font-weight: bold;
            cursor: pointer;
            border: none;
        }
        .btn-primary { background: var(--maroon); color: #fff; }
        .btn-primary:hover { background: var(--maroon-dark); }
        .btn-success { background: #16a34a; color: #fff; }
        .btn-success:hover { background: #15803d; }
        .btn-danger { background: #dc2626; color: #fff; }
        .btn-danger:hover { background: #b91c1c; }
        .btn-disabled { background: #e5e7eb; color: #9ca3af; cursor: not-allowed; }

        .search-bar-form {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }
        .search-bar-form input {
            flex: 1;
            padding: 10px 14px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 14px;
        }
    </style>
</head>
<body>

    <div class="app-page">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
            <div class="sidebar-brand">
                BBAU Library
                <span>CENTRAL SYSTEM</span>
            </div>

            <div style="font-size: 13px; color: #666; margin-top: 5px; border-bottom: 1px solid #eee; padding-bottom: 10px;">
                Logged as: <strong><%= currentUser.getName() %></strong><br>
                Type: <em><%= currentUser.getRole().getDisplayName() %></em><br>
                Limit: <strong><%= currentUser.getMaxBooksAllowed() %> Books / <%= currentUser.getMaxLoanDays() %> Days</strong>
            </div>

            <nav id="mainNav" style="margin-top: 15px;">
                <a href="${pageContext.request.contextPath}/dashboard?tab=catalog"
                   class="nav-button <%= "catalog".equals(activeTab) ? "active" : "" %>">
                    &#128218; Book Catalog
                </a>
                <a href="${pageContext.request.contextPath}/dashboard?tab=loans"
                   class="nav-button <%= "loans".equals(activeTab) ? "active" : "" %>">
                    &#128339; My Loans (<%= activeLoans != null ? activeLoans.size() : 0 %>)
                </a>
                <a href="${pageContext.request.contextPath}/dashboard?tab=fines"
                   class="nav-button <%= "fines".equals(activeTab) ? "active" : "" %>">
                    &#128179; Fines <%= totalUnpaidFine > 0 ? "(&#8377;" + totalUnpaidFine + ")" : "" %>
                </a>
                <% if (currentUser.isAdmin()) { %>
                    <a href="${pageContext.request.contextPath}/dashboard?tab=admin"
                       class="nav-button <%= "admin".equals(activeTab) ? "active" : "" %>">
                        &#9881; Admin Management
                    </a>
                <% } %>
            </nav>

            <form action="${pageContext.request.contextPath}/action" method="post" style="margin-top:auto;">
                <input type="hidden" name="action" value="logout">
                <button type="submit" class="logout-button" style="width:100%;">Sign Out</button>
            </form>
        </aside>

        <!-- Main Content Panel -->
        <main class="main-content">
            <div class="app-topbar">
                <div>
                    <h1>BBAU Central Library</h1>
                    <p style="margin: 4px 0 0; color: #666; font-size: 14px;">
                        OOP-Powered Lending Engine &bull; Polymorphic Rule Enforcement
                    </p>
                </div>
                <div class="role-badge">
                    <%= currentUser.getRole().getDisplayName() %>
                </div>
            </div>

            <!-- Flash Alert Messages -->
            <% if (flashMessage != null) { %>
                <div class="flash-alert flash-alert-<%= flashType %>" style="margin-top: 20px;">
                    <%= flashMessage %>
                </div>
            <% } %>

            <!-- TAB 1: BOOK CATALOG -->
            <% if ("catalog".equals(activeTab)) { %>
                <div id="viewContent">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin: 20px 0 10px;">
                        <h2>University Book Catalog</h2>
                        <span>Total: <strong><%= books != null ? books.size() : 0 %></strong> titles</span>
                    </div>

                    <!-- Search Box -->
                    <form action="${pageContext.request.contextPath}/dashboard" method="get" class="search-bar-form">
                        <input type="hidden" name="tab" value="catalog">
                        <input type="text" name="q" placeholder="Search by book title, author, subject, or ISBN..."
                               value="<%= request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery") : "" %>">
                        <button type="submit" class="primary-button" style="width:auto; margin:0; padding:10px 20px;">Search</button>
                        <% if (request.getAttribute("searchQuery") != null) { %>
                            <a href="${pageContext.request.contextPath}/dashboard?tab=catalog" class="btn-action btn-danger" style="display:flex; align-items:center;">Clear</a>
                        <% } %>
                    </form>

                    <!-- Books Table -->
                    <div class="panel" style="overflow-x:auto; padding:0;">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Title &amp; Description</th>
                                    <th>Author</th>
                                    <th>Subject</th>
                                    <th>Access Rule</th>
                                    <th>Availability</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (books == null || books.isEmpty()) { %>
                                    <tr>
                                        <td colspan="7" style="text-align:center; padding:30px; color:#888;">
                                            No books found matching criteria.
                                        </td>
                                    </tr>
                                <% } else {
                                    for (Book book : books) {
                                        boolean canUserBorrow = currentUser.canBorrow(book);
                                        boolean isAvailable = book.isAvailable();
                                %>
                                    <tr>
                                        <td><strong>#<%= book.getId() %></strong></td>
                                        <td>
                                            <strong><%= book.getTitle() %></strong>
                                            <% if (book.getDescription() != null) { %>
                                                <br><small style="color:#666;"><%= book.getDescription() %></small>
                                            <% } %>
                                            <br><small style="color:#999;">ISBN: <%= book.getIsbn() %></small>
                                        </td>
                                        <td><%= book.getAuthor() %></td>
                                        <td><span class="tag tag-general"><%= book.getSubject() %></span></td>
                                        <td>
                                            <% if (book.isFacultyOnly()) { %>
                                                <span class="tag tag-faculty">Faculty Reserve</span>
                                            <% } else { %>
                                                <span class="tag tag-general">All Patrons</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (isAvailable) { %>
                                                <span class="tag tag-available"><%= book.getAvailableCopies() %> / <%= book.getTotalCopies() %> available</span>
                                            <% } else { %>
                                                <span class="tag tag-unavailable">All on loan</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (!canUserBorrow) { %>
                                                <span class="tag tag-faculty" title="Subclass restriction: Student cannot borrow Faculty-only books">Faculty Only</span>
                                            <% } else if (!isAvailable) { %>
                                                <span class="btn-action btn-disabled">Out of Stock</span>
                                            <% } else { %>
                                                <form action="${pageContext.request.contextPath}/action" method="post" style="display:inline;">
                                                    <input type="hidden" name="action" value="borrow">
                                                    <input type="hidden" name="bookId" value="<%= book.getId() %>">
                                                    <button type="submit" class="btn-action btn-primary">Borrow</button>
                                                </form>
                                            <% } %>

                                            <% if (currentUser.isAdmin()) { %>
                                                <form action="${pageContext.request.contextPath}/action" method="post" style="display:inline; margin-left:4px;"
                                                      onsubmit="return confirm('Delete book: <%= book.getTitle() %>?');">
                                                    <input type="hidden" name="action" value="deleteBook">
                                                    <input type="hidden" name="bookId" value="<%= book.getId() %>">
                                                    <button type="submit" class="btn-action btn-danger" style="padding:4px 8px; font-size:11px;">Del</button>
                                                </form>
                                            <% } %>
                                        </td>
                                    </tr>
                                <%      }
                                   } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

            <!-- TAB 2: MY LOANS -->
            <% if ("loans".equals(activeTab)) { %>
                <div id="viewContent">
                    <h2 style="margin-top:20px;">Active Lending Transactions</h2>
                    <p style="color:#666;">Books currently in your possession. Please return on or before the due date to avoid late return fines.</p>

                    <div class="panel" style="overflow-x:auto; padding:0; margin-bottom:30px;">
                        <table>
                            <thead>
                                <tr>
                                    <th>Loan ID</th>
                                    <th>Book Title</th>
                                    <th>Issue Date</th>
                                    <th>Due Date</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (activeLoans == null || activeLoans.isEmpty()) { %>
                                    <tr>
                                        <td colspan="6" style="text-align:center; padding:30px; color:#888;">
                                            You have no active loans right now.
                                        </td>
                                    </tr>
                                <% } else {
                                    for (Loan loan : activeLoans) {
                                        boolean overdue = loan.isOverdue();
                                %>
                                    <tr>
                                        <td>#<%= loan.getId() %></td>
                                        <td><strong><%= loan.getBookTitle() %></strong></td>
                                        <td><%= loan.getIssueDate() %></td>
                                        <td>
                                            <strong><%= loan.getDueDate() %></strong>
                                        </td>
                                        <td>
                                            <% if (overdue) { %>
                                                <span class="tag tag-overdue"><%= loan.getOverdueDays() %> days overdue!</span>
                                                <br><small style="color:#b00020;">Accruing fine at &#8377;<%= currentUser.getDailyFineRate() %>/day</small>
                                            <% } else { %>
                                                <span class="tag tag-available">Active</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/action" method="post">
                                                <input type="hidden" name="action" value="return">
                                                <input type="hidden" name="loanId" value="<%= loan.getId() %>">
                                                <button type="submit" class="btn-action btn-success">Return Book</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%      }
                                   } %>
                            </tbody>
                        </table>
                    </div>

                    <h2>Past Loan History</h2>
                    <div class="panel" style="overflow-x:auto; padding:0;">
                        <table>
                            <thead>
                                <tr>
                                    <th>Loan ID</th>
                                    <th>Book Title</th>
                                    <th>Issue Date</th>
                                    <th>Due Date</th>
                                    <th>Returned Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (allUserLoans == null || allUserLoans.stream().noneMatch(l -> !l.isActive())) { %>
                                    <tr>
                                        <td colspan="5" style="text-align:center; padding:20px; color:#888;">
                                            No returned loan history recorded yet.
                                        </td>
                                    </tr>
                                <% } else {
                                    for (Loan loan : allUserLoans) {
                                        if (!loan.isActive()) {
                                %>
                                    <tr>
                                        <td>#<%= loan.getId() %></td>
                                        <td><%= loan.getBookTitle() %></td>
                                        <td><%= loan.getIssueDate() %></td>
                                        <td><%= loan.getDueDate() %></td>
                                        <td><span class="tag tag-available">Returned <%= loan.getReturnDate() %></span></td>
                                    </tr>
                                <%      }
                                    }
                                } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

            <!-- TAB 3: FINES & LEDGER -->
            <% if ("fines".equals(activeTab)) { %>
                <div id="viewContent">
                    <h2 style="margin-top:20px;">Library Dues &amp; Fines</h2>

                    <div class="stat-grid">
                        <div class="stat-card">
                            <h3>&#8377;<%= totalUnpaidFine %></h3>
                            <p>Total Outstanding Fines</p>
                        </div>
                        <div class="stat-card">
                            <h3>&#8377;<%= currentUser.getDailyFineRate() %>/day</h3>
                            <p>Your Overdue Fine Rate</p>
                        </div>
                    </div>

                    <div class="panel" style="overflow-x:auto; padding:0;">
                        <table>
                            <thead>
                                <tr>
                                    <th>Fine ID</th>
                                    <th>Book</th>
                                    <th>Reason</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (userFines == null || userFines.isEmpty()) { %>
                                    <tr>
                                        <td colspan="6" style="text-align:center; padding:30px; color:#888;">
                                            Great news! You have no fines on your account.
                                        </td>
                                    </tr>
                                <% } else {
                                    for (Fine fine : userFines) {
                                %>
                                    <tr>
                                        <td>#<%= fine.getId() %></td>
                                        <td><%= fine.getBookTitle() != null ? fine.getBookTitle() : "Book #" + fine.getBookId() %></td>
                                        <td><%= fine.getReason() %></td>
                                        <td><strong>&#8377;<%= fine.getAmount() %></strong></td>
                                        <td>
                                            <% if (fine.isPaid()) { %>
                                                <span class="tag tag-available">PAID</span>
                                            <% } else { %>
                                                <span class="tag tag-faculty">UNPAID</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (!fine.isPaid()) { %>
                                                <form action="${pageContext.request.contextPath}/action" method="post">
                                                    <input type="hidden" name="action" value="payFine">
                                                    <input type="hidden" name="fineId" value="<%= fine.getId() %>">
                                                    <button type="submit" class="btn-action btn-success">Pay &#8377;<%= fine.getAmount() %></button>
                                                </form>
                                            <% } else { %>
                                                <span style="color:#666; font-size:12px;">Cleared</span>
                                            <% } %>
                                        </td>
                                    </tr>
                                <%      }
                                   } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

            <!-- TAB 4: ADMIN MANAGEMENT CONSOLE -->
            <% if ("admin".equals(activeTab) && currentUser.isAdmin()) { %>
                <div id="viewContent">
                    <h2 style="margin-top:20px;">Library Administration Console</h2>

                    <!-- Admin KPI Stats -->
                    <div class="stat-grid">
                        <div class="stat-card">
                            <h3><%= request.getAttribute("totalBooksCount") %></h3>
                            <p>Titles in Catalog</p>
                        </div>
                        <div class="stat-card">
                            <h3><%= request.getAttribute("totalUsersCount") %></h3>
                            <p>Registered Patrons</p>
                        </div>
                        <div class="stat-card">
                            <h3><%= request.getAttribute("totalActiveLoansCount") %></h3>
                            <p>Active Borrowed Loans</p>
                        </div>
                        <div class="stat-card">
                            <h3>&#8377;<%= request.getAttribute("totalFineAmount") %></h3>
                            <p>Total Fines Incurred</p>
                        </div>
                    </div>

                    <!-- Add New Book Form Panel -->
                    <div class="panel" style="margin-bottom:30px;">
                        <h3>Add New Title to University Repository</h3>
                        <form action="${pageContext.request.contextPath}/action" method="post" style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                            <input type="hidden" name="action" value="addBook">

                            <div>
                                <label style="display:block; font-weight:bold; margin-bottom:5px;">Book Title *</label>
                                <input type="text" name="title" required placeholder="e.g. Operating System Concepts" style="width:100%; padding:9px; border-radius:4px; border:1px solid #ccc;">
                            </div>

                            <div>
                                <label style="display:block; font-weight:bold; margin-bottom:5px;">Author(s) *</label>
                                <input type="text" name="author" required placeholder="e.g. Silberschatz, Galvin" style="width:100%; padding:9px; border-radius:4px; border:1px solid #ccc;">
                            </div>

                            <div>
                                <label style="display:block; font-weight:bold; margin-bottom:5px;">Subject / Category</label>
                                <input type="text" name="subject" placeholder="e.g. Computer Science" style="width:100%; padding:9px; border-radius:4px; border:1px solid #ccc;">
                            </div>

                            <div>
                                <label style="display:block; font-weight:bold; margin-bottom:5px;">ISBN</label>
                                <input type="text" name="isbn" placeholder="e.g. 978-1118063330" style="width:100%; padding:9px; border-radius:4px; border:1px solid #ccc;">
                            </div>

                            <div>
                                <label style="display:block; font-weight:bold; margin-bottom:5px;">Total Inventory Copies *</label>
                                <input type="number" name="copies" min="1" max="100" value="5" required style="width:100%; padding:9px; border-radius:4px; border:1px solid #ccc;">
                            </div>

                            <div>
                                <label style="display:block; font-weight:bold; margin-bottom:5px;">Access Permission (Polymorphic Rule)</label>
                                <select name="facultyOnly" style="width:100%; padding:9px; border-radius:4px; border:1px solid #ccc;">
                                    <option value="false">General Public (Students &amp; Faculty)</option>
                                    <option value="true">Faculty Reserve Only (Restricted to Faculty)</option>
                                </select>
                            </div>

                            <div style="grid-column: 1 / -1;">
                                <label style="display:block; font-weight:bold; margin-bottom:5px;">Book Description</label>
                                <textarea name="description" rows="2" placeholder="Course syllabus notes or edition details..." style="width:100%; padding:9px; border-radius:4px; border:1px solid #ccc;"></textarea>
                            </div>

                            <div style="grid-column: 1 / -1;">
                                <button type="submit" class="primary-button" style="width:auto; padding:10px 24px;">Publish Book to Catalog</button>
                            </div>
                        </form>
                    </div>

                    <!-- System Fines Ledger & Waiver -->
                    <div class="panel" style="overflow-x:auto; padding:0; margin-bottom:30px;">
                        <div style="padding:15px; border-bottom:1px solid var(--border);">
                            <h3 style="margin:0;">System-wide Fines Ledger</h3>
                        </div>
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Patron ID</th>
                                    <th>Book ID</th>
                                    <th>Amount</th>
                                    <th>Reason</th>
                                    <th>Status</th>
                                    <th>Admin Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    @SuppressWarnings("unchecked")
                                    List<Fine> allFines = (List<Fine>) request.getAttribute("allFines");
                                    if (allFines != null) {
                                        for (Fine f : allFines) {
                                %>
                                    <tr>
                                        <td>#<%= f.getId() %></td>
                                        <td><strong><%= f.getUserId() %></strong></td>
                                        <td>#<%= f.getBookId() %></td>
                                        <td>&#8377;<%= f.getAmount() %></td>
                                        <td><%= f.getReason() %></td>
                                        <td>
                                            <% if (f.isPaid()) { %>
                                                <span class="tag tag-available">PAID</span>
                                            <% } else { %>
                                                <span class="tag tag-faculty">UNPAID</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (!f.isPaid()) { %>
                                                <form action="${pageContext.request.contextPath}/action" method="post" style="display:inline;">
                                                    <input type="hidden" name="action" value="waiveFine">
                                                    <input type="hidden" name="fineId" value="<%= f.getId() %>">
                                                    <button type="submit" class="btn-action btn-danger" style="font-size:11px; padding:4px 8px;">Waive Fine</button>
                                                </form>
                                            <% } else { %>
                                                <span style="color:#888; font-size:12px;">Settled</span>
                                            <% } %>
                                        </td>
                                    </tr>
                                <%      }
                                    } %>
                            </tbody>
                        </table>
                    </div>

                    <!-- Registered Users Ledger -->
                    <div class="panel" style="overflow-x:auto; padding:0;">
                        <div style="padding:15px; border-bottom:1px solid var(--border);">
                            <h3 style="margin:0;">Registered University Patrons</h3>
                        </div>
                        <table>
                            <thead>
                                <tr>
                                    <th>User ID</th>
                                    <th>Full Name</th>
                                    <th>Role Subclass</th>
                                    <th>Max Books</th>
                                    <th>Max Days</th>
                                    <th>Daily Fine Rate</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    @SuppressWarnings("unchecked")
                                    List<User> allUsers = (List<User>) request.getAttribute("allUsers");
                                    if (allUsers != null) {
                                        for (User u : allUsers) {
                                %>
                                    <tr>
                                        <td><strong><%= u.getId() %></strong></td>
                                        <td><%= u.getName() %></td>
                                        <td><span class="tag tag-general"><%= u.getRole().getDisplayName() %></span></td>
                                        <td><%= u.getMaxBooksAllowed() %> books</td>
                                        <td><%= u.getMaxLoanDays() %> days</td>
                                        <td>&#8377;<%= u.getDailyFineRate() %>/day</td>
                                    </tr>
                                <%      }
                                    } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

        </main>
    </div>

</body>
</html>

