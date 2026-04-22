package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoResponse Tests")
class TodoResponseTest {

    @Test
    @DisplayName("fromEntity should map all scalar fields")
    void shouldMapAllFields() {
        LocalDateTime now = LocalDateTime.of(2024, 6, 1, 10, 30);
        Todo todo = new Todo("T", "D", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now);
        todo.setDueDate(now.plusDays(5));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("T");
        assertThat(response.getDescription()).isEqualTo("D");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(response.getCreatedAt()).isEqualTo("2024-06-01 10:30");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-06-01 10:30");
        assertThat(response.getCompletedAt()).isEqualTo("2024-06-01 10:30");
        assertThat(response.getDueDate()).isEqualTo("2024-06-06 10:30");
    }

    @Test
    @DisplayName("fromEntity should mark overdue todos")
    void shouldMarkOverdue() {
        Todo todo = new Todo("T", "D", Priority.MEDIUM);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("fromEntity should not mark completed todos as overdue")
    void shouldNotMarkCompletedOverdue() {
        Todo todo = new Todo("T", "D", Priority.MEDIUM);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity should not mark cancelled todos as overdue")
    void shouldNotMarkCancelledOverdue() {
        Todo todo = new Todo("T", "D", Priority.MEDIUM);
        todo.setStatus(TodoStatus.CANCELLED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity should not mark future due dates as overdue")
    void shouldNotMarkFutureAsOverdue() {
        Todo todo = new Todo("T", "D", Priority.MEDIUM);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().plusDays(7));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity should leave unset timestamps null")
    void shouldSkipNullTimestamps() {
        Todo todo = new Todo("T", "D", Priority.MEDIUM);
        todo.setStatus(TodoStatus.PENDING);

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.getDueDate()).isNull();
        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Setters should round trip")
    void settersShouldRoundTrip() {
        TodoResponse response = new TodoResponse();
        response.setId(5L);
        response.setTitle("t");
        response.setDescription("d");
        response.setStatus(TodoStatus.PENDING);
        response.setPriority(Priority.LOW);
        response.setDueDate("2024-01-01 00:00");
        response.setCreatedAt("2024-01-01 00:00");
        response.setUpdatedAt("2024-01-01 00:00");
        response.setCompletedAt("2024-01-01 00:00");
        response.setOverdue(true);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getTitle()).isEqualTo("t");
        assertThat(response.getDescription()).isEqualTo("d");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(response.getPriority()).isEqualTo(Priority.LOW);
        assertThat(response.getDueDate()).isEqualTo("2024-01-01 00:00");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 00:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-01 00:00");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-01 00:00");
        assertThat(response.isOverdue()).isTrue();
    }
}
