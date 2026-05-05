package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        String dueDate = todo.getDueDate() != null ? todo.getDueDate().format(FORMATTER) : null;
        boolean overdue = todo.getDueDate() != null
                && todo.getDueDate().isBefore(LocalDateTime.now())
                && todo.getStatus() != TodoStatus.COMPLETED
                && todo.getStatus() != TodoStatus.CANCELLED;
        String createdAt = todo.getCreatedAt() != null ? todo.getCreatedAt().format(FORMATTER) : null;
        String updatedAt = todo.getUpdatedAt() != null ? todo.getUpdatedAt().format(FORMATTER) : null;
        String completedAt = todo.getCompletedAt() != null ? todo.getCompletedAt().format(FORMATTER) : null;

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getPriority(),
                dueDate,
                createdAt,
                updatedAt,
                completedAt,
                overdue
        );
    }
}
