package com.bbau.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ============================================================================
 * OOP CONCEPT: SINGLETON PATTERN & RESOURCE MANAGEMENT
 * ============================================================================
 * Thread-safe Singleton managing the JDBC connection lifecycle to MySQL.
 * Designed to fail gracefully if MySQL is not available, allowing seamless fallback
 * to the in-memory LibraryStore.
 */
public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static volatile DBConnection instance;

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "bbau_library";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private String url;
    private String username;
    private String password;
    private boolean driverAvailable;

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.driverAvailable = true;
        } catch (ClassNotFoundException e) {
            this.driverAvailable = false;
            LOGGER.log(Level.WARNING, "MySQL JDBC Driver not found. Using in-memory storage mode.");
        }

        String host = System.getenv().getOrDefault("DB_HOST", DEFAULT_HOST);
        String port = System.getenv().getOrDefault("DB_PORT", DEFAULT_PORT);
        String dbName = System.getenv().getOrDefault("DB_NAME", DEFAULT_DB);

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        this.username = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        this.password = System.getenv().getOrDefault("DB_PASS", DEFAULT_PASSWORD);
    }

    /**
     * Double-checked locking Singleton accessor.
     */
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Attempts to open a database connection. Returns null if database is not reachable.
     */
    public Connection getConnection() {
        if (!driverAvailable) {
            return null;
        }
        try {
            return DriverManager.getConnection(this.url, this.username, this.password);
        } catch (SQLException e) {
            LOGGER.log(Level.FINE, "MySQL not reachable at {0}. In-memory mode active. Reason: {1}",
                    new Object[]{this.url, e.getMessage()});
            return null;
        }
    }

    /**
     * Checks whether a live database connection can be established.
     */
    public boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

