package ru.akirakozov.sd.refactoring.repository;

import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductStorage {

    List<Product> getAll() throws SQLException;

    void insert(Product product) throws SQLException;

}
