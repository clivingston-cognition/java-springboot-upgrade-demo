package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Todo Entity Tests")
class TodoTest {

    @Test
    @DisplayName("Constructor should set fields and default status to PENDING")
    void constructorShouldSetFieldsAndDefaultStatus() {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);

        assertThat(todo.getTitle()).isEqualTo("Title");
        assertThat(todo.getDescription()).isEqualTo("Desc");
        assertThat(todo.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    @DisplayName("Default constructor should use default enum values")
    void defaultConstructor() {
        Todo todo = new Todo();

        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(todo.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    @DisplayName("onCreate should set createdAt and updatedAt")
    void onCreateShouldSetTimestamps() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();

        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("onUpdate should set updatedAt")
    void onUpdateShouldSetUpdatedAt() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();
        LocalDateTime originalUpdatedAt = todo.getUpdatedAt();

        todo.onUpdate();

        assertThat(todo.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    @DisplayName("onUpdate should set completedAt when status is COMPLETED and completedAt is null")
    void onUpdateShouldSetCompletedAtWhenCompleted() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);

        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("onUpdate should not overwrite existing completedAt when status is COMPLETED")
    void onUpdateShouldNotOverwriteCompletedAt() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);
        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        todo.setCompletedAt(fixedTime);

        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isEqualTo(fixedTime);
    }

    @Test
    @DisplayName("onUpdate should clear completedAt when status is not COMPLETED")
    void onUpdateShouldClearCompletedAtWhenNotCompleted() {
        Todo todo = new Todo("Test", "Desc", Priority.LOW);
        todo.onCreate();
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(LocalDateTime.now());
        todo.setStatus(TodoStatus.PENDING);

        todo.onUpdate();

        assertThat(todo.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Getters and setters should work correctly")
    void gettersAndSetters() {
        Todo todo = new Todo();
        todo.setId(42L);
        todo.setTitle("Title");
        todo.setDescription("Desc");
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setPriority(Priority.CRITICAL);
        LocalDateTime now = LocalDateTime.now();
        todo.setDueDate(now);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now);

        assertThat(todo.getId()).isEqualTo(42L);
        assertThat(todo.getTitle()).isEqualTo("Title");
        assertThat(todo.getDescription()).isEqualTo("Desc");
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(todo.getDueDate()).isEqualTo(now);
        assertThat(todo.getCreatedAt()).isEqualTo(now);
        assertThat(todo.getUpdatedAt()).isEqualTo(now);
        assertThat(todo.getCompletedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toString should contain key fields")
    void toStringShouldContainKeyFields() {
        Todo todo = new Todo("My Task", "Description", Priority.HIGH);
        todo.setId(1L);

        String result = todo.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("title='My Task'");
        assertThat(result).contains("priority=HIGH");
    }
}
