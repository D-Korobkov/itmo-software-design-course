package org.example.softwaredesign.mvc.config;

import org.example.softwaredesign.mvc.dao.TodoListDao;
import org.example.softwaredesign.mvc.dao.TodoListJdbcDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class JdbcDaoContextConfiguration {
    @Bean
    public TodoListDao todoListJdbcDao(DataSource dataSource) {
        return new TodoListJdbcDao(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:todo.db");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }
}
