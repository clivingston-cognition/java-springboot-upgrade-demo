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
    @DisplayName("Should set completedAt when status changes to COMPLETED on update")
    void shouldSetCompletedAtOnComplete() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);

        todo.onUpdate();

        assertThat(todo.getUpdatedAt()).isNotNull();
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should clear completedAt when status is not COMPLETED on update")
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
    @DisplayName("Should not overwrite completedAt if already set")
    void shouldNotOverwriteCompletedAtIfAlreadySet() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();
        LocalDateTime completedTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(completedTime);

        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isEqualTo(completedTime);
    }

    @Test
    @DisplayName("Should produce meaningful toString output")
    void shouldProduceToString() {
        Todo todo = new Todo("Test Todo", "Desc", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.PENDING);

        String result = todo.toString();

        assertThat(result).contains("Test Todo");
        assertThat(result).contains("PENDING");
        assertThat(result).contains("HIGH");
    }

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
        Todo todo = new Todo("Title", "Description", Priority.CRITICAL);
        assertThat(todo.getTitle()).isEqualTo("Title");
        assertThat(todo.getDescription()).isEqualTo("Description");
        assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    @DisplayName("Should set and get all fields via setters/getters")
    void shouldSetAndGetAllFields() {
        Todo todo = new Todo();
        LocalDateTime now = LocalDateTime.now();

        todo.setId(42L);
        todo.setTitle("My Title");
        todo.setDescription("My Desc");
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setPriority(Priority.LOW);
        todo.setDueDate(now);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now);

        assertThat(todo.getId()).isEqualTo(42L);
        assertThat(todo.getTitle()).isEqualTo("My Title");
        assertThat(todo.getDescription()).isEqualTo("My Desc");
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(todo.getPriority()).isEqualTo(Priority.LOW);
        assertThat(todo.getDueDate()).isEqualTo(now);
        assertThat(todo.getCreatedAt()).isEqualTo(now);
        assertThat(todo.getUpdatedAt()).isEqualTo(now);
        assertThat(todo.getCompletedAt()).isEqualTo(now);
    }
}
