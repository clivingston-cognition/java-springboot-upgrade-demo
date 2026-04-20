package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Todo Entity Tests")
class TodoTest {

    @Test
    @DisplayName("Default constructor creates todo with default status and priority")
    void defaultConstructor() {
        Todo todo = new Todo();
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(todo.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void parameterizedConstructor() {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        assertThat(todo.getTitle()).isEqualTo("Title");
        assertThat(todo.getDescription()).isEqualTo("Desc");
        assertThat(todo.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    @DisplayName("onCreate sets createdAt and updatedAt")
    void onCreateSetsTimestamps() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();
        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("onUpdate sets updatedAt and completedAt when status is COMPLETED")
    void onUpdateSetsCompletedAtWhenCompleted() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);
        todo.onUpdate();

        assertThat(todo.getUpdatedAt()).isNotNull();
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("onUpdate clears completedAt when status is not COMPLETED")
    void onUpdateClearsCompletedAtWhenNotCompleted() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(LocalDateTime.now());
        todo.setStatus(TodoStatus.PENDING);
        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("onUpdate does not overwrite existing completedAt for COMPLETED status")
    void onUpdateDoesNotOverwriteCompletedAt() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();
        LocalDateTime existing = LocalDateTime.of(2024, 1, 1, 12, 0);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(existing);
        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isEqualTo(existing);
    }

    @Test
    @DisplayName("toString contains expected fields")
    void toStringContainsExpectedFields() {
        Todo todo = new Todo("My Task", "Some desc", Priority.HIGH);
        todo.setId(42L);
        todo.setStatus(TodoStatus.IN_PROGRESS);

        String result = todo.toString();

        assertThat(result).contains("id=42");
        assertThat(result).contains("title='My Task'");
        assertThat(result).contains("status=IN_PROGRESS");
        assertThat(result).contains("priority=HIGH");
    }

    @Test
    @DisplayName("Getters and setters work for all fields")
    void gettersAndSetters() {
        Todo todo = new Todo();
        LocalDateTime now = LocalDateTime.now();

        todo.setId(1L);
        todo.setTitle("Title");
        todo.setDescription("Desc");
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setPriority(Priority.CRITICAL);
        todo.setDueDate(now);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now);

        assertThat(todo.getId()).isEqualTo(1L);
        assertThat(todo.getTitle()).isEqualTo("Title");
        assertThat(todo.getDescription()).isEqualTo("Desc");
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(todo.getDueDate()).isEqualTo(now);
        assertThat(todo.getCreatedAt()).isEqualTo(now);
        assertThat(todo.getUpdatedAt()).isEqualTo(now);
        assertThat(todo.getCompletedAt()).isEqualTo(now);
    }
}
