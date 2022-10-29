package ru.akirakozov.sd.refactoring.repository;

import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.*;

public class SqliteProductStorage implements ProductStorage {
    private final String dbUrl;

    public SqliteProductStorage(String dbUrl) {
        this.dbUrl = dbUrl;
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
