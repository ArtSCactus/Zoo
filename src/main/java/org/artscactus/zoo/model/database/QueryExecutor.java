package org.artscactus.zoo.model.database;

import java.sql.*;
import java.util.List;

/**
 * Executes SQL queries and updates on a database connection.
 * This class handles various types of database operations including queries, updates, inserts, and deletes.
 */
public class QueryExecutor {
    private final DatabaseConnection databaseConnection;
    private ResultSet currentLocalData;
    private String lastRequest;
    private String lastUpdate;
    private String lastInsert;
    private String lastRemove;

    /**
     * Creates a QueryExecutor with the specified database connection.
     *
     * @param databaseConnection the database connection to use for executing queries
     */
    public QueryExecutor(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Returns the most recent ResultSet from a query execution.
     *
     * @return the current local data ResultSet
     */
    public ResultSet getCurrentLocalData() {
        return currentLocalData;
    }

    /**
     * Sets the current local data ResultSet.
     *
     * @param currentLocalData the ResultSet to set
     */
    public void setCurrentLocalData(ResultSet currentLocalData) {
        this.currentLocalData = currentLocalData;
    }

    /**
     * Returns the last executed query string.
     *
     * @return the last request string
     */
    public String getLastRequest() {
        return lastRequest;
    }

    /**
     * Returns the last executed update string.
     *
     * @return the last update string
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Returns the last executed insert string.
     *
     * @return the last insert string
     */
    public String getLastInsert() {
        return lastInsert;
    }

    /**
     * Returns the last executed remove/delete string.
     *
     * @return the last remove string
     */
    public String getLastRemove() {
        return lastRemove;
    }

    /**
     * Executes a SQL query and returns the result set.
     * Saves the last request and result set for later reference.
     *
     * @param request the SQL query string
     * @return the ResultSet containing the query results
     * @throws SQLException if a database access error occurs or the connection is closed
     */
    public ResultSet executeRequest(String request) throws SQLException {
        if (databaseConnection.isClosed()) {
            throw new SQLException("Database connection is closed");
        }
        lastRequest = request;
        Statement statement = databaseConnection.getConnection().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet result = statement.executeQuery(request);
        currentLocalData = result;
        return result;
    }

    /**
     * Executes a query without saving the last ResultSet and statement row.
     *
     * @param statementRow the SQL query string
     * @return the ResultSet containing the query results
     * @throws SQLException if a database access error occurs or the connection is closed
     */
    public ResultSet executeAnonymousRequest(String statementRow) throws SQLException {
        if (statementRow == null) {
            throw new NullPointerException("Cannot execute null statement");
        }
        if (databaseConnection.isClosed()) {
            throw new SQLException("Database connection is closed");
        }
        try (Statement statement = databaseConnection.getConnection().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {
            return statement.executeQuery(statementRow);
        }
    }

    /**
     * Executes a prepared statement query with the provided values.
     *
     * @param statementRow the SQL query string with placeholders
     * @param values       the list of values to bind to the query
     * @return the ResultSet containing the query results
     * @throws SQLException if a database access error occurs or the connection is closed
     */
    public ResultSet executePreparedAnonymousRequest(String statementRow, List<String> values) throws SQLException {
        if (statementRow == null) {
            throw new NullPointerException("Cannot execute null statement");
        }
        if (databaseConnection.isClosed()) {
            throw new SQLException("Database connection is closed");
        }
        PreparedStatement statement = databaseConnection.getConnection().prepareStatement(
                statementRow,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        for (int index = 0; index < values.size(); index++) {
            statement.setString(index + 1, values.get(index));
        }
        return statement.executeQuery();
    }

    /**
     * Executes a SQL update statement (CREATE, ALTER, DROP, etc.).
     * Saves the update request string for later reference.
     *
     * @param statementRow the SQL update string
     * @return the number of rows affected
     * @throws SQLException if a database access error occurs or the connection is closed
     */
    public int executeUpdate(String statementRow) throws SQLException {
        if (statementRow == null) {
            throw new NullPointerException("Cannot execute null statement");
        }
        if (databaseConnection.isClosed()) {
            throw new SQLException("Database connection is closed");
        }
        lastUpdate = statementRow;
        try (Statement statement = databaseConnection.getConnection().createStatement()) {
            return statement.executeUpdate(statementRow);
        }
    }

    /**
     * Executes a prepared update statement with the provided values.
     *
     * @param updateRequest the SQL update string with placeholders
     * @param array         the list of values to bind to the update
     * @return the number of rows affected
     * @throws SQLException if a database access error occurs or the connection is closed
     */
    public int executePreparedUpdate(String updateRequest, List<String> array) throws SQLException {
        if (updateRequest == null) {
            throw new NullPointerException("Cannot execute null statement");
        }
        if (databaseConnection.isClosed()) {
            throw new SQLException("Database connection is closed");
        }
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(updateRequest)) {
            for (int index = 0; index < array.size(); index++) {
                statement.setString(index + 1, array.get(index));
            }
            return statement.executeUpdate();
        }
    }

    /**
     * Executes a SQL insert statement.
     * Saves the insert statement for later reference.
     *
     * @param statementRow the SQL insert string
     * @throws SQLException if a database access error occurs or the connection is closed
     */
    public void executeInsert(String statementRow) throws SQLException {
        if (statementRow == null) {
            throw new NullPointerException("Cannot execute null statement");
        }
        if (databaseConnection.isClosed()) {
            throw new SQLException("Database connection is closed");
        }
        lastInsert = statementRow;
        try (Statement statement = databaseConnection.getConnection().createStatement()) {
            statement.execute(statementRow);
        }
    }

    /**
     * Executes a SQL delete/remove statement.
     * Saves the remove statement for later reference.
     *
     * @param statementRow the SQL delete string
     * @throws SQLException if a database access error occurs or the connection is closed
     */
    public void executeRemove(String statementRow) throws SQLException {
        if (statementRow == null) {
            throw new NullPointerException("Cannot execute null statement");
        }
        if (databaseConnection.isClosed()) {
            throw new SQLException("Database connection is closed");
        }
        lastRemove = statementRow;
        try (Statement statement = databaseConnection.getConnection().createStatement()) {
            statement.execute(statementRow);
        }
    }

    /**
     * Returns the underlying database connection.
     *
     * @return the database connection
     */
    public Connection getConnection() {
        return databaseConnection.getConnection();
    }
}
