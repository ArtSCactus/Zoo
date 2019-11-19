package util;

import exceptions.DriverNotFoundException;
import model.database.Storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Controller {
    private Storage database;
    private String url;
    private String username;
    private String password;

    public Controller(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        database = new Storage();
    }

    public Controller() {
        url = "jdbc:postgresql://127.0.0.1:5432/Zoo";
        username = "postgres";
        password = "root";
        database = new Storage();
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ResultSet getCurrentLocalData() {
        return database.getCurrentLocalData();
    }

    public String getLastRequest() {
        return database.getLastRequest();
    }

    public boolean connect() throws SQLException, DriverNotFoundException {
        database.connect(url, username, password);
        return true;
    }

    public void disconnect() throws SQLException {
        database.disconnect();
    }

    public ResultSet executeRequest(String request) throws SQLException {
        return database.executeRequest(request);
    }

    public ResultSet executeAnonymousRequest(String statementRow) throws SQLException {
        return database.executeAnonymousRequest(statementRow);
    }

    public ResultSet executePreparedAnonymousRequest(String statementRow, List<String> values) throws SQLException {
        return database.executePreparedAnonymousRequest(statementRow, values);
    }

    public int executeUpdate(String statementLine) throws SQLException {
        return database.executeUpdate(statementLine);
    }

    public int executePreparedUpdate(String statement, List<String> arguments) throws SQLException {
        return database.executePreparedUpdate(statement, arguments);
    }

    public void executeInsert(String statementLine) throws SQLException {
        database.executeInsert(statementLine);
    }

    public void executeDelete(String statement) throws SQLException {
        database.executeRemove(statement);
    }

    public Connection getConnection(){
        return database.getConnection();
    }
    public String getLastRemove(){
        return database.getLastRemove();
    }

}
