package com.example.todo.model;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Model Tests")
class ModelTests {

    @Test
    @DisplayName("Priority enum exposes display names for all values")
    void priorityDisplayNames() {
        assertThat(Priority.LOW.getDisplayName()).isEqualTo("Low");
        assertThat(Priority.MEDIUM.getDisplayName()).isEqualTo("Medium");
        assertThat(Priority.HIGH.getDisplayName()).isEqualTo("High");
        assertThat(Priority.CRITICAL.getDisplayName()).isEqualTo("Critical");
        assertThat(Priority.valueOf("HIGH")).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("TodoStatus enum exposes display names for all values")
    void todoStatusDisplayNames() {
        assertThat(TodoStatus.PENDING.getDisplayName()).isEqualTo("Pending");
        assertThat(TodoStatus.IN_PROGRESS.getDisplayName()).isEqualTo("In Progress");
        assertThat(TodoStatus.COMPLETED.getDisplayName()).isEqualTo("Completed");
        assertThat(TodoStatus.CANCELLED.getDisplayName()).isEqualTo("Cancelled");
        assertThat(TodoStatus.valueOf("PENDING")).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    @DisplayName("Todo no-arg constructor initializes defaults")
    void todoNoArgDefaults() {
        Todo todo = new Todo();

        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(todo.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(todo.getId()).isNull();
        assertThat(todo.getTitle()).isNull();
    }

    @Test
    @DisplayName("Todo constructor and setters populate all fields and toString renders them")
    void todoGettersSettersAndToString() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 10, 0);
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        todo.setId(42L);
        todo.setDescription("New desc");
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setPriority(Priority.CRITICAL);
        todo.setDueDate(now.plusDays(1));
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now.plusDays(2));
        todo.setTitle("New Title");

        assertThat(todo.getId()).isEqualTo(42L);
        assertThat(todo.getTitle()).isEqualTo("New Title");
        assertThat(todo.getDescription()).isEqualTo("New desc");
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(todo.getDueDate()).isEqualTo(now.plusDays(1));
        assertThat(todo.getCreatedAt()).isEqualTo(now);
        assertThat(todo.getUpdatedAt()).isEqualTo(now);
        assertThat(todo.getCompletedAt()).isEqualTo(now.plusDays(2));

        String str = todo.toString();
        assertThat(str)
                .contains("id=42")
                .contains("title='New Title'")
                .contains("status=IN_PROGRESS")
                .contains("priority=CRITICAL");
    }

    @Test
    @DisplayName("Todo @PrePersist sets createdAt and updatedAt")
    void todoOnCreateCallback() throws Exception {
        Todo todo = new Todo();
        Method onCreate = Todo.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);

        onCreate.invoke(todo);

        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Todo @PreUpdate sets completedAt when status becomes COMPLETED")
    void todoOnUpdateSetsCompletedAt() throws Exception {
        Todo todo = new Todo("T", "D", Priority.LOW);
        todo.setStatus(TodoStatus.COMPLETED);
        Method onUpdate = Todo.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);

        onUpdate.invoke(todo);

        assertThat(todo.getUpdatedAt()).isNotNull();
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Todo @PreUpdate clears completedAt when status is not COMPLETED")
    void todoOnUpdateClearsCompletedAt() throws Exception {
        Todo todo = new Todo("T", "D", Priority.LOW);
        todo.setStatus(TodoStatus.PENDING);
        todo.setCompletedAt(LocalDateTime.now());
        Method onUpdate = Todo.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);

        onUpdate.invoke(todo);

        assertThat(todo.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Todo @PreUpdate keeps existing completedAt when status is COMPLETED and completedAt already set")
    void todoOnUpdateKeepsCompletedAt() throws Exception {
        Todo todo = new Todo("T", "D", Priority.LOW);
        todo.setStatus(TodoStatus.COMPLETED);
        LocalDateTime original = LocalDateTime.of(2020, 1, 1, 0, 0);
        todo.setCompletedAt(original);
        Method onUpdate = Todo.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);

        onUpdate.invoke(todo);

        assertThat(todo.getCompletedAt()).isEqualTo(original);
    }

    @Test
    @DisplayName("TodoRequest getters, setters and all-args constructor work")
    void todoRequestAllFields() {
        TodoRequest request = new TodoRequest("t", "d", Priority.HIGH, TodoStatus.IN_PROGRESS, "2024-01-01T10:00");
        assertThat(request.getTitle()).isEqualTo("t");
        assertThat(request.getDescription()).isEqualTo("d");
        assertThat(request.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(request.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(request.getDueDate()).isEqualTo("2024-01-01T10:00");

        TodoRequest blank = new TodoRequest();
        blank.setTitle("x");
        blank.setDescription("y");
        blank.setPriority(Priority.LOW);
        blank.setStatus(TodoStatus.CANCELLED);
        blank.setDueDate("2025-06-01T12:30");
        assertThat(blank.getTitle()).isEqualTo("x");
        assertThat(blank.getDescription()).isEqualTo("y");
        assertThat(blank.getPriority()).isEqualTo(Priority.LOW);
        assertThat(blank.getStatus()).isEqualTo(TodoStatus.CANCELLED);
        assertThat(blank.getDueDate()).isEqualTo("2025-06-01T12:30");
    }

    @Test
    @DisplayName("TodoResponse.fromEntity maps all fields and detects overdue")
    void todoResponseFromEntityOverdue() {
        Todo entity = new Todo("Title", "Desc", Priority.HIGH);
        entity.setId(3L);
        entity.setStatus(TodoStatus.PENDING);
        entity.setDueDate(LocalDateTime.now().minusDays(1));
        entity.setCreatedAt(LocalDateTime.now().minusDays(5));
        entity.setUpdatedAt(LocalDateTime.now().minusDays(4));

        TodoResponse response = TodoResponse.fromEntity(entity);

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getDescription()).isEqualTo("Desc");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(response.getDueDate()).isNotNull();
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("TodoResponse.fromEntity does not mark COMPLETED past-due todos as overdue")
    void todoResponseCompletedNotOverdue() {
        Todo entity = new Todo("Title", "Desc", Priority.HIGH);
        entity.setStatus(TodoStatus.COMPLETED);
        entity.setDueDate(LocalDateTime.now().minusDays(1));
        entity.setCompletedAt(LocalDateTime.now().minusHours(2));

        TodoResponse response = TodoResponse.fromEntity(entity);

        assertThat(response.isOverdue()).isFalse();
        assertThat(response.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("TodoResponse.fromEntity does not mark CANCELLED past-due todos as overdue")
    void todoResponseCancelledNotOverdue() {
        Todo entity = new Todo("Title", "Desc", Priority.HIGH);
        entity.setStatus(TodoStatus.CANCELLED);
        entity.setDueDate(LocalDateTime.now().minusDays(1));

        TodoResponse response = TodoResponse.fromEntity(entity);

        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("TodoResponse.fromEntity handles null due date and timestamps")
    void todoResponseNulls() {
        Todo entity = new Todo("Title", "Desc", Priority.LOW);
        entity.setStatus(TodoStatus.PENDING);

        TodoResponse response = TodoResponse.fromEntity(entity);

        assertThat(response.getDueDate()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("TodoResponse getters/setters work independently of fromEntity")
    void todoResponseSetters() {
        TodoResponse response = new TodoResponse();
        response.setId(9L);
        response.setTitle("t");
        response.setDescription("d");
        response.setStatus(TodoStatus.IN_PROGRESS);
        response.setPriority(Priority.CRITICAL);
        response.setDueDate("2024-01-01 10:00");
        response.setCreatedAt("2024-01-01 09:00");
        response.setUpdatedAt("2024-01-01 09:30");
        response.setCompletedAt("2024-01-01 11:00");
        response.setOverdue(true);

        assertThat(response.getId()).isEqualTo(9L);
        assertThat(response.getTitle()).isEqualTo("t");
        assertThat(response.getDescription()).isEqualTo("d");
        assertThat(response.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(response.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(response.getDueDate()).isEqualTo("2024-01-01 10:00");
        assertThat(response.getCreatedAt()).isEqualTo("2024-01-01 09:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-01-01 09:30");
        assertThat(response.getCompletedAt()).isEqualTo("2024-01-01 11:00");
        assertThat(response.isOverdue()).isTrue();
    }
}
