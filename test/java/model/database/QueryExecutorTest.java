package model.database;

import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for QueryExecutor class.
 */
public class QueryExecutorTest {

    private QueryExecutor queryExecutor;
    private DatabaseConnection mockDatabaseConnection;
    private Connection mockConnection;

    @Before
    public void setUp() throws SQLException {
        mockDatabaseConnection = mock(DatabaseConnection.class);
        mockConnection = mock(Connection.class);
        when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);
        when(mockDatabaseConnection.isClosed()).thenReturn(false);
        
        queryExecutor = new QueryExecutor(mockDatabaseConnection);
    }

    @Test
    public void testConstructor() {
        assertNotNull("QueryExecutor should not be null", queryExecutor);
    }

    @Test
    public void testExecuteRequest() throws SQLException {
        String query = "SELECT * FROM test";
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        
        when(mockConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE))
            .thenReturn(mockStatement);
        when(mockStatement.executeQuery(query)).thenReturn(mockResultSet);
        
        ResultSet result = queryExecutor.executeRequest(query);
        
        assertNotNull("ResultSet should not be null", result);
        assertEquals("ResultSet should match", mockResultSet, result);
        assertEquals("Last request should be saved", query, queryExecutor.getLastRequest());
        assertEquals("Current local data should be saved", mockResultSet, queryExecutor.getCurrentLocalData());
    }

    @Test(expected = SQLException.class)
    public void testExecuteRequestThrowsExceptionWhenConnectionClosed() throws SQLException {
        when(mockDatabaseConnection.isClosed()).thenReturn(true);
        
        queryExecutor.executeRequest("SELECT * FROM test");
    }

    @Test
    public void testExecuteAnonymousRequest() throws SQLException {
        String query = "SELECT * FROM test";
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        
        when(mockConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE))
            .thenReturn(mockStatement);
        when(mockStatement.executeQuery(query)).thenReturn(mockResultSet);
        
        ResultSet result = queryExecutor.executeAnonymousRequest(query);
        
        assertNotNull("ResultSet should not be null", result);
        verify(mockStatement, times(1)).close();
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteAnonymousRequestThrowsNullPointerException() throws SQLException {
        queryExecutor.executeAnonymousRequest(null);
    }

    @Test
    public void testExecutePreparedAnonymousRequest() throws SQLException {
        String query = "SELECT * FROM test WHERE id = ?";
        List<String> values = Arrays.asList("1");
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        
        when(mockConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE))
            .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        ResultSet result = queryExecutor.executePreparedAnonymousRequest(query, values);
        
        assertNotNull("ResultSet should not be null", result);
        verify(mockPreparedStatement, times(1)).setString(1, "1");
    }

    @Test
    public void testExecuteUpdate() throws SQLException {
        String updateQuery = "UPDATE test SET name = 'value'";
        Statement mockStatement = mock(Statement.class);
        int expectedRowsAffected = 5;
        
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeUpdate(updateQuery)).thenReturn(expectedRowsAffected);
        
        int rowsAffected = queryExecutor.executeUpdate(updateQuery);
        
        assertEquals("Should return correct rows affected", expectedRowsAffected, rowsAffected);
        assertEquals("Last update should be saved", updateQuery, queryExecutor.getLastUpdate());
        verify(mockStatement, times(1)).close();
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteUpdateThrowsNullPointerException() throws SQLException {
        queryExecutor.executeUpdate(null);
    }

    @Test
    public void testExecutePreparedUpdate() throws SQLException {
        String updateQuery = "UPDATE test SET name = ? WHERE id = ?";
        List<String> values = Arrays.asList("newName", "1");
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        int expectedRowsAffected = 3;
        
        when(mockConnection.prepareStatement(updateQuery)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(expectedRowsAffected);
        
        int rowsAffected = queryExecutor.executePreparedUpdate(updateQuery, values);
        
        assertEquals("Should return correct rows affected", expectedRowsAffected, rowsAffected);
        verify(mockPreparedStatement, times(1)).setString(1, "newName");
        verify(mockPreparedStatement, times(1)).setString(2, "1");
        verify(mockPreparedStatement, times(1)).close();
    }

    @Test
    public void testExecuteInsert() throws SQLException {
        String insertQuery = "INSERT INTO test (name) VALUES ('value')";
        Statement mockStatement = mock(Statement.class);
        
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.execute(insertQuery)).thenReturn(true);
        
        queryExecutor.executeInsert(insertQuery);
        
        assertEquals("Last insert should be saved", insertQuery, queryExecutor.getLastInsert());
        verify(mockStatement, times(1)).execute(insertQuery);
        verify(mockStatement, times(1)).close();
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteInsertThrowsNullPointerException() throws SQLException {
        queryExecutor.executeInsert(null);
    }

    @Test
    public void testExecuteRemove() throws SQLException {
        String deleteQuery = "DELETE FROM test WHERE id = 1";
        Statement mockStatement = mock(Statement.class);
        
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.execute(deleteQuery)).thenReturn(true);
        
        queryExecutor.executeRemove(deleteQuery);
        
        assertEquals("Last remove should be saved", deleteQuery, queryExecutor.getLastRemove());
        verify(mockStatement, times(1)).execute(deleteQuery);
        verify(mockStatement, times(1)).close();
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteRemoveThrowsNullPointerException() throws SQLException {
        queryExecutor.executeRemove(null);
    }

    @Test
    public void testGetConnection() {
        Connection result = queryExecutor.getConnection();
        
        assertEquals("Should return the database connection", mockConnection, result);
    }

    @Test
    public void testSetCurrentLocalData() {
        ResultSet mockResultSet = mock(ResultSet.class);
        
        queryExecutor.setCurrentLocalData(mockResultSet);
        
        assertEquals("Current local data should be set", mockResultSet, queryExecutor.getCurrentLocalData());
    }
}
