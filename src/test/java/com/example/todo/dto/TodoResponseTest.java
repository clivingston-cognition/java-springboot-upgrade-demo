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
    @DisplayName("fromEntity should map all fields when all dates are set")
    void fromEntityWithAllDates() {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.of(2030, 12, 31, 23, 59));
        todo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        todo.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 12, 0));
        todo.setCompletedAt(LocalDateTime.of(2024, 1, 3, 14, 0));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getDescription()).isEqualTo("Desc");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(response.getDueDate()).isEqualTo("2030-12-31 23:59");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 10:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-02 12:00");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-03 14:00");
    }

    @Test
    @DisplayName("fromEntity should handle null dates")
    void fromEntityWithNullDates() {
        Todo todo = new Todo("Title", "Desc", Priority.LOW);
        todo.setId(2L);

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.getDueDate()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity should mark as overdue when due date is past and status is PENDING")
    void fromEntityShouldMarkOverdueForPastDueDate() {
        Todo todo = new Todo("Overdue", "Desc", Priority.HIGH);
        todo.setId(3L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("fromEntity should not mark completed todo as overdue")
    void fromEntityShouldNotMarkCompletedAsOverdue() {
        Todo todo = new Todo("Done", "Desc", Priority.HIGH);
        todo.setId(4L);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity should not mark cancelled todo as overdue")
    void fromEntityShouldNotMarkCancelledAsOverdue() {
        Todo todo = new Todo("Cancelled", "Desc", Priority.HIGH);
        todo.setId(5L);
        todo.setStatus(TodoStatus.CANCELLED);
        todo.setDueDate(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("fromEntity should not mark as overdue when due date is in the future")
    void fromEntityShouldNotMarkFutureDueDateAsOverdue() {
        Todo todo = new Todo("Future", "Desc", Priority.MEDIUM);
        todo.setId(6L);
        todo.setStatus(TodoStatus.PENDING);
        todo.setDueDate(LocalDateTime.now().plusDays(10));

        TodoResponse response = TodoResponse.fromEntity(todo);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Default constructor and setters should work")
    void defaultConstructorAndSetters() {
        TodoResponse response = new TodoResponse();
        response.setId(10L);
        response.setTitle("T");
        response.setDescription("D");
        response.setStatus(TodoStatus.IN_PROGRESS);
        response.setPriority(Priority.CRITICAL);
        response.setDueDate("2024-06-15 10:00");
        response.setCreatedAt("2024-01-01 00:00");
        response.setUpdatedAt("2024-01-02 00:00");
        response.setCompletedAt("2024-01-03 00:00");
        response.setOverdue(true);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("T");
        assertThat(response.getDescription()).isEqualTo("D");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(response.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(response.getDueDate()).isEqualTo("2024-06-15 10:00");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 00:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-02 00:00");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-03 00:00");
        assertThat(response.isOverdue()).isTrue();
    }
}
