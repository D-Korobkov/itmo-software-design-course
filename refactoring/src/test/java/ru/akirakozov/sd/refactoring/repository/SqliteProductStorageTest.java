package ru.akirakozov.sd.refactoring.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

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
    public void testGetAll() throws Exception {
        Product fstProduct = new Product("Xbox", 666);
        Product sndProduct = new Product("IPhone", 444);

        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString(anyString())).thenReturn(fstProduct.getName()).thenReturn(sndProduct.getName());
        when(resultSet.getLong(anyString())).thenReturn(fstProduct.getPrice()).thenReturn(sndProduct.getPrice());

        List<Product> allProducts = new SqliteProductStorage("test").getAll();

        List<Product> expectedAllProducts = Arrays.asList(fstProduct, sndProduct);
        Assertions.assertEquals(expectedAllProducts, allProducts);

        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).executeQuery("SELECT NAME, PRICE FROM PRODUCT");
        verify(resultSet, times(3)).next();
        verify(resultSet, times(2)).getString("name");
        verify(resultSet, times(2)).getLong("price");
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
