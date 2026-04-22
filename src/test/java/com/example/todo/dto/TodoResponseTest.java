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
    @DisplayName("Should map all fields from entity")
    void shouldMapAllFieldsFromEntity() {
        Todo todo = new Todo("Test", "Description", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.of(2025, 12, 31, 23, 59));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 12, 30));
        todo.setCompletedAt(LocalDateTime.of(2024, 1, 3, 15, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(response.getDueDate()).isEqualTo("2025-12-31 23:59");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 10:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-02 12:30");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-03 15:00");
    }

    @Test
    @DisplayName("Should handle null dates from entity")
    void shouldHandleNullDates() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.setId(2L);
        todo.setStatus(TodoStatus.PENDING);
        // No dates set

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getDueDate()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should mark as overdue when due date is past and status is PENDING")
    void shouldMarkOverdueWhenPastDueAndPending() {
        Todo todo = new Todo("Overdue", "Desc", Priority.HIGH);
        todo.setId(3L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now().minusDays(5));
        todo.setUpdatedAt(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Should mark as overdue when due date is past and status is IN_PROGRESS")
    void shouldMarkOverdueWhenPastDueAndInProgress() {
        Todo todo = new Todo("Overdue", "Desc", Priority.HIGH);
        todo.setId(4L);
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now().minusDays(5));
        todo.setUpdatedAt(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Should NOT mark as overdue when status is COMPLETED")
    void shouldNotMarkOverdueWhenCompleted() {
        Todo todo = new Todo("Done", "Desc", Priority.HIGH);
        todo.setId(5L);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now().minusDays(5));
        todo.setUpdatedAt(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should NOT mark as overdue when status is CANCELLED")
    void shouldNotMarkOverdueWhenCancelled() {
        Todo todo = new Todo("Cancelled", "Desc", Priority.HIGH);
        todo.setId(6L);
        todo.setStatus(TodoStatus.CANCELLED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now().minusDays(5));
        todo.setUpdatedAt(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should NOT mark as overdue when due date is in the future")
    void shouldNotMarkOverdueWhenDueDateFuture() {
        Todo todo = new Todo("Future", "Desc", Priority.HIGH);
        todo.setId(7L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().plusDays(5));
        todo.setCreatedAt(LocalDateTime.now().minusDays(1));
        todo.setUpdatedAt(LocalDateTime.now());

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Default constructor and setters/getters work")
    void defaultConstructorAndSetters() {
        TodoResponse response = new TodoResponse();
        response.setId(10L);
        response.setTitle("T");
        response.setDescription("D");
        response.setStatus(TodoStatus.COMPLETED);
        response.setPriority(Priority.CRITICAL);
        response.setDueDate("2024-01-01 00:00");
        response.setCreatedAt("2024-01-01 00:00");
        response.setUpdatedAt("2024-01-01 00:00");
        response.setCompletedAt("2024-01-01 00:00");
        response.setOverdue(true);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("T");
        assertThat(response.getDescription()).isEqualTo("D");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.COMPLETED);
        assertThat(response.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(response.getDueDate()).isEqualTo("2024-01-01 00:00");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 00:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-01 00:00");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-01 00:00");
        assertThat(response.isOverdue()).isTrue();
    }
}
