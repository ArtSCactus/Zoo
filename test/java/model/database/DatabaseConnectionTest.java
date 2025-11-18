package model.database;

import exceptions.DriverNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DatabaseConnection class.
 */
public class DatabaseConnectionTest {

    private DatabaseConnection databaseConnection;

    @Before
    public void setUp() {
        databaseConnection = new DatabaseConnection();
    }

    @After
    public void tearDown() throws SQLException {
        if (databaseConnection != null && !databaseConnection.isClosed()) {
            databaseConnection.disconnect();
        }
    }

    @Test
    public void testCheckDriver() {
        try {
            DatabaseConnection.checkDriver();
            // If no exception is thrown, the test passes
        } catch (DriverNotFoundException e) {
            fail("Driver should be available");
        }
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull("DatabaseConnection should not be null", databaseConnection);
    }

    @Test
    public void testConstructorWithConnection() throws DriverNotFoundException, SQLException {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isClosed()).thenReturn(false);
        
        DatabaseConnection dbConn = new DatabaseConnection(mockConnection);
        
        assertNotNull("DatabaseConnection should not be null", dbConn);
        assertEquals("Connection should match", mockConnection, dbConn.getConnection());
    }

    @Test
    public void testConnectWithConnection() throws SQLException, DriverNotFoundException {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isClosed()).thenReturn(false);
        
        databaseConnection.connect(mockConnection);
        
        assertNotNull("Connection should be set", databaseConnection.getConnection());
        assertEquals("Connection should match", mockConnection, databaseConnection.getConnection());
    }

    @Test
    public void testDisconnect() throws SQLException, DriverNotFoundException {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isClosed()).thenReturn(false);
        
        databaseConnection.connect(mockConnection);
        databaseConnection.disconnect();
        
        verify(mockConnection, times(1)).close();
    }

    @Test
    public void testIsClosed() throws SQLException, DriverNotFoundException {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isClosed()).thenReturn(false);
        
        databaseConnection.connect(mockConnection);
        assertFalse("Connection should not be closed", databaseConnection.isClosed());
        
        when(mockConnection.isClosed()).thenReturn(true);
        assertTrue("Connection should be closed", databaseConnection.isClosed());
    }

    @Test
    public void testIsClosedWithNullConnection() throws SQLException {
        assertTrue("Null connection should be considered closed", databaseConnection.isClosed());
    }

    @Test
    public void testGetConnection() throws SQLException, DriverNotFoundException {
        Connection mockConnection = mock(Connection.class);
        databaseConnection.connect(mockConnection);
        
        Connection result = databaseConnection.getConnection();
        
        assertEquals("Should return the same connection", mockConnection, result);
    }

    @Test
    public void testDisconnectDoesNotThrowOnClosedConnection() throws SQLException, DriverNotFoundException {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isClosed()).thenReturn(true);
        
        databaseConnection.connect(mockConnection);
        
        // Should not throw an exception
        databaseConnection.disconnect();
        
        // Verify close was not called since connection was already closed
        verify(mockConnection, never()).close();
    }
}
