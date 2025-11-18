package org.artscactus.zoo.controller;

import org.artscactus.zoo.exception.DriverNotFoundException;
import org.artscactus.zoo.model.database.Storage;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Controller class.
 */
public class ControllerTest {

    private Controller controller;
    private Storage mockStorage;
    private String testUrl = "jdbc:postgresql://localhost:5432/testdb";
    private String testUsername = "testuser";
    private String testPassword = "testpass";

    @Before
    public void setUp() {
        mockStorage = mock(Storage.class);
    }

    @Test
    public void testConstructorWithCredentials() {
        controller = new Controller(testUrl, testUsername, testPassword);
        
        assertNotNull("Controller should not be null", controller);
        assertEquals("URL should match", testUrl, controller.getUrl());
        assertEquals("Username should match", testUsername, controller.getUsername());
        assertEquals("Password should match", testPassword, controller.getPassword());
    }

    @Test
    public void testConstructorWithStorageAndCredentials() {
        controller = new Controller(mockStorage, testUrl, testUsername, testPassword);
        
        assertNotNull("Controller should not be null", controller);
        assertEquals("URL should match", testUrl, controller.getUrl());
        assertEquals("Username should match", testUsername, controller.getUsername());
        assertEquals("Password should match", testPassword, controller.getPassword());
    }

    @Test
    public void testDefaultConstructor() {
        controller = new Controller();
        
        assertNotNull("Controller should not be null", controller);
        assertEquals("Default URL should be set", "jdbc:postgresql://127.0.0.1:5432/Zoo", controller.getUrl());
        assertEquals("Default username should be set", "postgres", controller.getUsername());
        assertEquals("Default password should be set", "root", controller.getPassword());
    }

    @Test
    public void testConstructorWithStorage() {
        controller = new Controller(mockStorage);
        
        assertNotNull("Controller should not be null", controller);
        assertEquals("Default URL should be set", "jdbc:postgresql://127.0.0.1:5432/Zoo", controller.getUrl());
        assertEquals("Default username should be set", "postgres", controller.getUsername());
        assertEquals("Default password should be set", "root", controller.getPassword());
    }

    @Test
    public void testSetAndGetUrl() {
        controller = new Controller(mockStorage);
        String newUrl = "jdbc:postgresql://newhost:5432/newdb";
        
        controller.setUrl(newUrl);
        
        assertEquals("URL should be updated", newUrl, controller.getUrl());
    }

    @Test
    public void testSetAndGetUsername() {
        controller = new Controller(mockStorage);
        String newUsername = "newuser";
        
        controller.setUsername(newUsername);
        
        assertEquals("Username should be updated", newUsername, controller.getUsername());
    }

    @Test
    public void testSetAndGetPassword() {
        controller = new Controller(mockStorage);
        String newPassword = "newpass";
        
        controller.setPassword(newPassword);
        
        assertEquals("Password should be updated", newPassword, controller.getPassword());
    }

    @Test
    public void testConnect() throws SQLException, DriverNotFoundException {
        controller = new Controller(mockStorage, testUrl, testUsername, testPassword);
        
        boolean result = controller.connect();
        
        assertTrue("Connect should return true", result);
        verify(mockStorage, times(1)).connect(testUrl, testUsername, testPassword);
    }

    @Test
    public void testDisconnect() throws SQLException {
        controller = new Controller(mockStorage);
        
        controller.disconnect();
        
        verify(mockStorage, times(1)).disconnect();
    }

    @Test
    public void testExecuteRequest() throws SQLException {
        controller = new Controller(mockStorage);
        String query = "SELECT * FROM test";
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockStorage.executeRequest(query)).thenReturn(mockResultSet);
        
        ResultSet result = controller.executeRequest(query);
        
        assertEquals("ResultSet should match", mockResultSet, result);
        verify(mockStorage, times(1)).executeRequest(query);
    }

    @Test
    public void testExecuteAnonymousRequest() throws SQLException {
        controller = new Controller(mockStorage);
        String query = "SELECT * FROM test";
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockStorage.executeAnonymousRequest(query)).thenReturn(mockResultSet);
        
        ResultSet result = controller.executeAnonymousRequest(query);
        
        assertEquals("ResultSet should match", mockResultSet, result);
        verify(mockStorage, times(1)).executeAnonymousRequest(query);
    }

    @Test
    public void testExecutePreparedAnonymousRequest() throws SQLException {
        controller = new Controller(mockStorage);
        String query = "SELECT * FROM test WHERE id = ?";
        List<String> values = Arrays.asList("1");
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockStorage.executePreparedAnonymousRequest(query, values)).thenReturn(mockResultSet);
        
        ResultSet result = controller.executePreparedAnonymousRequest(query, values);
        
        assertEquals("ResultSet should match", mockResultSet, result);
        verify(mockStorage, times(1)).executePreparedAnonymousRequest(query, values);
    }

    @Test
    public void testExecuteUpdate() throws SQLException {
        controller = new Controller(mockStorage);
        String updateQuery = "UPDATE test SET name = 'value'";
        int expectedRowsAffected = 5;
        when(mockStorage.executeUpdate(updateQuery)).thenReturn(expectedRowsAffected);
        
        int rowsAffected = controller.executeUpdate(updateQuery);
        
        assertEquals("Rows affected should match", expectedRowsAffected, rowsAffected);
        verify(mockStorage, times(1)).executeUpdate(updateQuery);
    }

    @Test
    public void testExecutePreparedUpdate() throws SQLException {
        controller = new Controller(mockStorage);
        String updateQuery = "UPDATE test SET name = ? WHERE id = ?";
        List<String> values = Arrays.asList("newName", "1");
        int expectedRowsAffected = 3;
        when(mockStorage.executePreparedUpdate(updateQuery, values)).thenReturn(expectedRowsAffected);
        
        int rowsAffected = controller.executePreparedUpdate(updateQuery, values);
        
        assertEquals("Rows affected should match", expectedRowsAffected, rowsAffected);
        verify(mockStorage, times(1)).executePreparedUpdate(updateQuery, values);
    }

    @Test
    public void testExecuteInsert() throws SQLException {
        controller = new Controller(mockStorage);
        String insertQuery = "INSERT INTO test (name) VALUES ('value')";
        
        controller.executeInsert(insertQuery);
        
        verify(mockStorage, times(1)).executeInsert(insertQuery);
    }

    @Test
    public void testExecuteDelete() throws SQLException {
        controller = new Controller(mockStorage);
        String deleteQuery = "DELETE FROM test WHERE id = 1";
        
        controller.executeDelete(deleteQuery);
        
        verify(mockStorage, times(1)).executeRemove(deleteQuery);
    }

    @Test
    public void testGetConnection() {
        controller = new Controller(mockStorage);
        Connection mockConnection = mock(Connection.class);
        when(mockStorage.getConnection()).thenReturn(mockConnection);
        
        Connection result = controller.getConnection();
        
        assertEquals("Connection should match", mockConnection, result);
        verify(mockStorage, times(1)).getConnection();
    }

    @Test
    public void testGetCurrentLocalData() {
        controller = new Controller(mockStorage);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockStorage.getCurrentLocalData()).thenReturn(mockResultSet);
        
        ResultSet result = controller.getCurrentLocalData();
        
        assertEquals("ResultSet should match", mockResultSet, result);
        verify(mockStorage, times(1)).getCurrentLocalData();
    }

    @Test
    public void testGetLastRequest() {
        controller = new Controller(mockStorage);
        String lastRequest = "SELECT * FROM test";
        when(mockStorage.getLastRequest()).thenReturn(lastRequest);
        
        String result = controller.getLastRequest();
        
        assertEquals("Last request should match", lastRequest, result);
        verify(mockStorage, times(1)).getLastRequest();
    }

    @Test
    public void testGetLastRemove() {
        controller = new Controller(mockStorage);
        String lastRemove = "DELETE FROM test WHERE id = 1";
        when(mockStorage.getLastRemove()).thenReturn(lastRemove);
        
        String result = controller.getLastRemove();
        
        assertEquals("Last remove should match", lastRemove, result);
        verify(mockStorage, times(1)).getLastRemove();
    }
}
