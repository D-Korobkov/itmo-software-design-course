package ru.akirakozov.sd.refactoring.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SqliteProductStorageTest {
    private static MockedStatic<DriverManager> driverManagerMockedStatic;
    private static Connection connection;

    @BeforeEach
    public void setup() throws SQLException {
        driverManagerMockedStatic = mockStatic(DriverManager.class);
        connection = mock(Connection.class);

        when(DriverManager.getConnection(any())).thenReturn(connection);
    }

    @AfterEach
    public void teardown() {
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

    @Test
    public void testGetTheMostExpensiveProduct() throws Exception {
        Product mostExpensiveProduct = new Product("Xbox", 666);

        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(anyString())).thenReturn(mostExpensiveProduct.getName());
        when(resultSet.getLong(anyString())).thenReturn(mostExpensiveProduct.getPrice());

        Optional<Product> product = new SqliteProductStorage("test").getTheMostExpensiveProduct();

        Assertions.assertEquals(Optional.of(mostExpensiveProduct), product);

        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).executeQuery("SELECT NAME, PRICE FROM PRODUCT ORDER BY PRICE DESC LIMIT 1");
        verify(resultSet, times(1)).next();
        verify(resultSet, times(1)).getString("name");
        verify(resultSet, times(1)).getLong("price");
    }

    @Test
    public void testGetTheCheapestProduct() throws Exception {
        Product mostExpensiveProduct = new Product("IPhone", 444);

        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(anyString())).thenReturn(mostExpensiveProduct.getName());
        when(resultSet.getLong(anyString())).thenReturn(mostExpensiveProduct.getPrice());

        Optional<Product> product = new SqliteProductStorage("test").getTheCheapestProduct();

        Assertions.assertEquals(Optional.of(mostExpensiveProduct), product);

        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).executeQuery("SELECT NAME, PRICE FROM PRODUCT ORDER BY PRICE LIMIT 1");
        verify(resultSet, times(1)).next();
        verify(resultSet, times(1)).getString("name");
        verify(resultSet, times(1)).getLong("price");
    }

    @Test
    public void testSumProductPrices() throws Exception {
        int summaryPrice = 1000;

        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(anyString())).thenReturn(summaryPrice);

        int actualSummaryPrice = new SqliteProductStorage("test").sumProductPrices();

        Assertions.assertEquals(summaryPrice, actualSummaryPrice);

        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).executeQuery("SELECT SUM(PRICE) AS PRICE_SUM FROM PRODUCT");
        verify(resultSet, times(1)).next();
        verify(resultSet, times(1)).getInt("PRICE_SUM");
    }

    @Test
    public void testCountProducts() throws Exception {
        int numberOfProducts = 42;

        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(anyString())).thenReturn(numberOfProducts);

        int actualNumberOfProducts = new SqliteProductStorage("test").countProducts();

        Assertions.assertEquals(numberOfProducts, actualNumberOfProducts);

        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).executeQuery("SELECT COUNT(*) AS PRODUCTS_CNT FROM PRODUCT");
        verify(resultSet, times(1)).next();
        verify(resultSet, times(1)).getInt("PRODUCTS_CNT");
    }
}
