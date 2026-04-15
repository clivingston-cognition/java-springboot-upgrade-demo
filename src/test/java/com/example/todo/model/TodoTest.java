package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Todo Model Tests")
class TodoTest {

    @Test
    @DisplayName("Default constructor sets defaults")
    void defaultConstructor() {
        Todo todo = new Todo();
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(todo.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    @DisplayName("Parameterized constructor sets fields")
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
        Todo todo = new Todo("Title", "Desc", Priority.LOW);
        todo.onCreate();
        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("onUpdate sets completedAt when status is COMPLETED")
    void onUpdateSetsCompletedAtWhenCompleted() {
        Todo todo = new Todo("Title", "Desc", Priority.LOW);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);
        todo.onUpdate();
        assertThat(todo.getCompletedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("onUpdate clears completedAt when status is not COMPLETED")
    void onUpdateClearsCompletedAtWhenNotCompleted() {
        Todo todo = new Todo("Title", "Desc", Priority.LOW);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(LocalDateTime.now());
        todo.onUpdate();
        assertThat(todo.getCompletedAt()).isNotNull();

        // Now set back to PENDING
        todo.setStatus(TodoStatus.PENDING);
        todo.onUpdate();
        assertThat(todo.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("onUpdate does not overwrite completedAt if already set")
    void onUpdateDoesNotOverwriteCompletedAt() {
        Todo todo = new Todo("Title", "Desc", Priority.LOW);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);
        LocalDateTime fixed = LocalDateTime.of(2024, 1, 1, 12, 0);
        todo.setCompletedAt(fixed);
        todo.onUpdate();
        assertThat(todo.getCompletedAt()).isEqualTo(fixed);
    }

    @Test
    @DisplayName("toString contains expected fields")
    void toStringContainsFields() {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        todo.setId(1L);
        String result = todo.toString();
        assertThat(result).contains("id=1");
        assertThat(result).contains("title='Title'");
        assertThat(result).contains("priority=HIGH");
        assertThat(result).contains("status=PENDING");
    }

    @Test
    @DisplayName("Getters and setters work correctly")
    void gettersAndSetters() {
        Todo todo = new Todo();
        todo.setId(42L);
        todo.setTitle("My Task");
        todo.setDescription("Details");
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setPriority(Priority.CRITICAL);
        LocalDateTime now = LocalDateTime.now();
        todo.setDueDate(now);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now);

        assertThat(todo.getId()).isEqualTo(42L);
        assertThat(todo.getTitle()).isEqualTo("My Task");
        assertThat(todo.getDescription()).isEqualTo("Details");
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(todo.getDueDate()).isEqualTo(now);
        assertThat(todo.getCreatedAt()).isEqualTo(now);
        assertThat(todo.getUpdatedAt()).isEqualTo(now);
        assertThat(todo.getCompletedAt()).isEqualTo(now);
    }
}
