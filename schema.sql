CREATE DATABASE IF NOT EXISTS bbau_library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bbau_library;

CREATE TABLE users (
  id VARCHAR(30) PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('student','faculty','admin') NOT NULL
);

CREATE TABLE books (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  author VARCHAR(150) NOT NULL,
  subject VARCHAR(100) NOT NULL,
  isbn VARCHAR(30) NOT NULL,
  total_copies INT NOT NULL DEFAULT 1,
  available_copies INT NOT NULL DEFAULT 1,
  faculty_only BOOLEAN NOT NULL DEFAULT FALSE,
  description TEXT
);

CREATE TABLE loans (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(30) NOT NULL,
  book_id INT NOT NULL,
  issue_date DATE NOT NULL,
  due_date DATE NOT NULL,
  return_date DATE NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE fines (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(30) NOT NULL,
  book_id INT NOT NULL,
  amount INT NOT NULL,
  reason VARCHAR(255) NOT NULL,
  paid BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE reviews (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(30) NOT NULL,
  book_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE wishlist (
  user_id VARCHAR(30) NOT NULL,
  book_id INT NOT NULL,
  PRIMARY KEY(user_id, book_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE notifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(30) NOT NULL,
  message VARCHAR(500) NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
