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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.*;

public class AddProductServletTest {
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
    public void testDoGet() throws Exception {
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        Statement statement = mock(Statement.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(connection.createStatement()).thenReturn(statement);
        when(request.getParameter("name")).thenReturn("Xbox");
        when(statement.executeUpdate(any())).thenReturn(1);
        when(request.getParameter("price")).thenReturn("666");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));

        new AddProductServlet().doGet(request, response);

        Assertions.assertEquals(String.format("OK%n"), responseBodyWriter.toString());

        verify(request, times(1)).getParameter("name");
        verify(request, times(1)).getParameter("price");
        verify(statement, times(1)).executeUpdate("INSERT INTO PRODUCT (NAME, PRICE) VALUES (\"Xbox\",666)");
        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

}
