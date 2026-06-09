package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TodoViewController {

    private final TodoService todoService;

    public TodoViewController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("todos", todoService.findAll());
        model.addAttribute("totalCount", todoService.findAll().size());
        model.addAttribute("pendingCount", todoService.countByStatus(TodoStatus.PENDING));
        model.addAttribute("completedCount", todoService.countByStatus(TodoStatus.COMPLETED));
        return "index";
    }

    @GetMapping("/todos/new")
    public String showCreateForm(Model model) {
        model.addAttribute("todoRequest", new TodoRequest());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TodoStatus.values());
        return "todo-form";
    }

    @PostMapping("/todos")
    public String createTodo(@Valid @ModelAttribute("todoRequest") TodoRequest request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", TodoStatus.values());
            return "todo-form";
        }
        todoService.create(request);
        redirectAttributes.addFlashAttribute("successMessage", "Todo created successfully!");
        return "redirect:/";
    }

    @GetMapping("/todos/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        Todo todo = todoService.findById(id);
        model.addAttribute("todo", todo);
        return "todo-detail";
    }

    @GetMapping("/todos/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Todo todo = todoService.findById(id);
        TodoRequest request = new TodoRequest();
        request.setTitle(todo.getTitle());
        request.setDescription(todo.getDescription());
        request.setStatus(todo.getStatus());
        request.setPriority(todo.getPriority());
        request.setDueDate(todo.getDueDate());

        model.addAttribute("todoRequest", request);
        model.addAttribute("todoId", id);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TodoStatus.values());
        model.addAttribute("editing", true);
        return "todo-form";
    }

    @PostMapping("/todos/{id}")
    public String updateTodo(@PathVariable Long id,
                             @Valid @ModelAttribute("todoRequest") TodoRequest request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("todoId", id);
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", TodoStatus.values());
            model.addAttribute("editing", true);
            return "todo-form";
        }
        todoService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Todo updated successfully!");
        return "redirect:/";
    }

    @PostMapping("/todos/{id}/toggle")
    public String toggleComplete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.toggleComplete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Todo status toggled!");
        return "redirect:/";
    }

    @PostMapping("/todos/{id}/delete")
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Todo deleted successfully!");
        return "redirect:/";
    }
}
