package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;
import com.example.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Map;

@Controller
public class TodoViewController {

    private final TodoService todoService;

    public TodoViewController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public String listTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) TodoStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String search,
            Model model) {

        Page<TodoResponse> todosPage;

        if (search != null && !search.trim().isEmpty()) {
            todosPage = todoService.searchTodos(search.trim(), page, size);
            model.addAttribute("search", search);
        } else {
            todosPage = todoService.getFilteredTodos(status, priority, page, size, sortBy, direction);
        }

        Map<String, Object> stats = todoService.getTodoStatistics();

        model.addAttribute("todos", todosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", todosPage.getTotalPages());
        model.addAttribute("totalItems", todosPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("statuses", TodoStatus.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("stats", stats);

        return "index";
    }

    @GetMapping("/todos/new")
    public String showCreateForm(Model model) {
        model.addAttribute("todoRequest", new TodoRequest());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TodoStatus.values());
        model.addAttribute("isEdit", false);
        return "todo-form";
    }

    @PostMapping("/todos/new")
    public String createTodo(@Valid @ModelAttribute("todoRequest") TodoRequest request,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", TodoStatus.values());
            model.addAttribute("isEdit", false);
            return "todo-form";
        }

        todoService.createTodo(request);
        redirectAttributes.addFlashAttribute("successMessage", "Todo created successfully!");
        return "redirect:/";
    }

    @GetMapping("/todos/{id}")
    public String viewTodo(@PathVariable Long id, Model model) {
        TodoResponse todo = todoService.getTodoById(id);
        model.addAttribute("todo", todo);
        return "todo-detail";
    }

    @GetMapping("/todos/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        TodoResponse todo = todoService.getTodoById(id);

        TodoRequest request = new TodoRequest();
        request.setTitle(todo.getTitle());
        request.setDescription(todo.getDescription());
        request.setPriority(todo.getPriority());
        request.setStatus(todo.getStatus());
        request.setDueDate(todo.getDueDate() != null ? todo.getDueDate().replace(" ", "T") : null);

        model.addAttribute("todoRequest", request);
        model.addAttribute("todoId", id);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TodoStatus.values());
        model.addAttribute("isEdit", true);
        return "todo-form";
    }

    @PostMapping("/todos/{id}/edit")
    public String updateTodo(@PathVariable Long id,
                             @Valid @ModelAttribute("todoRequest") TodoRequest request,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("todoId", id);
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", TodoStatus.values());
            model.addAttribute("isEdit", true);
            return "todo-form";
        }

        todoService.updateTodo(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Todo updated successfully!");
        return "redirect:/";
    }

    @PostMapping("/todos/{id}/delete")
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.deleteTodo(id);
        redirectAttributes.addFlashAttribute("successMessage", "Todo deleted successfully!");
        return "redirect:/";
    }

    @PostMapping("/todos/{id}/toggle")
    public String toggleComplete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.toggleComplete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Todo status updated!");
        return "redirect:/";
    }
}
