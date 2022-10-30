package ru.akirakozov.sd.refactoring.repository;

import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductStorage {

    void insert(Product product) throws SQLException;

    List<Product> getAll() throws SQLException;

    Optional<Product> getTheMostExpensiveProduct() throws SQLException;

    Optional<Product> getTheCheapestProduct() throws SQLException;

    int sumProductPrices() throws SQLException;

    int countProducts() throws SQLException;

}
