package ru.akirakozov.sd.refactoring.repository;

import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SqliteProductStorage implements ProductStorage {
    private final String dbUrl;

    public SqliteProductStorage(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public List<Product> getAll() throws SQLException {
        try (Connection c = DriverManager.getConnection(dbUrl)) {
            try (Statement stmt = c.createStatement()) {
                String sql = "SELECT NAME, PRICE FROM PRODUCT";

                try (ResultSet rs = stmt.executeQuery(sql)) {
                    List<Product> allProducts = new LinkedList<>();
                    while (rs.next()) {
                        Product product = new Product(rs.getString("name"), rs.getLong("price"));
                        allProducts.add(product);
                    }

                    return allProducts;
                }
            }
        }
    }

    @Override
    public void insert(Product product) throws SQLException {
        try (Connection c = DriverManager.getConnection(dbUrl)) {
            String sql = "INSERT INTO PRODUCT (NAME, PRICE) VALUES (?, ?)";

            try (PreparedStatement stmt = c.prepareStatement(sql)) {
                stmt.setString(1, product.getName());
                stmt.setLong(2, product.getPrice());
                stmt.executeUpdate();
            }
        }
    }

}
