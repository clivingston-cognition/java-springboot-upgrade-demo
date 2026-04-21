package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoResponse / TodoRequest DTO Tests")
class TodoResponseTest {

    @Nested
    @DisplayName("TodoResponse.fromEntity")
    class FromEntity {

        @Test
        @DisplayName("Maps all populated fields and formats timestamps")
        void mapsPopulatedFields() {
            Todo todo = new Todo("t", "d", Priority.HIGH);
            todo.setId(5L);
            todo.setStatus(TodoStatus.IN_PROGRESS);
            LocalDateTime now = LocalDateTime.of(2024, 1, 2, 3, 4);
            todo.setCreatedAt(now);
            todo.setUpdatedAt(now);
            todo.setCompletedAt(now);
            todo.setDueDate(now.plusDays(1));

            TodoResponse response = TodoResponse.fromEntity(todo);

            assertThat(response.getId()).isEqualTo(5L);
            assertThat(response.getTitle()).isEqualTo("t");
            assertThat(response.getDescription()).isEqualTo("d");
            assertThat(response.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
            assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
            assertThat(response.getDueDate()).isEqualTo("2024-01-03 03:04");
            assertThat(response.getCreatedAt()).isEqualTo("2024-01-02 03:04");
            assertThat(response.getUpdatedAt()).isEqualTo("2024-01-02 03:04");
            assertThat(response.getCompletedAt()).isEqualTo("2024-01-02 03:04");
        }

        @Test
        @DisplayName("Marks PENDING todo past due date as overdue")
        void marksOverdue() {
            Todo todo = new Todo("t", "d", Priority.HIGH);
            todo.setStatus(TodoStatus.PENDING);
            todo.setDueDate(LocalDateTime.now().minusDays(1));

            TodoResponse response = TodoResponse.fromEntity(todo);

            assertThat(response.isOverdue()).isTrue();
        }

        @Test
        @DisplayName("Does not mark COMPLETED todo as overdue")
        void completedNotOverdue() {
            Todo todo = new Todo("t", "d", Priority.HIGH);
            todo.setStatus(TodoStatus.COMPLETED);
            todo.setDueDate(LocalDateTime.now().minusDays(1));

            TodoResponse response = TodoResponse.fromEntity(todo);

            assertThat(response.isOverdue()).isFalse();
        }

        @Test
        @DisplayName("Does not mark CANCELLED todo as overdue")
        void cancelledNotOverdue() {
            Todo todo = new Todo("t", "d", Priority.HIGH);
            todo.setStatus(TodoStatus.CANCELLED);
            todo.setDueDate(LocalDateTime.now().minusDays(1));

            TodoResponse response = TodoResponse.fromEntity(todo);

            assertThat(response.isOverdue()).isFalse();
        }

        @Test
        @DisplayName("Does not mark future-due todo as overdue")
        void futureNotOverdue() {
            Todo todo = new Todo("t", "d", Priority.HIGH);
            todo.setStatus(TodoStatus.PENDING);
            todo.setDueDate(LocalDateTime.now().plusDays(1));

            TodoResponse response = TodoResponse.fromEntity(todo);

            assertThat(response.isOverdue()).isFalse();
        }

        @Test
        @DisplayName("Handles entity with null timestamps and due date")
        void handlesNulls() {
            Todo todo = new Todo("t", "d", Priority.MEDIUM);

            TodoResponse response = TodoResponse.fromEntity(todo);

            assertThat(response.getDueDate()).isNull();
            assertThat(response.getCreatedAt()).isNull();
            assertThat(response.getUpdatedAt()).isNull();
            assertThat(response.getCompletedAt()).isNull();
            assertThat(response.isOverdue()).isFalse();
        }
    }

    @Nested
    @DisplayName("TodoResponse setters/getters")
    class Accessors {

        @Test
        @DisplayName("Round-trips every property")
        void roundTrip() {
            TodoResponse response = new TodoResponse();
            response.setId(1L);
            response.setTitle("t");
            response.setDescription("d");
            response.setStatus(TodoStatus.COMPLETED);
            response.setPriority(Priority.LOW);
            response.setDueDate("2024-01-01 00:00");
            response.setCreatedAt("2024-01-01 00:01");
            response.setUpdatedAt("2024-01-01 00:02");
            response.setCompletedAt("2024-01-01 00:03");
            response.setOverdue(true);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("t");
            assertThat(response.getDescription()).isEqualTo("d");
            assertThat(response.getStatus()).isEqualTo(TodoStatus.COMPLETED);
            assertThat(response.getPriority()).isEqualTo(Priority.LOW);
            assertThat(response.getDueDate()).isEqualTo("2024-01-01 00:00");
            assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 00:01");
            assertThat(response.getUpdatedAt()).isEqualTo("2024-01-01 00:02");
            assertThat(response.getCompletedAt()).isEqualTo("2024-01-01 00:03");
            assertThat(response.isOverdue()).isTrue();
        }
    }

    @Nested
    @DisplayName("TodoRequest")
    class RequestTests {

        @Test
        @DisplayName("Default constructor initializes enums to defaults")
        void defaultConstructor() {
            TodoRequest request = new TodoRequest();
            assertThat(request.getPriority()).isEqualTo(Priority.MEDIUM);
            assertThat(request.getStatus()).isEqualTo(TodoStatus.PENDING);
        }

        @Test
        @DisplayName("Parameterized constructor populates all fields")
        void parameterizedConstructor() {
            TodoRequest request = new TodoRequest("t", "d", Priority.HIGH, TodoStatus.IN_PROGRESS, "2024-01-01T00:00");

            assertThat(request.getTitle()).isEqualTo("t");
            assertThat(request.getDescription()).isEqualTo("d");
            assertThat(request.getPriority()).isEqualTo(Priority.HIGH);
            assertThat(request.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
            assertThat(request.getDueDate()).isEqualTo("2024-01-01T00:00");
        }

        @Test
        @DisplayName("Setters round-trip all fields")
        void setters() {
            TodoRequest request = new TodoRequest();
            request.setTitle("t");
            request.setDescription("d");
            request.setPriority(Priority.CRITICAL);
            request.setStatus(TodoStatus.CANCELLED);
            request.setDueDate("2024-01-01T00:00");

            assertThat(request.getTitle()).isEqualTo("t");
            assertThat(request.getDescription()).isEqualTo("d");
            assertThat(request.getPriority()).isEqualTo(Priority.CRITICAL);
            assertThat(request.getStatus()).isEqualTo(TodoStatus.CANCELLED);
            assertThat(request.getDueDate()).isEqualTo("2024-01-01T00:00");
        }
    }
}
