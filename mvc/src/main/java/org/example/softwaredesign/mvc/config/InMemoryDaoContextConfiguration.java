package org.example.softwaredesign.mvc.config;

import org.example.softwaredesign.mvc.dao.TodoListDao;
import org.example.softwaredesign.mvc.dao.TodoListInMemoryDao;
import org.springframework.context.annotation.Bean;

public class InMemoryDaoContextConfiguration {

    @Bean
    public TodoListDao productDao() {
        return new TodoListInMemoryDao();
    }

}
