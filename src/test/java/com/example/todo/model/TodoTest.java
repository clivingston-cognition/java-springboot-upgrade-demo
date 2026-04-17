package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Todo Entity Tests")
class TodoTest {

    @Test
    @DisplayName("Should set createdAt and updatedAt on persist")
    void shouldSetTimestampsOnCreate() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();

        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update updatedAt and set completedAt when status is COMPLETED")
    void shouldSetCompletedAtOnUpdate() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);

        todo.onUpdate();

        assertThat(todo.getUpdatedAt()).isNotNull();
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should clear completedAt when status is not COMPLETED")
    void shouldClearCompletedAtWhenNotCompleted() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(LocalDateTime.now());

        todo.setStatus(TodoStatus.PENDING);
        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Should not overwrite completedAt if already set and status is COMPLETED")
    void shouldNotOverwriteCompletedAt() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();
        LocalDateTime existingCompletedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(existingCompletedAt);

        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isEqualTo(existingCompletedAt);
    }

    @Test
    @DisplayName("Should produce correct toString output")
    void shouldProduceCorrectToString() {
        Todo todo = new Todo("Test Todo", "Desc", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.PENDING);

        String result = todo.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("title='Test Todo'");
        assertThat(result).contains("status=PENDING");
        assertThat(result).contains("priority=HIGH");
    }

    @Test
    @DisplayName("Default constructor should create todo with defaults")
    void defaultConstructorShouldWork() {
        Todo todo = new Todo();
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(todo.getPriority()).isEqualTo(Priority.MEDIUM);
    }
}
