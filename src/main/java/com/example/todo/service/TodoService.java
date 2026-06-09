package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> findAll() {
        return todoRepository.findAllByOrderByCreatedAtDesc();
    }

    public Todo findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    public List<Todo> findByStatus(TodoStatus status) {
        return todoRepository.findByStatus(status);
    }

    public List<Todo> findByPriority(Priority priority) {
        return todoRepository.findByPriority(priority);
    }

    @Transactional
    public Todo create(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setStatus(request.getStatus() != null ? request.getStatus() : TodoStatus.PENDING);
        todo.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        todo.setDueDate(request.getDueDate());
        return todoRepository.save(todo);
    }

    @Transactional
    public Todo update(Long id, TodoRequest request) {
        Todo todo = findById(id);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            todo.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        todo.setDueDate(request.getDueDate());
        return todoRepository.save(todo);
    }

    @Transactional
    public Todo toggleComplete(Long id) {
        Todo todo = findById(id);
        if (todo.getStatus() == TodoStatus.COMPLETED) {
            todo.setStatus(TodoStatus.PENDING);
        } else {
            todo.setStatus(TodoStatus.COMPLETED);
        }
        return todoRepository.save(todo);
    }

    @Transactional
    public void delete(Long id) {
        Todo todo = findById(id);
        todoRepository.delete(todo);
    }

    public long countByStatus(TodoStatus status) {
        return todoRepository.findByStatus(status).size();
    }
}
