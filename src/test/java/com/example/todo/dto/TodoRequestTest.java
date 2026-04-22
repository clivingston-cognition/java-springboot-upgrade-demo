package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoRequest Tests")
class TodoRequestTest {

    @Test
    @DisplayName("Default constructor sets defaults")
    void defaultConstructorSetsDefaults() {
        TodoRequest request = new TodoRequest();
        assertThat(request.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(request.getTitle()).isNull();
        assertThat(request.getDescription()).isNull();
        assertThat(request.getDueDate()).isNull();
    }

    @Test
    @DisplayName("Parameterized constructor sets all fields")
    void parameterizedConstructorSetsAllFields() {
        TodoRequest request = new TodoRequest("Title", "Desc", Priority.HIGH, TodoStatus.IN_PROGRESS, "2024-12-31T23:59");
        assertThat(request.getTitle()).isEqualTo("Title");
        assertThat(request.getDescription()).isEqualTo("Desc");
        assertThat(request.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(request.getDueDate()).isEqualTo("2024-12-31T23:59");
    }

    @Test
    @DisplayName("Setters and getters work correctly")
    void settersAndGettersWork() {
        TodoRequest request = new TodoRequest();
        request.setTitle("My Title");
        request.setDescription("My Desc");
        request.setPriority(Priority.CRITICAL);
        request.setStatus(TodoStatus.CANCELLED);
        request.setDueDate("2025-06-15T14:00");

        assertThat(request.getTitle()).isEqualTo("My Title");
        assertThat(request.getDescription()).isEqualTo("My Desc");
        assertThat(request.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.CANCELLED);
        assertThat(request.getDueDate()).isEqualTo("2025-06-15T14:00");
    }
}
