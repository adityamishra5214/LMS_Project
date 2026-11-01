# BBAU Library - Watermelon UI Frontend

This frontend is designed using the **Watermelon UI** design system (built on Tailwind CSS with vibrant watermelon rose, rind emerald, and slate color tokens).

## ✨ Features
- **Bento Grid Dashboard**: Real-time KPI cards for Available Titles, Active Borrow Quotas, Dynamic Loan Durations, and Daily Fine Rates.
- **Dynamic Role Selector (Live OOP Sandbox)**: Switch instantly between `Student`, `Faculty`, and `Admin` to observe how OOP inheritance and polymorphism alter access clearances and borrowing limits on the fly!
- **Polymorphic Clearance Guard**: Students attempting to borrow Faculty Reserve books trigger dynamic clearance rejection (`user.canBorrow(book) == false`).
- **Interactive Lending & Returns**: Borrow books, observe live inventory decrements, view active loans, and return books.
- **Fines & Ledgers**: Tracks overdue balances with simulated fine payments.
- **Admin Publishing Console**: Add new book titles and set circulation clearances.

## 🖥️ How to Preview
Open either:
- `index.html` (in root directory)
- or `frontend/pages/index.html`

directly in your web browser (Chrome, Edge, Firefox, Safari).
