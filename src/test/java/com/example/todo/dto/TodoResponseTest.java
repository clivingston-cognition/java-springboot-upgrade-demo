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
    @DisplayName("Should convert entity with all fields populated")
    void shouldConvertEntityWithAllFields() {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.of(2020, 1, 1, 12, 0));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));
        todo.setCompletedAt(LocalDateTime.of(2024, 1, 3, 10, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getDescription()).isEqualTo("Desc");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(response.getDueDate()).isEqualTo("2020-01-01 12:00");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 10:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-02 10:00");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-03 10:00");
        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Should mark as not overdue when status is COMPLETED")
    void shouldNotBeOverdueWhenCompleted() {
        Todo todo = new Todo("Title", "Desc", Priority.MEDIUM);
        todo.setId(1L);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should mark as not overdue when status is CANCELLED")
    void shouldNotBeOverdueWhenCancelled() {
        Todo todo = new Todo("Title", "Desc", Priority.MEDIUM);
        todo.setId(1L);
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
        Todo todo = new Todo("Title", "Desc", Priority.LOW);
        todo.setId(1L);

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getDueDate()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should not be overdue when due date is in the future")
    void shouldNotBeOverdueWhenDueDateInFuture() {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().plusDays(7));
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }
}
