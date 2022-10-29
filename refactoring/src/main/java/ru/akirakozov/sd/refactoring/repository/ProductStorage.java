package ru.akirakozov.sd.refactoring.repository;

import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.SQLException;

public interface ProductStorage {

    void insert(Product product) throws SQLException;

}
