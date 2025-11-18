package model.database;

import exceptions.DriverNotFoundException;

import java.sql.*;
import java.util.List;

/**
 * Facade class for database operations in the Zoo application.
 * This class provides a unified interface for database connection and query execution
 * using dependency injection of DatabaseConnection and QueryExecutor.
 * 
 * @deprecated Consider using DatabaseConnection and QueryExecutor directly for better separation of concerns.
 */
public class Storage {
    private final DatabaseConnection databaseConnection;
    private final QueryExecutor queryExecutor;

    /**
     * Default constructor that creates new DatabaseConnection and QueryExecutor instances.
     */
    public Storage() {
        this.databaseConnection = new DatabaseConnection();
        this.queryExecutor = new QueryExecutor(databaseConnection);
    }

    /**
     * Creates a Storage with an existing database connection.
     *
     * @param connection the database connection to use
     * @throws DriverNotFoundException if the PostgreSQL driver is not found
     */
    public Storage(Connection connection) throws DriverNotFoundException {
        this.databaseConnection = new DatabaseConnection(connection);
        this.queryExecutor = new QueryExecutor(databaseConnection);
    }

    /**
     * Creates a Storage and establishes a connection using provided credentials.
     *
     * @param url      the database URL
     * @param username the database username
     * @param password the database password
     * @throws SQLException            if a database access error occurs
     * @throws DriverNotFoundException if the PostgreSQL driver is not found
     */
    public Storage(String url, String username, String password) throws SQLException, DriverNotFoundException {
        this.databaseConnection = new DatabaseConnection(url, username, password);
        this.queryExecutor = new QueryExecutor(databaseConnection);
    }

    /**
     * Returns the most recent ResultSet from a query execution.
     *
     * @return the current local data ResultSet
     */
    public ResultSet getCurrentLocalData() {
        return queryExecutor.getCurrentLocalData();
    }

    /**
     * Sets the current local data ResultSet.
     *
     * @param currentLocalData the ResultSet to set
     */
    public void setCurrentLocalData(ResultSet currentLocalData) {
        queryExecutor.setCurrentLocalData(currentLocalData);
    }

    /**
     * Returns the last executed query string.
     *
     * @return the last request string
     */
    public String getLastRequest() {
        return queryExecutor.getLastRequest();
    }

    /**
     * Returns the last executed update string.
     *
     * @return the last update string
     */
    public String getLastUpdate() {
        return queryExecutor.getLastUpdate();
    }

    /**
     * Checks if the PostgreSQL JDBC driver is available.
     *
     * @throws DriverNotFoundException if the driver is not found
     */
    public static void checkDriver() throws DriverNotFoundException {
        DatabaseConnection.checkDriver();
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
        databaseConnection.connect(url, username, password);
    }

    /**
     * Sets the database connection to an existing connection.
     *
     * @param connection the database connection to use
     * @throws SQLException            if a database access error occurs
     * @throws DriverNotFoundException if the PostgreSQL driver is not found
     */
    public void connect(Connection connection) throws SQLException, DriverNotFoundException {
        databaseConnection.connect(connection);
    }

    /**
     * Closes the current database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public void disconnect() throws SQLException {
        databaseConnection.disconnect();
    }

    /**
     * Returns the current database connection.
     *
     * @return the current connection
     */
    public Connection getConnection() {
        return databaseConnection.getConnection();
    }

    /**
     * Returns the last executed insert string.
     *
     * @return the last insert string
     */
    public String getLastInsert() {
        return queryExecutor.getLastInsert();
    }

    /**
     * Returns the last executed remove/delete string.
     *
     * @return the last remove string
     */
    public String getLastRemove() {
        return queryExecutor.getLastRemove();
    }

    /**
     * Executes a SQL query and returns the result set.
     * Should be used only to request data from database.
     * Saves last request row and last ResultSet.
     *
     * @param request the SQL query string
     * @return the ResultSet containing the query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executeRequest(String request) throws SQLException {
        return queryExecutor.executeRequest(request);
    }

    /**
     * Executes a query without saving the last ResultSet and statement row.
     *
     * @param statementRow the SQL query string
     * @return the ResultSet containing the query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executeAnonymousRequest(String statementRow) throws SQLException {
        return queryExecutor.executeAnonymousRequest(statementRow);
    }

    /**
     * Executes a prepared statement query with the provided values.
     *
     * @param statementRow the SQL query string with placeholders
     * @param values       the list of values to bind to the query
     * @return the ResultSet containing the query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executePreparedAnonymousRequest(String statementRow, List<String> values) throws SQLException {
        return queryExecutor.executePreparedAnonymousRequest(statementRow, values);
    }

    /**
     * Executes a SQL update statement (CREATE, ALTER, DROP, etc.).
     * Should be used only to build or change table.
     * Saves update request row.
     *
     * @param statementRow the SQL update string
     * @return the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdate(String statementRow) throws SQLException {
        return queryExecutor.executeUpdate(statementRow);
    }

    /**
     * Executes a prepared update statement with the provided values.
     *
     * @param updateRequest the SQL update string with placeholders
     * @param array         the list of values to bind to the update
     * @return the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int executePreparedUpdate(String updateRequest, List<String> array) throws SQLException {
        return queryExecutor.executePreparedUpdate(updateRequest, array);
    }

    /**
     * Executes a SQL insert statement.
     *
     * @param statementRow the SQL insert string
     * @throws SQLException if a database access error occurs
     */
    public void executeInsert(String statementRow) throws SQLException {
        queryExecutor.executeInsert(statementRow);
    }

    /**
     * Executes a SQL delete/remove statement.
     *
     * @param statementRow the SQL delete string
     * @throws SQLException if a database access error occurs
     */
    public void executeRemove(String statementRow) throws SQLException {
        queryExecutor.executeRemove(statementRow);
    }
}
