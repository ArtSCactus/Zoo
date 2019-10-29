package model.database;

import exceptions.DriverNotFoundException;

import java.sql.*;
import java.util.ArrayList;

public class Storage {
    private Connection connection;
    public Storage() {
    }

    public Storage(Connection connection) throws DriverNotFoundException {
        checkDriver();
        this.connection = connection;
    }

    public Storage(String url, String username, String password) throws SQLException, DriverNotFoundException {
        checkDriver();
        connection = DriverManager.getConnection(url, username, password);
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
     *
     * @param request query string record
     * @return result of request
     * @throws SQLException
     */
    public ResultSet executeRequest(String request) throws SQLException {
        if (connection.isClosed()) {
            // here will be DatabaseConnectionException
        }
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(request);
        return result;
    }

    /**
     * Should be used only to build or change table.
     *
     * @param statementLine
     * @throws SQLException
     */
    public void executeQuery(String statementLine) throws SQLException {
        if (connection.isClosed()) {
            // here will be DatabaseConnectionException
        }
        Statement statement = connection.createStatement();
        statement.executeQuery(statementLine);
    }

}
