package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QueryServletTest {
    private static MockedStatic<DriverManager> driverManagerMockedStatic;
    private static Connection connection;

    @BeforeAll
    public static void setup() throws SQLException {
        driverManagerMockedStatic = mockStatic(DriverManager.class);
        connection = mock(Connection.class);

        when(DriverManager.getConnection(any())).thenReturn(connection);
    }

    @AfterAll
    public static void teardown() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            driverManagerMockedStatic.close();
        }
    }

    @Test
    public void testDoGetMax() throws Exception {
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        Statement statement = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("command")).thenReturn("max");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(any())).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString(any())).thenReturn("IPhone");
        when(rs.getInt(any())).thenReturn(700);

        new QueryServlet().doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%n<h1>Product with max price: </h1>%nIPhone\t700</br>%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(statement, times(1)).executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1");
        verify(rs, times(2)).next();
        verify(rs, times(1)).getString("name");
        verify(rs, times(1)).getInt("price");
        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetMin() throws Exception {
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        Statement statement = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("command")).thenReturn("min");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(any())).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString(any())).thenReturn("Xbox");
        when(rs.getInt(any())).thenReturn(500);

        new QueryServlet().doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%n<h1>Product with min price: </h1>%nXbox\t500</br>%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(statement, times(1)).executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1");
        verify(rs, times(2)).next();
        verify(rs, times(1)).getString("name");
        verify(rs, times(1)).getInt("price");
        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetSum() throws Exception {
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        Statement statement = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("command")).thenReturn("sum");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(any())).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(anyInt())).thenReturn(1200);

        new QueryServlet().doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%nSummary price: %n1200%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(statement, times(1)).executeQuery("SELECT SUM(price) FROM PRODUCT");
        verify(rs, times(1)).next();
        verify(rs, times(1)).getInt(1);
        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetCount() throws Exception {
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        Statement statement = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("command")).thenReturn("count");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(any())).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(anyInt())).thenReturn(2);

        new QueryServlet().doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%nNumber of products: %n2%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(statement, times(1)).executeQuery("SELECT COUNT(*) FROM PRODUCT");
        verify(rs, times(1)).next();
        verify(rs, times(1)).getInt(1);
        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetUnknownCommand() throws Exception {
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("command")).thenReturn("UNKNOWN");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));

        new QueryServlet().doGet(request, response);

        String expectedResponseBody = String.format("Unknown command: UNKNOWN%n");
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
}
