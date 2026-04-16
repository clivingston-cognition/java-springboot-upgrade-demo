package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Todo Entity Tests")
class TodoTest {

    @Test
    @DisplayName("Should set createdAt and updatedAt on create")
    void onCreateShouldSetTimestamps() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();

        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should set completedAt when status is COMPLETED on update")
    void onUpdateShouldSetCompletedAtWhenCompleted() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);

        todo.onUpdate();

        assertThat(todo.getUpdatedAt()).isNotNull();
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should clear completedAt when status is not COMPLETED on update")
    void onUpdateShouldClearCompletedAtWhenNotCompleted() {
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
    void onUpdateShouldNotOverwriteCompletedAt() {
        Todo todo = new Todo("Test", "Desc", Priority.MEDIUM);
        todo.onCreate();
        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(fixedTime);

        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isEqualTo(fixedTime);
    }

    @Test
    @DisplayName("Should produce correct toString")
    void toStringShouldContainFields() {
        Todo todo = new Todo("Test Todo", "Desc", Priority.HIGH);
        todo.setId(1L);
        todo.setStatus(TodoStatus.IN_PROGRESS);

        String result = todo.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("title='Test Todo'");
        assertThat(result).contains("status=IN_PROGRESS");
        assertThat(result).contains("priority=HIGH");
    }

    @Test
    @DisplayName("Default constructor should set defaults")
    void defaultConstructorDefaults() {
        Todo todo = new Todo();

        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(todo.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    @DisplayName("Parameterized constructor sets fields")
    void parameterizedConstructor() {
        Todo todo = new Todo("Title", "Desc", Priority.CRITICAL);

        assertThat(todo.getTitle()).isEqualTo("Title");
        assertThat(todo.getDescription()).isEqualTo("Desc");
        assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    @DisplayName("Setters and getters work correctly")
    void settersAndGetters() {
        Todo todo = new Todo();
        LocalDateTime now = LocalDateTime.now();

        todo.setId(5L);
        todo.setTitle("Title");
        todo.setDescription("Desc");
        todo.setStatus(TodoStatus.CANCELLED);
        todo.setPriority(Priority.LOW);
        todo.setDueDate(now);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now);

        assertThat(todo.getId()).isEqualTo(5L);
        assertThat(todo.getTitle()).isEqualTo("Title");
        assertThat(todo.getDescription()).isEqualTo("Desc");
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.CANCELLED);
        assertThat(todo.getPriority()).isEqualTo(Priority.LOW);
        assertThat(todo.getDueDate()).isEqualTo(now);
        assertThat(todo.getCreatedAt()).isEqualTo(now);
        assertThat(todo.getUpdatedAt()).isEqualTo(now);
        assertThat(todo.getCompletedAt()).isEqualTo(now);
    }
}
