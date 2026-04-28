package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Todo Entity Tests")
class TodoTest {

    @Test
    @DisplayName("Should create todo with constructor")
    void shouldCreateTodoWithConstructor() {
        Todo todo = new Todo("Title", "Description", Priority.HIGH);

        assertThat(todo.getTitle()).isEqualTo("Title");
        assertThat(todo.getDescription()).isEqualTo("Description");
        assertThat(todo.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    @DisplayName("Should set timestamps on create")
    void shouldSetTimestampsOnCreate() {
        Todo todo = new Todo("Title", "Desc", Priority.MEDIUM);
        todo.onCreate();

        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should set completedAt on update when status is COMPLETED")
    void shouldSetCompletedAtOnUpdate() {
        Todo todo = new Todo("Title", "Desc", Priority.MEDIUM);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.onUpdate();

        assertThat(todo.getUpdatedAt()).isNotNull();
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should clear completedAt on update when status is not COMPLETED")
    void shouldClearCompletedAtOnUpdate() {
        Todo todo = new Todo("Title", "Desc", Priority.MEDIUM);
        todo.setCompletedAt(LocalDateTime.now());
        todo.setStatus(TodoStatus.PENDING);
        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Should not overwrite completedAt if already set and status is COMPLETED")
    void shouldNotOverwriteCompletedAt() {
        LocalDateTime existingCompletedAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        Todo todo = new Todo("Title", "Desc", Priority.MEDIUM);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(existingCompletedAt);
        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isEqualTo(existingCompletedAt);
    }

    @Test
    @DisplayName("Should generate toString")
    void shouldGenerateToString() {
        Todo todo = new Todo("Title", "Desc", Priority.LOW);
        todo.setId(1L);
        String str = todo.toString();

        assertThat(str).contains("id=1");
        assertThat(str).contains("title='Title'");
        assertThat(str).contains("status=PENDING");
        assertThat(str).contains("priority=LOW");
    }

    @Test
    @DisplayName("Should use default constructor")
    void shouldUseDefaultConstructor() {
        Todo todo = new Todo();
        assertThat(todo.getId()).isNull();
        assertThat(todo.getTitle()).isNull();
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        Todo todo = new Todo();
        LocalDateTime now = LocalDateTime.now();

        todo.setId(42L);
        todo.setTitle("Test");
        todo.setDescription("Desc");
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setPriority(Priority.CRITICAL);
        todo.setDueDate(now);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now);

        assertThat(todo.getId()).isEqualTo(42L);
        assertThat(todo.getTitle()).isEqualTo("Test");
        assertThat(todo.getDescription()).isEqualTo("Desc");
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(todo.getDueDate()).isEqualTo(now);
        assertThat(todo.getCreatedAt()).isEqualTo(now);
        assertThat(todo.getUpdatedAt()).isEqualTo(now);
        assertThat(todo.getCompletedAt()).isEqualTo(now);
    }
}
