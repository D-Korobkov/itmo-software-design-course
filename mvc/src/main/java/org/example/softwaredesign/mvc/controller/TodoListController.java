package org.example.softwaredesign.mvc.controller;

import org.example.softwaredesign.mvc.dao.TodoListDao;
import org.example.softwaredesign.mvc.model.TodoList;
import org.example.softwaredesign.mvc.model.TodoListItem;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class TodoListController {
    private final TodoListDao todoListDao;

    public TodoListController(TodoListDao todoListDao) {
        this.todoListDao = todoListDao;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getStartPage() {
        return String.format("redirect:/todo-list?atDate=%s", DateTimeFormatter.ISO_DATE.format(LocalDate.now()));
    }

    @RequestMapping(value = "/todo-list/item/new", method = RequestMethod.POST)
    public String addTodoListItem(
        @RequestParam("atDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate atDate,
        @ModelAttribute("todo") TodoListItem todo
    ) {
        todoListDao.addNewItem(todo, atDate);

        return String.format("redirect:/todo-list?atDate=%s", DateTimeFormatter.ISO_DATE.format(atDate));
    }

    @RequestMapping(value = "/todo-list", method = RequestMethod.GET)
    public String getTodoList(
        @RequestParam("atDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate atDate,
        ModelMap map
    ) {
        var todos = todoListDao.getTodoList(atDate);
        prepareModelMap(map, new TodoList(atDate, todos));

        return "index";
    }

    @RequestMapping(value = "/todo-list/item/{id}/remove", method = RequestMethod.POST)
    public String removeTodoListItemItem(
        @RequestParam("atDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate atDate,
        @PathVariable("id") Long id
    ) {
        todoListDao.removeItem(id);

        return String.format("redirect:/todo-list?atDate=%s", DateTimeFormatter.ISO_DATE.format(atDate));
    }


    @RequestMapping(value = "/todo-list/item/{id}/switch-is-done-flag", method = RequestMethod.POST)
    public String switchIsDoneFlag(
        @RequestParam("atDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate atDate,
        @PathVariable("id") Long id
    ) {
        todoListDao.switchIsDoneFlag(id);

        return String.format("redirect:/todo-list?atDate=%s", DateTimeFormatter.ISO_DATE.format(atDate));
    }

    private void prepareModelMap(ModelMap map, TodoList todoList) {
        map.addAttribute("todoList", todoList);
        map.addAttribute("todoListItem", new TodoListItem());
    }
}
