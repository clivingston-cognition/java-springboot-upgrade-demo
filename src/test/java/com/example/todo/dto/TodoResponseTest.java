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
    @DisplayName("Should map all fields from entity including dates")
    void shouldMapAllFieldsFromEntity() {
        Todo todo = new Todo("Test", "Description", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setDueDate(LocalDateTime.of(2025, 12, 31, 23, 59));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 12, 0));
        todo.setCompletedAt(LocalDateTime.of(2024, 1, 3, 14, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.COMPLETED);
        assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(response.getDueDate()).isEqualTo("2025-12-31 23:59");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 10:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-02 12:00");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-03 14:00");
        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should mark as overdue when due date is past and status is not COMPLETED or CANCELLED")
    void shouldMarkAsOverdue() {
        Todo todo = new Todo("Overdue Task", "Desc", Priority.HIGH);
        todo.setId(2L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now().minusDays(5));
        todo.setUpdatedAt(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Should not mark as overdue when status is COMPLETED")
    void shouldNotBeOverdueWhenCompleted() {
        Todo todo = new Todo("Done Task", "Desc", Priority.LOW);
        todo.setId(3L);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should not mark as overdue when status is CANCELLED")
    void shouldNotBeOverdueWhenCancelled() {
        Todo todo = new Todo("Cancelled Task", "Desc", Priority.LOW);
        todo.setId(4L);
        todo.setStatus(TodoStatus.CANCELLED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should handle null dates gracefully")
    void shouldHandleNullDates() {
        Todo todo = new Todo("No dates", "Desc", Priority.MEDIUM);
        todo.setId(5L);
        todo.setDueDate(null);
        todo.setCreatedAt(null);
        todo.setUpdatedAt(null);
        todo.setCompletedAt(null);

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getDueDate()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.isOverdue()).isFalse();
    }
}
