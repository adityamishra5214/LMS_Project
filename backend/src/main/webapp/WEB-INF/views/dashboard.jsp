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

    @SuppressWarnings("unchecked")
    List<Book> wishlistBooks = (List<Book>) request.getAttribute("wishlistBooks");

    @SuppressWarnings("unchecked")
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");

    @SuppressWarnings("unchecked")
    List<Discussion> discussions = (List<Discussion>) request.getAttribute("discussions");

    @SuppressWarnings("unchecked")
    List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");

    Integer totalUnpaidFine = (Integer) request.getAttribute("totalUnpaidFine");
    if (totalUnpaidFine == null) totalUnpaidFine = 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BBAU Central Library - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <style>
        .flash-alert {
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 14px;
            font-weight: 500;
        }
        .flash-alert-success { background: #def7ec; color: #03543f; border: 1px solid #bcf0da; }
        .flash-alert-warning { background: #fef08a; color: #854d0e; border: 1px solid #fde047; }
        .flash-alert-danger { background: #fde8e8; color: #9b1c1c; border: 1px solid #f8b4b4; }
        .flash-alert-info { background: #e0f2fe; color: #075985; border: 1px solid #bae6fd; }

        .stat-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
            gap: 15px;
            margin-bottom: 22px;
        }
        .stat-card {
            background: #fff;
            padding: 18px;
            border-radius: 8px;
            border: 1px solid var(--border);
            border-left: 5px solid var(--maroon);
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
        }
        .stat-card h3 { margin: 0; font-size: 24px; color: var(--maroon); }
        .stat-card p { margin: 4px 0 0; color: #666; font-size: 12px; text-transform: uppercase; letter-spacing: 1px; }

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
            padding: 5px 12px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 12px;
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
            font-size: 13px;
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

            <div style="font-size: 12px; color: #666; margin-top: 5px; border-bottom: 1px solid #eee; padding-bottom: 10px;">
                Logged as: <strong><%= currentUser.getName() %></strong><br>
                Type: <em><%= currentUser.getRole().getDisplayName() %></em><br>
                Quota: <strong><%= currentUser.getMaxBooksAllowed() %> Books / <%= currentUser.getMaxLoanDays() %> Days</strong>
            </div>

            <nav id="mainNav" style="margin-top: 15px;">
                <a href="${pageContext.request.contextPath}/dashboard?tab=catalog"
                   class="nav-button <%= "catalog".equals(activeTab) ? "active" : "" %>">
                    📚 Book Catalogue
                </a>
                <a href="${pageContext.request.contextPath}/dashboard?tab=mybooks"
                   class="nav-button <%= "mybooks".equals(activeTab) ? "active" : "" %>">
                    📖 My Books (<%= activeLoans != null ? activeLoans.size() : 0 %>)
                </a>
                <a href="${pageContext.request.contextPath}/dashboard?tab=wishlist"
                   class="nav-button <%= "wishlist".equals(activeTab) ? "active" : "" %>">
                    💖 Wishlist (<%= wishlistBooks != null ? wishlistBooks.size() : 0 %>)
                </a>
                <a href="${pageContext.request.contextPath}/dashboard?tab=fines"
                   class="nav-button <%= "fines".equals(activeTab) ? "active" : "" %>">
                    💳 Fine Section <%= totalUnpaidFine > 0 ? "(&#8377;" + totalUnpaidFine + ")" : "" %>
                </a>
                <a href="${pageContext.request.contextPath}/dashboard?tab=discussions"
                   class="nav-button <%= "discussions".equals(activeTab) ? "active" : "" %>">
                    💬 Reviews &amp; QA
                </a>
                <a href="${pageContext.request.contextPath}/dashboard?tab=notifications"
                   class="nav-button <%= "notifications".equals(activeTab) ? "active" : "" %>">
                    🔔 Notifications (<%= notifications != null ? notifications.stream().filter(n -> !n.isRead()).count() : 0 %>)
                </a>
                <% if (currentUser.isAdmin()) { %>
                    <a href="${pageContext.request.contextPath}/dashboard?tab=admin"
                       class="nav-button <%= "admin".equals(activeTab) ? "active" : "" %>">
                        ⚙️ Admin Console
                    </a>
                <% } %>
            </nav>

            <form action="${pageContext.request.contextPath}/action" method="post" style="margin-top:auto;">
                <input type="hidden" name="action" value="logout">
                <button type="submit" class="logout-button" style="width:100%;">Sign Out</button>
            </form>
        </aside>

        <!-- Main Content Area -->
        <main class="main-content">
            <div class="app-topbar">
                <div>
                    <h1>Babasaheb Bhimrao Ambedkar University</h1>
                    <p style="margin: 4px 0 0; color: #666; font-size: 13px;">
                        Central Library Management System &bull; NAAC 'A++' Accredited
                    </p>
                </div>
                <div class="role-badge">
                    <%= currentUser.getRole().getDisplayName() %>
                </div>
            </div>

            <!-- Flash Alert Messages -->
            <% if (flashMessage != null) { %>
                <div class="flash-alert flash-alert-<%= flashType %>" style="margin-top: 15px;">
                    <%= flashMessage %>
                </div>
            <% } %>

            <!-- TAB 1: BOOK CATALOGUE (2.1 & 2.2) -->
            <% if ("catalog".equals(activeTab)) { %>
                <div id="viewContent">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin: 15px 0 10px;">
                        <h2>University Book Catalogue</h2>
                        <span>Total: <strong><%= books != null ? books.size() : 0 %></strong> titles</span>
                    </div>

                    <form action="${pageContext.request.contextPath}/dashboard" method="get" class="search-bar-form">
                        <input type="hidden" name="tab" value="catalog">
                        <input type="text" name="q" placeholder="Search by book name, author name, or subject..."
                               value="<%= request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery") : "" %>">
                        <button type="submit" class="primary-button" style="width:auto; margin:0; padding:10px 20px;">Search</button>
                    </form>

                    <div class="panel" style="overflow-x:auto; padding:0;">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Book Title &amp; Intro</th>
                                    <th>Author</th>
                                    <th>Subject / Category</th>
                                    <th>Copies (Avail / Total)</th>
                                    <th>Clearance</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (books != null) {
                                    for (Book b : books) {
                                        boolean canBorrow = currentUser.canBorrow(b);
                                        boolean isAvail = b.isAvailable();
                                %>
                                    <tr>
                                        <td>#<%= b.getId() %></td>
                                        <td>
                                            <strong><%= b.getTitle() %></strong>
                                            <br><small style="color:#666;"><%= b.getDescription() %></small>
                                            <br><small style="color:#999;">ISBN: <%= b.getIsbn() %></small>
                                        </td>
                                        <td><%= b.getAuthor() %></td>
                                        <td><span class="tag tag-general"><%= b.getSubject() %></span></td>
                                        <td>
                                            <strong><%= b.getAvailableCopies() %></strong> / <%= b.getTotalCopies() %>
                                        </td>
                                        <td>
                                            <% if (b.isFacultyOnly()) { %>
                                                <span class="tag tag-faculty">Faculty Reserve</span>
                                            <% } else { %>
                                                <span class="tag tag-general">All Patrons</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (!canBorrow) { %>
                                                <span class="btn-action btn-disabled">Faculty Only</span>
                                            <% } else if (!isAvail) { %>
                                                <span class="btn-action btn-disabled">Out of Stock</span>
                                            <% } else { %>
                                                <form action="${pageContext.request.contextPath}/action" method="post" style="display:inline;">
                                                    <input type="hidden" name="action" value="borrow">
                                                    <input type="hidden" name="bookId" value="<%= b.getId() %>">
                                                    <button type="submit" class="btn-action btn-primary">Issue</button>
                                                </form>
                                            <% } %>
                                            <form action="${pageContext.request.contextPath}/action" method="post" style="display:inline; margin-left:4px;">
                                                <input type="hidden" name="action" value="addWishlist">
                                                <input type="hidden" name="bookId" value="<%= b.getId() %>">
                                                <button type="submit" class="btn-action" style="background:#eee; color:#333;">💖</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%  }
                                } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

            <!-- TAB 2: MY BOOKS (2.3) -->
            <% if ("mybooks".equals(activeTab)) { %>
                <div id="viewContent">
                    <h2 style="margin-top:15px;">Currently Issued Books</h2>
                    <div class="panel" style="overflow-x:auto; padding:0; margin-bottom:25px;">
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
                                    <tr><td colspan="6" style="text-align:center; padding:25px; color:#888;">No active loans.</td></tr>
                                <% } else {
                                    for (Loan l : activeLoans) {
                                        boolean overdue = l.isOverdue();
                                %>
                                    <tr>
                                        <td>#<%= l.getId() %></td>
                                        <td><strong><%= l.getBookTitle() %></strong></td>
                                        <td><%= l.getIssueDate() %></td>
                                        <td><strong><%= l.getDueDate() %></strong></td>
                                        <td>
                                            <% if (overdue) { %>
                                                <span class="tag tag-overdue"><%= l.getOverdueDays() %> days overdue (₹5/day)</span>
                                            <% } else { %>
                                                <span class="tag tag-available">Active</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/action" method="post">
                                                <input type="hidden" name="action" value="return">
                                                <input type="hidden" name="loanId" value="<%= l.getId() %>">
                                                <button type="submit" class="btn-action btn-success">Return Book</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%  }
                                } %>
                            </tbody>
                        </table>
                    </div>

                    <h2>Previously Returned Books (Borrowing History)</h2>
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
                                <% if (allUserLoans != null) {
                                    for (Loan l : allUserLoans) {
                                        if (!l.isActive()) {
                                %>
                                    <tr>
                                        <td>#<%= l.getId() %></td>
                                        <td><%= l.getBookTitle() %></td>
                                        <td><%= l.getIssueDate() %></td>
                                        <td><%= l.getDueDate() %></td>
                                        <td><span class="tag tag-available">Returned <%= l.getReturnDate() %></span></td>
                                    </tr>
                                <%      }
                                    }
                                } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

            <!-- TAB 3: WISHLIST (2.4) -->
            <% if ("wishlist".equals(activeTab)) { %>
                <div id="viewContent">
                    <h2 style="margin-top:15px;">Your Saved Wishlist</h2>
                    <p style="color:#666;">Track books you want to read and inspect current shelf availability.</p>

                    <div class="panel" style="overflow-x:auto; padding:0;">
                        <table>
                            <thead>
                                <tr>
                                    <th>Title</th>
                                    <th>Author</th>
                                    <th>Subject</th>
                                    <th>Available Copies</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (wishlistBooks == null || wishlistBooks.isEmpty()) { %>
                                    <tr><td colspan="5" style="text-align:center; padding:25px; color:#888;">Your wishlist is currently empty.</td></tr>
                                <% } else {
                                    for (Book b : wishlistBooks) {
                                %>
                                    <tr>
                                        <td><strong><%= b.getTitle() %></strong></td>
                                        <td><%= b.getAuthor() %></td>
                                        <td><span class="tag tag-general"><%= b.getSubject() %></span></td>
                                        <td><strong><%= b.getAvailableCopies() %></strong> of <%= b.getTotalCopies() %> available</td>
                                        <td>
                                            <% if (b.isAvailable() && currentUser.canBorrow(b)) { %>
                                                <form action="${pageContext.request.contextPath}/action" method="post" style="display:inline;">
                                                    <input type="hidden" name="action" value="borrow">
                                                    <input type="hidden" name="bookId" value="<%= b.getId() %>">
                                                    <button type="submit" class="btn-action btn-primary">Issue</button>
                                                </form>
                                            <% } %>
                                            <form action="${pageContext.request.contextPath}/action" method="post" style="display:inline; margin-left:4px;">
                                                <input type="hidden" name="action" value="removeWishlist">
                                                <input type="hidden" name="bookId" value="<%= b.getId() %>">
                                                <button type="submit" class="btn-action btn-danger">Remove</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%  }
                                } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

            <!-- TAB 4: FINES (2.5) -->
            <% if ("fines".equals(activeTab)) { %>
                <div id="viewContent">
                    <h2 style="margin-top:15px;">Library Fine Section &amp; Payment Gateway</h2>

                    <div class="stat-grid">
                        <div class="stat-card">
                            <h3>&#8377;5 / day</h3>
                            <p>Late Return Fine</p>
                        </div>
                        <div class="stat-card">
                            <h3>&#8377;200</h3>
                            <p>Damaged Book Fine</p>
                        </div>
                        <div class="stat-card">
                            <h3>&#8377;500</h3>
                            <p>Lost Book Replacement</p>
                        </div>
                        <div class="stat-card">
                            <h3>&#8377;<%= totalUnpaidFine %></h3>
                            <p>Your Outstanding Balance</p>
                        </div>
                    </div>

                    <div class="panel" style="overflow-x:auto; padding:0;">
                        <table>
                            <thead>
                                <tr>
                                    <th>Fine ID</th>
                                    <th>Type &amp; Reason</th>
                                    <th>Book</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Payment Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (userFines == null || userFines.isEmpty()) { %>
                                    <tr><td colspan="6" style="text-align:center; padding:25px; color:#888;">No fines assessed on your account.</td></tr>
                                <% } else {
                                    for (Fine f : userFines) {
                                %>
                                    <tr>
                                        <td>#<%= f.getId() %></td>
                                        <td>
                                            <strong><%= f.getFineType() != null ? f.getFineType() : "FINE" %></strong>
                                            <br><small style="color:#666;"><%= f.getReason() %></small>
                                        </td>
                                        <td><%= f.getBookTitle() %></td>
                                        <td><strong>&#8377;<%= f.getAmount() %></strong></td>
                                        <td>
                                            <% if (f.isPaid()) { %>
                                                <span class="tag tag-available">PAID (<%= f.getPaymentMethod() != null ? f.getPaymentMethod() : "Settled" %>)</span>
                                            <% } else { %>
                                                <span class="tag tag-faculty">UNPAID</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (!f.isPaid()) { %>
                                                <form action="${pageContext.request.contextPath}/action" method="post">
                                                    <input type="hidden" name="action" value="payFine">
                                                    <input type="hidden" name="fineId" value="<%= f.getId() %>">
                                                    <input type="hidden" name="amount" value="<%= f.getAmount() %>">
                                                    <input type="hidden" name="payMethod" value="UPI / Smart Card Gateway">
                                                    <button type="submit" class="btn-action btn-success">Pay &#8377;<%= f.getAmount() %> Online</button>
                                                </form>
                                            <% } else { %>
                                                <span style="color:#888; font-size:11px;">Cleared Ref: <%= f.getTransactionId() != null ? f.getTransactionId() : "TXN-OK" %></span>
                                            <% } %>
                                        </td>
                                    </tr>
                                <%  }
                                } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

            <!-- TAB 5: DISCUSSIONS & REVIEWS (2.6) -->
            <% if ("discussions".equals(activeTab)) { %>
                <div id="viewContent">
                    <h2 style="margin-top:15px;">Book Reviews &amp; Academic Q&amp;A</h2>

                    <div class="panel" style="margin-bottom:20px;">
                        <h3>Write a Book Review</h3>
                        <form action="${pageContext.request.contextPath}/action" method="post" style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
                            <input type="hidden" name="action" value="addReview">
                            <div>
                                <label style="display:block; font-size:12px; font-weight:bold;">Select Book</label>
                                <select name="bookId" style="width:100%; padding:8px;">
                                    <% if (books != null) {
                                        for (Book b : books) { %>
                                            <option value="<%= b.getId() %>"><%= b.getTitle() %></option>
                                    <%  }
                                    } %>
                                </select>
                            </div>
                            <div>
                                <label style="display:block; font-size:12px; font-weight:bold;">Star Rating</label>
                                <select name="rating" style="width:100%; padding:8px;">
                                    <option value="5">⭐⭐⭐⭐⭐ (5 Stars)</option>
                                    <option value="4">⭐⭐⭐⭐ (4 Stars)</option>
                                    <option value="3">⭐⭐⭐ (3 Stars)</option>
                                </select>
                            </div>
                            <div style="grid-column:1 / -1;">
                                <label style="display:block; font-size:12px; font-weight:bold;">Headline</label>
                                <input type="text" name="headline" required placeholder="e.g. Masterpiece for algorithmic understanding" style="width:100%; padding:8px;">
                            </div>
                            <div style="grid-column:1 / -1;">
                                <label style="display:block; font-size:12px; font-weight:bold;">Review Comments</label>
                                <textarea name="body" rows="2" placeholder="Write feedback regarding textbook clarity and code quality..." style="width:100%; padding:8px;"></textarea>
                            </div>
                            <div style="grid-column:1 / -1;">
                                <button type="submit" class="btn-action btn-primary" style="padding:8px 18px;">Publish Review</button>
                            </div>
                        </form>
                    </div>

                    <div class="panel" style="padding:0;">
                        <div style="padding:15px; border-bottom:1px solid #eee;">
                            <h3 style="margin:0;">Recent Reviews Feed</h3>
                        </div>
                        <% if (reviews != null) {
                            for (Review r : reviews) { %>
                                <div style="padding:15px; border-bottom:1px solid #eee;">
                                    <div style="display:flex; justify-content:space-between;">
                                        <strong><%= r.getBookTitle() %></strong>
                                        <span><%= "⭐".repeat(r.getRating()) %></span>
                                    </div>
                                    <p style="margin:4px 0; color:#333;"><%= r.getTitle() %></p>
                                    <small style="color:#666;"><%= r.getBody() %></small>
                                    <div style="font-size:11px; color:#888; margin-top:4px;">Reviewed by <strong><%= r.getUserName() %></strong></div>
                                </div>
                        <%  }
                        } %>
                    </div>
                </div>
            <% } %>

            <!-- TAB 6: NOTIFICATIONS (2.7) -->
            <% if ("notifications".equals(activeTab)) { %>
                <div id="viewContent">
                    <h2 style="margin-top:15px;">Patron Notification Center</h2>
                    <p style="color:#666;">Issue confirmations, return receipts, due reminders, overdue notices, and fine payments.</p>

                    <div class="panel" style="padding:0;">
                        <% if (notifications == null || notifications.isEmpty()) { %>
                            <div style="padding:25px; text-align:center; color:#888;">No notifications on your account.</div>
                        <% } else {
                            for (Notification n : notifications) { %>
                                <div style="padding:15px; border-bottom:1px solid #eee;">
                                    <div style="display:flex; justify-content:space-between; align-items:center;">
                                        <strong><%= n.getTitle() %></strong>
                                        <span class="tag tag-general"><%= n.getType() != null ? n.getType().replace('_', ' ') : "NOTICE" %></span>
                                    </div>
                                    <p style="margin:4px 0 0; color:#555; font-size:13px;"><%= n.getMessage() %></p>
                                </div>
                        <%  }
                        } %>
                    </div>
                </div>
            <% } %>

            <!-- TAB 7: ADMIN DASHBOARD (4.1 - 4.4) -->
            <% if ("admin".equals(activeTab) && currentUser.isAdmin()) { %>
                <div id="viewContent">
                    <h2 style="margin-top:15px;">Library Administration Console</h2>

                    <div class="stat-grid">
                        <div class="stat-card">
                            <h3><%= request.getAttribute("totalBooksCount") %></h3>
                            <p>Repository Titles</p>
                        </div>
                        <div class="stat-card">
                            <h3><%= request.getAttribute("totalUsersCount") %></h3>
                            <p>Patrons Registered</p>
                        </div>
                        <div class="stat-card">
                            <h3><%= request.getAttribute("totalActiveLoansCount") %></h3>
                            <p>Active Loans</p>
                        </div>
                        <div class="stat-card">
                            <h3>&#8377;<%= request.getAttribute("totalFineAmount") %></h3>
                            <p>Fine Ledger Total</p>
                        </div>
                    </div>

                    <!-- 4.2 Add New Book -->
                    <div class="panel" style="margin-bottom:20px;">
                        <h3>4.2 Book Management: Add New Book</h3>
                        <form action="${pageContext.request.contextPath}/action" method="post" style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
                            <input type="hidden" name="action" value="addBook">
                            <div>
                                <label style="display:block; font-size:12px; font-weight:bold;">Title *</label>
                                <input type="text" name="title" required placeholder="e.g. Modern Compiler Design" style="width:100%; padding:8px;">
                            </div>
                            <div>
                                <label style="display:block; font-size:12px; font-weight:bold;">Author *</label>
                                <input type="text" name="author" required placeholder="e.g. Dick Grune" style="width:100%; padding:8px;">
                            </div>
                            <div>
                                <label style="display:block; font-size:12px; font-weight:bold;">Category / Subject *</label>
                                <input type="text" name="subject" required placeholder="e.g. Operating Systems" style="width:100%; padding:8px;">
                            </div>
                            <div>
                                <label style="display:block; font-size:12px; font-weight:bold;">Total Copies *</label>
                                <input type="number" name="copies" min="1" max="50" value="5" required style="width:100%; padding:8px;">
                            </div>
                            <div>
                                <label style="display:block; font-size:12px; font-weight:bold;">Access Permission</label>
                                <select name="facultyOnly" style="width:100%; padding:8px;">
                                    <option value="false">General Public (Students &amp; Faculty)</option>
                                    <option value="true">Faculty Reserve Only</option>
                                </select>
                            </div>
                            <div>
                                <label style="display:block; font-size:12px; font-weight:bold;">ISBN</label>
                                <input type="text" name="isbn" placeholder="978-0387972725" style="width:100%; padding:8px;">
                            </div>
                            <div style="grid-column:1 / -1;">
                                <label style="display:block; font-size:12px; font-weight:bold;">Introduction / Description</label>
                                <textarea name="description" rows="2" placeholder="Course preface or synopsis..." style="width:100%; padding:8px;"></textarea>
                            </div>
                            <div style="grid-column:1 / -1;">
                                <button type="submit" class="btn-action btn-primary" style="padding:8px 18px;">Publish Book</button>
                            </div>
                        </form>
                    </div>

                    <!-- 4.1 Student Records -->
                    <div class="panel" style="padding:0; margin-bottom:20px;">
                        <div style="padding:15px; border-bottom:1px solid #eee;">
                            <h3 style="margin:0;">4.1 Student &amp; Patron Records</h3>
                        </div>
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Role</th>
                                    <th>Borrow Quota</th>
                                    <th>Loan Days</th>
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
