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
    @DisplayName("fromEntity maps all fields including timestamps")
    void fromEntityMapsAllFields() {
        Todo todo = new Todo("Title", "Description", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setDueDate(LocalDateTime.of(2024, 12, 31, 23, 59));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 15, 30));
        todo.setCompletedAt(LocalDateTime.of(2024, 1, 3, 9, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(response.getDueDate()).isEqualTo("2024-12-31 23:59");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 10:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-02 15:30");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-03 09:00");
    }

    @Test
    @DisplayName("fromEntity handles null timestamps")
    void fromEntityHandlesNullTimestamps() {
        Todo todo = new Todo("Title", "Description", Priority.LOW);
        todo.setId(2L);

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getDueDate()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity marks as overdue when due date is past and not completed/cancelled")
    void fromEntityMarksOverdue() {
        Todo todo = new Todo("Overdue Task", "Desc", Priority.HIGH);
        todo.setId(3L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now().minusDays(5));
        todo.setUpdatedAt(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("fromEntity does not mark as overdue when status is COMPLETED")
    void fromEntityNotOverdueWhenCompleted() {
        Todo todo = new Todo("Done Task", "Desc", Priority.HIGH);
        todo.setId(4L);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now().minusDays(5));
        todo.setUpdatedAt(LocalDateTime.now());

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity does not mark as overdue when status is CANCELLED")
    void fromEntityNotOverdueWhenCancelled() {
        Todo todo = new Todo("Cancelled Task", "Desc", Priority.MEDIUM);
        todo.setId(5L);
        todo.setStatus(TodoStatus.CANCELLED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));
        todo.setCreatedAt(LocalDateTime.now().minusDays(5));
        todo.setUpdatedAt(LocalDateTime.now());

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity not overdue when due date is in the future")
    void fromEntityNotOverdueWhenFutureDueDate() {
        Todo todo = new Todo("Future Task", "Desc", Priority.LOW);
        todo.setId(6L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().plusDays(5));
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }
}
