package com.webapp.controller;

import com.webapp.model.Todo;
import com.webapp.repository.TodoRepository;
import com.webapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class TodoControllerJpa {
    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("list-todos")
    public String listAllTodos(ModelMap model)
    {
        String username = getLoggedinUsername(model);
        todoRepository.findAll();
        List<Todo> todos = todoRepository.findByUsername(username);
        model.addAttribute("todos",todos);
        return "listtodos";
    }

    private static String getLoggedinUsername(ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("add-todo")
    public String showNewTodo(ModelMap model){
        String username = getLoggedinUsername(model);
        Todo todo = new Todo(0,username,"",LocalDate.now().plusYears(1),false);
        model.put("todo",todo);
        return "todo";
    }
    @PostMapping("add-todo")
    public String addNewTodo(ModelMap model, @Valid Todo todo, BindingResult result)
    {
        if(result.hasErrors()){
            return "todo";
    }
        String username = getName(model);
        todo.setUsername(username);
        todoRepository.save(todo);
        return "redirect:list-todos";
    }

    private static String getName(ModelMap model) {
        return getLoggedinUsername(model);
    }

    @RequestMapping("delete-todo")
    public String deleteTodo(@RequestParam int id)
    {
        todoRepository.deleteById(id);
//        todoService.deleteById(id);
        return "redirect:list-todos";
    }
    @RequestMapping("update-todo")
    public String showUpdateTodo(@RequestParam int id,ModelMap model)
    {
        Todo todo = todoRepository.findById(id).get();
        model.addAttribute("todo",todo);
        return "todo";
    }
    @PostMapping("update-todo")
    public String updateTodo(ModelMap model, @Valid Todo todo, BindingResult result)
    {
        if(result.hasErrors()){
            return "todo";
        }
        String username = getLoggedinUsername(model);
        todo.setUsername(username);
        todoRepository.save(todo);
        return "redirect:list-todos";
    }
}
