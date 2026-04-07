package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TodoResponse {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Long id;
    private String title;
    private String description;
    private TodoStatus status;
    private Priority priority;
    private String dueDate;
    private String createdAt;
    private String updatedAt;
    private String completedAt;
    private boolean overdue;

    public TodoResponse() {
    }

    public static TodoResponse fromEntity(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setStatus(todo.getStatus());
        response.setPriority(todo.getPriority());

        if (todo.getDueDate() != null) {
            response.setDueDate(todo.getDueDate().format(FORMATTER));
            response.setOverdue(todo.getDueDate().isBefore(LocalDateTime.now())
                    && todo.getStatus() != TodoStatus.COMPLETED
                    && todo.getStatus() != TodoStatus.CANCELLED);
        }

        if (todo.getCreatedAt() != null) {
            response.setCreatedAt(todo.getCreatedAt().format(FORMATTER));
        }

        if (todo.getUpdatedAt() != null) {
            response.setUpdatedAt(todo.getUpdatedAt().format(FORMATTER));
        }

        if (todo.getCompletedAt() != null) {
            response.setCompletedAt(todo.getCompletedAt().format(FORMATTER));
        }

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }
}
