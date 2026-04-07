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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        log.info("Creating new todo: {}", request.getTitle());
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus() != null ? request.getStatus() : TodoStatus.PENDING);

        if (request.getDueDate() != null && !request.getDueDate().isEmpty()) {
            todo.setDueDate(LocalDateTime.parse(request.getDueDate(), DATE_FORMATTER));
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

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus());

        if (request.getDueDate() != null && !request.getDueDate().isEmpty()) {
            todo.setDueDate(LocalDateTime.parse(request.getDueDate(), DATE_FORMATTER));
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

        if (todo.getStatus() == TodoStatus.COMPLETED) {
            todo.setStatus(TodoStatus.PENDING);
        } else {
            todo.setStatus(TodoStatus.COMPLETED);
        }

        Todo updated = todoRepository.save(todo);
        return TodoResponse.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTodoStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", todoRepository.count());
        stats.put("pending", todoRepository.countByStatus(TodoStatus.PENDING));
        stats.put("inProgress", todoRepository.countByStatus(TodoStatus.IN_PROGRESS));
        stats.put("completed", todoRepository.countByStatus(TodoStatus.COMPLETED));
        stats.put("cancelled", todoRepository.countByStatus(TodoStatus.CANCELLED));

        List<Todo> overdueTodos = todoRepository.findByDueDateBeforeAndStatusNot(
                LocalDateTime.now(), TodoStatus.COMPLETED);
        stats.put("overdue", overdueTodos.size());

        return stats;
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getFilteredTodos(TodoStatus status, Priority priority, int page, int size,
                                                String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (status != null && priority != null) {
            return todoRepository.findByStatusAndPriority(status, priority, pageable)
                    .map(TodoResponse::fromEntity);
        } else if (status != null) {
            return todoRepository.findByStatus(status, pageable).map(TodoResponse::fromEntity);
        } else if (priority != null) {
            return todoRepository.findByPriority(priority, pageable).map(TodoResponse::fromEntity);
        } else {
            return todoRepository.findAll(pageable).map(TodoResponse::fromEntity);
        }
    }
}
