package ru.akirakozov.sd.refactoring.dbutils;

import ru.akirakozov.sd.refactoring.fputils.Try;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DbUtils {

    public static <T> Try<SQLException, T> doSelect(
        String dbUrl,
        String selectQuery,
        Function<ResultSet, Try<SQLException, T>> processResultSet
    ) {
        try (Connection c = DriverManager.getConnection(dbUrl)) {
            try (Statement stmt = c.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(selectQuery)) {
                    return processResultSet.apply(rs);
                }
            }
        } catch (SQLException e) {
            return Try.failure(e);
        }
    }

    public static Try<SQLException, Integer> toInt(ResultSet rs, String columnName) {
        try {
            rs.next();
            return Try.success(rs.getInt(columnName));
        } catch (SQLException e) {
            return Try.failure(e);
        }
    }

    public static <T> Try<SQLException, Optional<T>> toOptional(ResultSet rs, Function<ResultSet, Try<SQLException, T>> getItem) {
        try {
            if (rs.next()) {
                T item = getItem.apply(rs).getValue();
                return Try.success(Optional.ofNullable(item));
            }
            return Try.success(Optional.empty());
        } catch (SQLException e) {
            return Try.failure(e);
        }
    }

    public static <T> Try<SQLException, List<T>> toList(ResultSet rs, Function<ResultSet, Try<SQLException, T>> getItem) {
        try {
            List<T> allItems = new LinkedList<>();
            while (rs.next()) {
                T item = getItem.apply(rs).getValue();
                allItems.add(item);
            }
            return Try.success(allItems);
        } catch (SQLException e) {
            return Try.failure(e);
        }
    }

}
