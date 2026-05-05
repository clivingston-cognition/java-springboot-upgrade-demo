package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoResponse createTodo(TodoRequest request) {
        log.info("Creating new todo: {}", request.title());
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setPriority(request.priority());
        todo.setStatus(request.status() != null ? request.status() : TodoStatus.PENDING);

        if (request.dueDate() != null && !request.dueDate().isEmpty()) {
            todo.setDueDate(LocalDateTime.parse(request.dueDate(), DATE_FORMATTER));
        }

        Todo saved = todoRepository.save(todo);
        log.info("Created todo with id: {}", saved.getId());
        return TodoResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public TodoResponse getTodoById(Long id) {
        log.debug("Fetching todo with id: {}", id);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        return TodoResponse.fromEntity(todo);
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getAllTodos(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return todoRepository.findAll(pageable).map(TodoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodosByStatus(TodoStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return todoRepository.findByStatus(status, pageable).map(TodoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodosByPriority(Priority priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return todoRepository.findByPriority(priority, pageable).map(TodoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> searchTodos(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return todoRepository.searchByKeyword(keyword, pageable).map(TodoResponse::fromEntity);
    }

    public TodoResponse updateTodo(Long id, TodoRequest request) {
        log.info("Updating todo with id: {}", id);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setPriority(request.priority());
        todo.setStatus(request.status());

        if (request.dueDate() != null && !request.dueDate().isEmpty()) {
            todo.setDueDate(LocalDateTime.parse(request.dueDate(), DATE_FORMATTER));
        } else {
            todo.setDueDate(null);
        }

        Todo updated = todoRepository.save(todo);
        log.info("Updated todo with id: {}", updated.getId());
        return TodoResponse.fromEntity(updated);
    }

    public void deleteTodo(Long id) {
        log.info("Deleting todo with id: {}", id);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        todoRepository.delete(todo);
        log.info("Deleted todo with id: {}", id);
    }

    public TodoResponse toggleComplete(Long id) {
        log.info("Toggling completion for todo with id: {}", id);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        TodoStatus next = switch (todo.getStatus()) {
            case COMPLETED -> TodoStatus.PENDING;
            case PENDING, IN_PROGRESS, CANCELLED -> TodoStatus.COMPLETED;
        };
        todo.setStatus(next);

        Todo updated = todoRepository.save(todo);
        return TodoResponse.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTodoStatistics() {
        List<Todo> overdueTodos = todoRepository.findByDueDateBeforeAndStatusNot(
                LocalDateTime.now(), TodoStatus.COMPLETED);

        return Map.of(
                "total", todoRepository.count(),
                "pending", todoRepository.countByStatus(TodoStatus.PENDING),
                "inProgress", todoRepository.countByStatus(TodoStatus.IN_PROGRESS),
                "completed", todoRepository.countByStatus(TodoStatus.COMPLETED),
                "cancelled", todoRepository.countByStatus(TodoStatus.CANCELLED),
                "overdue", overdueTodos.size()
        );
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getFilteredTodos(TodoStatus status, Priority priority, int page, int size,
                                                String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Todo> source;
        if (status != null && priority != null) {
            source = todoRepository.findByStatusAndPriority(status, priority, pageable);
        } else if (status != null) {
            source = todoRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            source = todoRepository.findByPriority(priority, pageable);
        } else {
            source = todoRepository.findAll(pageable);
        }
        return source.map(TodoResponse::fromEntity);
    }
}
