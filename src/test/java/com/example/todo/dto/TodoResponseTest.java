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
    @DisplayName("fromEntity maps all fields correctly")
    void fromEntityMapsAllFields() {
        Todo todo = new Todo("Test", "Description", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 16, 11, 0));
        todo.setDueDate(LocalDateTime.of(2025, 12, 31, 23, 59));
        todo.setCompletedAt(null);

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-15 10:30");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-16 11:00");
        assertThat(response.getDueDate()).isEqualTo("2025-12-31 23:59");
        assertThat(response.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("fromEntity marks as overdue when due date is in the past and not completed")
    void fromEntityMarksOverdueForPastDueDate() {
        Todo todo = new Todo("Overdue Task", "Desc", Priority.HIGH);
        todo.setId(2L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("fromEntity does not mark as overdue when status is COMPLETED")
    void fromEntityNotOverdueWhenCompleted() {
        Todo todo = new Todo("Done Task", "Desc", Priority.LOW);
        todo.setId(3L);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setDueDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity does not mark as overdue when status is CANCELLED")
    void fromEntityNotOverdueWhenCancelled() {
        Todo todo = new Todo("Cancelled Task", "Desc", Priority.LOW);
        todo.setId(4L);
        todo.setStatus(TodoStatus.CANCELLED);
        todo.setDueDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity does not mark as overdue when due date is in the future")
    void fromEntityNotOverdueWhenFutureDueDate() {
        Todo todo = new Todo("Future Task", "Desc", Priority.MEDIUM);
        todo.setId(5L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().plusDays(30));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity handles null dates gracefully")
    void fromEntityHandlesNullDates() {
        Todo todo = new Todo("No Dates", "Desc", Priority.LOW);
        todo.setId(6L);
        todo.setStatus(TodoStatus.PENDING);
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

    @Test
    @DisplayName("fromEntity formats completedAt when present")
    void fromEntityFormatsCompletedAt() {
        Todo todo = new Todo("Completed", "Desc", Priority.LOW);
        todo.setId(7L);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 0, 0));
        todo.setCompletedAt(LocalDateTime.of(2024, 1, 2, 14, 30));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getCompletedAt()).isEqualTo("2024-01-02 14:30");
    }
}
