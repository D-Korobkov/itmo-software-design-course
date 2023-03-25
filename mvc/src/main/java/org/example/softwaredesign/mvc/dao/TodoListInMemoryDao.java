package org.example.softwaredesign.mvc.dao;

import org.example.softwaredesign.mvc.model.TodoListItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TodoListInMemoryDao implements TodoListDao {
    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, LocalDate> itemByIdSearchIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<LocalDate, List<TodoListItem>> lists = new ConcurrentHashMap<>();

    @Override
    public void addNewItem(TodoListItem todo, LocalDate atDate) {
        var id = idGenerator.getAndIncrement();
        todo.setId(id);

        CopyOnWriteArrayList<TodoListItem> defaultList = new CopyOnWriteArrayList<>();
        defaultList.add(todo);
        lists.merge(atDate, defaultList, (existingList, newList) -> {
            existingList.addAll(newList);
            return existingList;
        });
        itemByIdSearchIndex.put(id, atDate);
    }

    @Override
    public void removeItem(long id) {
        if (id > Integer.MAX_VALUE || id < 0) {
            return;
        }

        int intId = (int) id;
        LocalDate atDate = itemByIdSearchIndex.get(intId);
        lists.get(atDate).removeIf(item -> item.getId() == id);
        itemByIdSearchIndex.remove(intId);
    }

    @Override
    public List<TodoListItem> getTodoList(LocalDate atDate) {
        return lists.get(atDate);
    }

    @Override
    public Optional<TodoListItem> findTodoListItem(long id) {
        if (id > Integer.MAX_VALUE || id < 0) {
            return Optional.empty();
        }

        int intId = (int) id;
        LocalDate atDate = itemByIdSearchIndex.get(intId);
        return lists.get(atDate).stream().filter(item -> item.getId() == id).findFirst();
    }

    @Override
    public void switchIsDoneFlag(long id) {
        if (id > Integer.MAX_VALUE || id < 0) {
            return;
        }

        int intId = (int) id;
        LocalDate atDate = itemByIdSearchIndex.get(intId);
        lists.get(atDate).stream().filter(item -> item.getId() == id).findFirst().ifPresent(item -> item.setIsDone(item.isDone()));
    }
}
