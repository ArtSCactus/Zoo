package util;

import exceptions.DriverNotFoundException;
import model.database.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public boolean connect() throws SQLException, DriverNotFoundException {
        database.connect(url, username, password);
        return true;
    }

    public boolean disconnect() throws SQLException {
        database.disconnect();
        return true;
    }

    public ResultSet executeRequest(String request) throws SQLException {
        return database.executeRequest(request);
    }

    public void executeQuery(String statementLine) throws SQLException {
        database.executeQuery(statementLine);
    }
}
