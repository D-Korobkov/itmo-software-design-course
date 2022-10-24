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

public class GetProductsServletTest {
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
        ResultSet rs = mock(ResultSet.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(any())).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rs.getString(any())).thenReturn("Xbox").thenReturn("IPhone");
        when(rs.getInt(any())).thenReturn(500).thenReturn(700);

        new GetProductsServlet().doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%nXbox\t500</br>%nIPhone\t700</br>%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(statement, times(1)).executeQuery("SELECT * FROM PRODUCT");
        verify(rs, times(3)).next();
        verify(rs, times(2)).getString("name");
        verify(rs, times(2)).getInt("price");
        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }


}
