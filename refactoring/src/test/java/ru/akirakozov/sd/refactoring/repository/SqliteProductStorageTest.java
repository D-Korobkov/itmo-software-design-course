package ru.akirakozov.sd.refactoring.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;

import java.sql.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SqliteProductStorageTest {
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
    public void testInsert() throws Exception {
        Product product = new Product("Xbox", 666);

        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(any())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        new SqliteProductStorage("test").insert(product);

        verify(connection, times(1)).prepareStatement("INSERT INTO PRODUCT (NAME, PRICE) VALUES (?, ?)");
        verify(statement, times(1)).setString(1, product.getName());
        verify(statement, times(1)).setLong(2, product.getPrice());
        verify(statement, times(1)).executeUpdate();
    }

}
