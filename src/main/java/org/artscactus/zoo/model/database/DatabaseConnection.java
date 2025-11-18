package org.artscactus.zoo.model.database;

import org.artscactus.zoo.exception.DriverNotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections for the Zoo application.
 * This class is responsible for establishing and closing connections to the PostgreSQL database.
 */
public class DatabaseConnection {
    private Connection connection;

    /**
     * Default constructor for DatabaseConnection.
     */
    public DatabaseConnection() {
    }

    /**
     * Creates a DatabaseConnection with an existing connection.
     *
     * @param connection the database connection to use
     * @throws DriverNotFoundException if the PostgreSQL driver is not found
     */
    public DatabaseConnection(Connection connection) throws DriverNotFoundException {
        checkDriver();
        this.connection = connection;
    }

    /**
     * Creates a DatabaseConnection and establishes a connection using provided credentials.
     *
     * @param url      the database URL
     * @param username the database username
     * @param password the database password
     * @throws SQLException            if a database access error occurs
     * @throws DriverNotFoundException if the PostgreSQL driver is not found
     */
    public DatabaseConnection(String url, String username, String password) throws SQLException, DriverNotFoundException {
        checkDriver();
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Checks if the PostgreSQL JDBC driver is available.
     *
     * @throws DriverNotFoundException if the driver is not found
     */
    public static void checkDriver() throws DriverNotFoundException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DriverNotFoundException("SQL driver not found.");
        }
    }

    /**
     * Establishes a connection to the database using provided credentials.
     *
     * @param url      the database URL
     * @param username the database username
     * @param password the database password
     * @throws SQLException            if a database access error occurs
     * @throws DriverNotFoundException if the PostgreSQL driver is not found
     */
    public void connect(String url, String username, String password) throws SQLException, DriverNotFoundException {
        checkDriver();
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Sets the database connection to an existing connection.
     *
     * @param connection the database connection to use
     * @throws SQLException            if a database access error occurs
     * @throws DriverNotFoundException if the PostgreSQL driver is not found
     */
    public void connect(Connection connection) throws SQLException, DriverNotFoundException {
        checkDriver();
        this.connection = connection;
    }

    /**
     * Closes the current database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Returns the current database connection.
     *
     * @return the current connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Checks if the connection is closed.
     *
     * @return true if the connection is closed or null, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean isClosed() throws SQLException {
        return connection == null || connection.isClosed();
    }
}
