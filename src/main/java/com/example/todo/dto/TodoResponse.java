package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable response DTO for Todo items, using a Java 17 record.
 */
public record TodoResponse(
        Long id,
        String title,
        String description,
        TodoStatus status,
        Priority priority,
        String dueDate,
        String createdAt,
        String updatedAt,
        String completedAt,
        boolean overdue
) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static TodoResponse fromEntity(Todo todo) {
        String dueDateStr = null;
        boolean isOverdue = false;
        String createdAtStr = null;
        String updatedAtStr = null;
        String completedAtStr = null;

        if (todo.getDueDate() != null) {
            dueDateStr = todo.getDueDate().format(FORMATTER);
            isOverdue = todo.getDueDate().isBefore(LocalDateTime.now())
                    && todo.getStatus() != TodoStatus.COMPLETED
                    && todo.getStatus() != TodoStatus.CANCELLED;
        }

        if (todo.getCreatedAt() != null) {
            createdAtStr = todo.getCreatedAt().format(FORMATTER);
        }

        if (todo.getUpdatedAt() != null) {
            updatedAtStr = todo.getUpdatedAt().format(FORMATTER);
        }

        if (todo.getCompletedAt() != null) {
            completedAtStr = todo.getCompletedAt().format(FORMATTER);
        }

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getPriority(),
                dueDateStr,
                createdAtStr,
                updatedAtStr,
                completedAtStr,
                isOverdue
        );
    }

    // JavaBean-style getters for Thymeleaf and Jackson compatibility
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TodoStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getCompletedAt() { return completedAt; }
    public boolean isOverdue() { return overdue; }
}
