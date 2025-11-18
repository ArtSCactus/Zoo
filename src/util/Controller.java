package util;

import exceptions.DriverNotFoundException;
import model.database.Storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller class that manages database operations for the Zoo application.
 * This class acts as a facade between the UI and the database layer,
 * providing a simplified interface for database interactions.
 * 
 * Uses dependency injection to decouple from the Storage implementation.
 */
public class Controller {
    private final Storage database;
    private String url;
    private String username;
    private String password;

    /**
     * Creates a Controller with custom database credentials.
     *
     * @param url      the database URL
     * @param username the database username
     * @param password the database password
     */
    public Controller(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        database = new Storage();
    }

    /**
     * Creates a Controller with an existing Storage instance.
     * This constructor enables dependency injection for better testability.
     *
     * @param storage  the Storage instance to use
     * @param url      the database URL
     * @param username the database username
     * @param password the database password
     */
    public Controller(Storage storage, String url, String username, String password) {
        this.database = storage;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a Controller with default database credentials.
     * Default URL: jdbc:postgresql://127.0.0.1:5432/Zoo
     * Default username: postgres
     * Default password: root
     */
    public Controller() {
        url = "jdbc:postgresql://127.0.0.1:5432/Zoo";
        username = "postgres";
        password = "root";
        database = new Storage();
    }

    /**
     * Creates a Controller with an existing Storage instance and default credentials.
     *
     * @param storage the Storage instance to use
     */
    public Controller(Storage storage) {
        this.database = storage;
        url = "jdbc:postgresql://127.0.0.1:5432/Zoo";
        username = "postgres";
        password = "root";
    }

    /**
     * Gets the database URL.
     *
     * @return the database URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the database URL.
     *
     * @param url the database URL to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the database username.
     *
     * @return the database username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the database username.
     *
     * @param username the database username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the database password.
     *
     * @return the database password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the database password.
     *
     * @param password the database password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the most recent ResultSet from a query execution.
     *
     * @return the current local data ResultSet
     */
    public ResultSet getCurrentLocalData() {
        return database.getCurrentLocalData();
    }

    /**
     * Returns the last executed query string.
     *
     * @return the last request string
     */
    public String getLastRequest() {
        return database.getLastRequest();
    }

    /**
     * Establishes a connection to the database using the configured credentials.
     *
     * @return true if the connection was successful
     * @throws SQLException            if a database access error occurs
     * @throws DriverNotFoundException if the PostgreSQL driver is not found
     */
    public boolean connect() throws SQLException, DriverNotFoundException {
        database.connect(url, username, password);
        return true;
    }

    /**
     * Closes the current database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public void disconnect() throws SQLException {
        database.disconnect();
    }

    /**
     * Executes a SQL query and returns the result set.
     *
     * @param request the SQL query string
     * @return the ResultSet containing the query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executeRequest(String request) throws SQLException {
        return database.executeRequest(request);
    }

    /**
     * Executes a query without saving the last ResultSet and statement row.
     *
     * @param statementRow the SQL query string
     * @return the ResultSet containing the query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executeAnonymousRequest(String statementRow) throws SQLException {
        return database.executeAnonymousRequest(statementRow);
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
        return database.executePreparedAnonymousRequest(statementRow, values);
    }

    /**
     * Executes a SQL update statement (CREATE, ALTER, DROP, etc.).
     *
     * @param statementLine the SQL update string
     * @return the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdate(String statementLine) throws SQLException {
        return database.executeUpdate(statementLine);
    }

    /**
     * Executes a prepared update statement with the provided values.
     *
     * @param statement the SQL update string with placeholders
     * @param arguments the list of values to bind to the update
     * @return the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int executePreparedUpdate(String statement, List<String> arguments) throws SQLException {
        return database.executePreparedUpdate(statement, arguments);
    }

    /**
     * Executes a SQL insert statement.
     *
     * @param statementLine the SQL insert string
     * @throws SQLException if a database access error occurs
     */
    public void executeInsert(String statementLine) throws SQLException {
        database.executeInsert(statementLine);
    }

    /**
     * Executes a SQL delete/remove statement.
     *
     * @param statement the SQL delete string
     * @throws SQLException if a database access error occurs
     */
    public void executeDelete(String statement) throws SQLException {
        database.executeRemove(statement);
    }

    /**
     * Returns the current database connection.
     *
     * @return the current connection
     */
    public Connection getConnection(){
        return database.getConnection();
    }

    /**
     * Returns the last executed remove/delete string.
     *
     * @return the last remove string
     */
    public String getLastRemove(){
        return database.getLastRemove();
    }
}
