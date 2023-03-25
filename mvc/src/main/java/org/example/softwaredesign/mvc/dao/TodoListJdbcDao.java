package org.example.softwaredesign.mvc.dao;

import org.example.softwaredesign.mvc.model.TodoListItem;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TodoListJdbcDao extends JdbcDaoSupport implements TodoListDao {

    public TodoListJdbcDao(DataSource dataSource) {
        super();
        setDataSource(dataSource);
    }

    @Override
    public List<TodoListItem> getTodoList(LocalDate atDate) {
        var sql = "SELECT id, description, is_done FROM todo_list WHERE at_date = ? ORDER BY id ASC";

        return getTodoListByRequest(
            connection -> {
                var preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setDate(1, Date.valueOf(atDate));

                return preparedStatement;
            }
        );
    }

    @Override
    public void switchIsDoneFlag(long id) {
        var sql = "UPDATE todo_list SET is_done = not is_done WHERE id = ?";

        var jdbcTemplate = getJdbcTemplate();
        if (jdbcTemplate == null) {
            return;
        }

        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<TodoListItem> findTodoListItem(long id) {
        var sql = "SELECT id, description, is_done FROM todo_list WHERE id = ?";

        var items = getTodoListByRequest(
            connection -> {
                var preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, id);

                return preparedStatement;
            }
        );
        if (items.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(items.get(0));
    }

    @Override
    public void addNewItem(TodoListItem todo, LocalDate atDate) {
        var sql = "INSERT INTO todo_list (at_date, description, is_done) VALUES (?, ?, false)";

        var jdbcTemplate = getJdbcTemplate();
        if (jdbcTemplate == null) {
            return;
        }

        jdbcTemplate.update(sql, Date.valueOf(atDate), todo.getDescription());
    }

    public void removeItem(long id) {
        var sql = "DELETE FROM todo_list WHERE id = ?";

        var jdbcTemplate = getJdbcTemplate();
        if (jdbcTemplate == null) {
            return;
        }

        jdbcTemplate.update(sql, id);
    }

    private List<TodoListItem> getTodoListByRequest(PreparedStatementCreator psc) {
        var jdbcTemplate = getJdbcTemplate();
        if (jdbcTemplate == null) {
            return List.of();
        }

        return jdbcTemplate.query(psc, new BeanPropertyRowMapper<>(TodoListItem.class));
    }
}
