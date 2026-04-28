package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoRequest Tests")
class TodoRequestTest {

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgs() {
        TodoRequest request = new TodoRequest("Title", "Desc", Priority.HIGH, TodoStatus.IN_PROGRESS, "2024-12-31T23:59");

        assertThat(request.getTitle()).isEqualTo("Title");
        assertThat(request.getDescription()).isEqualTo("Desc");
        assertThat(request.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(request.getDueDate()).isEqualTo("2024-12-31T23:59");
    }

    @Test
    @DisplayName("Should have default values for no-arg constructor")
    void shouldHaveDefaults() {
        TodoRequest request = new TodoRequest();

        assertThat(request.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(request.getTitle()).isNull();
        assertThat(request.getDescription()).isNull();
        assertThat(request.getDueDate()).isNull();
    }
}
