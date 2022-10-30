package ru.akirakozov.sd.refactoring.repository;

import ru.akirakozov.sd.refactoring.fputils.Try;
import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static ru.akirakozov.sd.refactoring.dbutils.DbUtils.*;

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

    @Override
    public List<Product> getAll() throws SQLException {
        String sql = "SELECT NAME, PRICE FROM PRODUCT";
        return doSelect(dbUrl, sql, rs -> toList(rs, this::getProduct)).getValue();
    }

    @Override
    public Optional<Product> getTheMostExpensiveProduct() throws SQLException {
        String sql = "SELECT NAME, PRICE FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
        return doSelect(dbUrl, sql, rs -> toOptional(rs, this::getProduct)).getValue();
    }

    @Override
    public Optional<Product> getTheCheapestProduct() throws SQLException {
        String sql = "SELECT NAME, PRICE FROM PRODUCT ORDER BY PRICE LIMIT 1";
        return doSelect(dbUrl, sql, rs -> toOptional(rs, this::getProduct)).getValue();
    }

    @Override
    public int sumProductPrices() throws SQLException {
        String sql = "SELECT SUM(PRICE) AS PRICE_SUM FROM PRODUCT";
        return doSelect(dbUrl, sql, rs -> toInt(rs, "PRICE_SUM")).getValue();
    }

    @Override
    public int countProducts() throws SQLException {
        String sql = "SELECT COUNT(*) AS PRODUCTS_CNT FROM PRODUCT";
        return doSelect(dbUrl, sql, rs -> toInt(rs, "PRODUCTS_CNT")).getValue();
    }

    private Try<SQLException, Product> getProduct(ResultSet rs) {
        try {
            String name = rs.getString("name");
            long price = rs.getLong("price");

            return Try.success(new Product(name, price));
        } catch (SQLException e) {
            return Try.failure(e);
        }
    }
}
