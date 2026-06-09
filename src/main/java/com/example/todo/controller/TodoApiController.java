package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAll() {
        List<TodoResponse> todos = todoService.findAll().stream()
                .map(TodoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getById(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        return ResponseEntity.ok(TodoResponse.fromEntity(todo));
    }

    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody TodoRequest request) {
        Todo created = todoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TodoResponse.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        Todo updated = todoService.update(id, request);
        return ResponseEntity.ok(TodoResponse.fromEntity(updated));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<TodoResponse> toggleComplete(@PathVariable Long id) {
        Todo toggled = todoService.toggleComplete(id);
        return ResponseEntity.ok(TodoResponse.fromEntity(toggled));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
