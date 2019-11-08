package model.database;

import exceptions.DriverNotFoundException;

import java.sql.*;

public class Storage {
    private Connection connection;
    private ResultSet currentLocalData;
    private String lastRequest;
    private String lastUpdate;

    public Storage() {
    }

    public Storage(Connection connection) throws DriverNotFoundException {
        checkDriver();
        this.connection = connection;
        currentLocalData = null;
    }

    public Storage(String url, String username, String password) throws SQLException, DriverNotFoundException {
        checkDriver();
        connection = DriverManager.getConnection(url, username, password);
    }

    public ResultSet getCurrentLocalData() {
        return currentLocalData;
    }

    public void setCurrentLocalData(ResultSet currentLocalData) {
        this.currentLocalData = currentLocalData;
    }

    public String getLastRequest() {
        return lastRequest;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public static void checkDriver() throws DriverNotFoundException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DriverNotFoundException("SQL driver not found.");
        }
    }

    /*public static ArrayList resultSetAsArrayList(ResultSet requestResult){

    }*/

    public void connect(String url, String username, String password) throws SQLException, DriverNotFoundException {
        checkDriver();
        connection = DriverManager.getConnection(url, username, password);
    }


    public void connect(Connection connection) throws SQLException, DriverNotFoundException {
        checkDriver();
        this.connection = connection;
    }

    public void disconnect() throws SQLException {
        connection.close();
    }


    public Connection getConnection() {
        return connection;
    }

    /**
     * Should be used only to request data from database.
     * <p>
     * Saves last request row in {@code String lastRequest}
     * Also saves last ResultSet in {@code ResultSet currentLocalData}
     *
     * @param request query string record
     * @return result of request
     * @throws SQLException
     */
    public ResultSet executeRequest(String request) throws SQLException {
        if (connection.isClosed()) {
            // here will be DatabaseConnectionException
        }
        lastRequest = request;
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(request);
        currentLocalData = result;
        return result;
    }

    /**
     * Should be used only to build or change table.
     * <p>
     * Saves update request row in {@code String lastUpdate}.
     *
     * @param statementRow
     * @throws SQLException
     */
    public int executeUpdate(String statementRow) throws SQLException {
        if (statementRow == null) {
            throw new NullPointerException("Cannot execute null statement");
        }
        if (connection.isClosed()) {
            // here will be DatabaseConnectionException
        }
        lastUpdate = statementRow;
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(statementRow);
        }
    }

    /**Executes request, without saving last ResultSet and statementRow.
     *
     * @param statementRow sql request
     * @return ResultSet
     * @throws SQLException
     */
    public ResultSet executeAnonymousRequest(String statementRow) throws SQLException {
        if (statementRow==null){
            throw new NullPointerException("Cannot execute null statement");
        }
        if (connection.isClosed()){
            // here will be DatabaseConnectionException
        }
        Statement statement = connection.createStatement();
            return statement.executeQuery(statementRow);
    }

    public ResultSet executeAnonymousRequest_SCROLLABLE(String statementRow) throws SQLException {
        Statement statement = connection.createStatement(  ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        return statement.executeQuery(statementRow);
    }

}
