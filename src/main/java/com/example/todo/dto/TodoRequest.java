package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TodoRequest(

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotNull(message = "Priority is required")
        Priority priority,

        @NotNull(message = "Status is required")
        TodoStatus status,

        String dueDate
) {

    public TodoRequest {
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
        if (status == null) {
            status = TodoStatus.PENDING;
        }
    }

    public TodoRequest() {
        this(null, null, Priority.MEDIUM, TodoStatus.PENDING, null);
    }
}
