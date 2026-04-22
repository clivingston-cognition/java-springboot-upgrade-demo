package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoRequest Tests")
class TodoRequestTest {

    @Test
    @DisplayName("Default constructor should use sensible defaults")
    void defaultsShouldBeApplied() {
        TodoRequest request = new TodoRequest();

        assertThat(request.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(request.getTitle()).isNull();
        assertThat(request.getDescription()).isNull();
        assertThat(request.getDueDate()).isNull();
    }

    @Test
    @DisplayName("All args constructor should populate every field")
    void allArgsConstructorShouldPopulate() {
        TodoRequest request = new TodoRequest("t", "d", Priority.HIGH, TodoStatus.IN_PROGRESS, "2024-12-31T23:59");

        assertThat(request.getTitle()).isEqualTo("t");
        assertThat(request.getDescription()).isEqualTo("d");
        assertThat(request.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(request.getDueDate()).isEqualTo("2024-12-31T23:59");
    }

    @Test
    @DisplayName("Setters should round trip")
    void settersShouldRoundTrip() {
        TodoRequest request = new TodoRequest();
        request.setTitle("title");
        request.setDescription("desc");
        request.setPriority(Priority.CRITICAL);
        request.setStatus(TodoStatus.COMPLETED);
        request.setDueDate("2025-01-01T00:00");

        assertThat(request.getTitle()).isEqualTo("title");
        assertThat(request.getDescription()).isEqualTo("desc");
        assertThat(request.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.COMPLETED);
        assertThat(request.getDueDate()).isEqualTo("2025-01-01T00:00");
    }
}
