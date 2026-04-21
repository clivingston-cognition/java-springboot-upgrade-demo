package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Model Tests")
class ModelTests {

    @Nested
    @DisplayName("Todo Entity")
    class TodoEntityTests {

        @Test
        @DisplayName("Default constructor produces an empty todo")
        void defaultConstructor() {
            Todo todo = new Todo();
            assertThat(todo.getId()).isNull();
            assertThat(todo.getTitle()).isNull();
            assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
            assertThat(todo.getPriority()).isEqualTo(Priority.MEDIUM);
        }

        @Test
        @DisplayName("Convenience constructor populates title, description, priority and defaults status")
        void convenienceConstructor() {
            Todo todo = new Todo("t", "d", Priority.HIGH);
            assertThat(todo.getTitle()).isEqualTo("t");
            assertThat(todo.getDescription()).isEqualTo("d");
            assertThat(todo.getPriority()).isEqualTo(Priority.HIGH);
            assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
        }

        @Test
        @DisplayName("Setters cover all fields")
        void settersAndGetters() {
            LocalDateTime now = LocalDateTime.now();
            Todo todo = new Todo();
            todo.setId(7L);
            todo.setTitle("title");
            todo.setDescription("desc");
            todo.setStatus(TodoStatus.IN_PROGRESS);
            todo.setPriority(Priority.CRITICAL);
            todo.setDueDate(now);
            todo.setCreatedAt(now);
            todo.setUpdatedAt(now);
            todo.setCompletedAt(now);

            assertThat(todo.getId()).isEqualTo(7L);
            assertThat(todo.getTitle()).isEqualTo("title");
            assertThat(todo.getDescription()).isEqualTo("desc");
            assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
            assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
            assertThat(todo.getDueDate()).isEqualTo(now);
            assertThat(todo.getCreatedAt()).isEqualTo(now);
            assertThat(todo.getUpdatedAt()).isEqualTo(now);
            assertThat(todo.getCompletedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("onCreate populates timestamps")
        void onCreateSetsTimestamps() throws Exception {
            Todo todo = new Todo("t", "d", Priority.LOW);

            Method onCreate = Todo.class.getDeclaredMethod("onCreate");
            onCreate.setAccessible(true);
            onCreate.invoke(todo);

            assertThat(todo.getCreatedAt()).isNotNull();
            assertThat(todo.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("onUpdate sets completedAt when status becomes COMPLETED")
        void onUpdateSetsCompletedAt() throws Exception {
            Todo todo = new Todo("t", "d", Priority.LOW);
            todo.setStatus(TodoStatus.COMPLETED);

            invokeOnUpdate(todo);

            assertThat(todo.getCompletedAt()).isNotNull();
            assertThat(todo.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("onUpdate preserves existing completedAt when already COMPLETED")
        void onUpdatePreservesCompletedAt() throws Exception {
            Todo todo = new Todo("t", "d", Priority.LOW);
            LocalDateTime original = LocalDateTime.of(2020, 1, 1, 0, 0);
            todo.setStatus(TodoStatus.COMPLETED);
            todo.setCompletedAt(original);

            invokeOnUpdate(todo);

            assertThat(todo.getCompletedAt()).isEqualTo(original);
        }

        @Test
        @DisplayName("onUpdate clears completedAt when status is not COMPLETED")
        void onUpdateClearsCompletedAt() throws Exception {
            Todo todo = new Todo("t", "d", Priority.LOW);
            todo.setStatus(TodoStatus.PENDING);
            todo.setCompletedAt(LocalDateTime.now());

            invokeOnUpdate(todo);

            assertThat(todo.getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("toString contains identifying fields")
        void toStringOutput() {
            Todo todo = new Todo("t", "d", Priority.LOW);
            todo.setId(1L);

            String output = todo.toString();

            assertThat(output)
                    .contains("Todo{")
                    .contains("id=1")
                    .contains("title='t'")
                    .contains("status=PENDING")
                    .contains("priority=LOW");
        }

        private void invokeOnUpdate(Todo todo) throws Exception {
            Method onUpdate = Todo.class.getDeclaredMethod("onUpdate");
            onUpdate.setAccessible(true);
            onUpdate.invoke(todo);
        }
    }

    @Nested
    @DisplayName("Enums")
    class EnumTests {

        @Test
        @DisplayName("Priority exposes display names")
        void priorityDisplay() {
            assertThat(Priority.LOW.getDisplayName()).isEqualTo("Low");
            assertThat(Priority.MEDIUM.getDisplayName()).isEqualTo("Medium");
            assertThat(Priority.HIGH.getDisplayName()).isEqualTo("High");
            assertThat(Priority.CRITICAL.getDisplayName()).isEqualTo("Critical");
            assertThat(Priority.values()).hasSize(4);
            assertThat(Priority.valueOf("HIGH")).isEqualTo(Priority.HIGH);
        }

        @Test
        @DisplayName("TodoStatus exposes display names")
        void todoStatusDisplay() {
            assertThat(TodoStatus.PENDING.getDisplayName()).isEqualTo("Pending");
            assertThat(TodoStatus.IN_PROGRESS.getDisplayName()).isEqualTo("In Progress");
            assertThat(TodoStatus.COMPLETED.getDisplayName()).isEqualTo("Completed");
            assertThat(TodoStatus.CANCELLED.getDisplayName()).isEqualTo("Cancelled");
            assertThat(TodoStatus.values()).hasSize(4);
            assertThat(TodoStatus.valueOf("PENDING")).isEqualTo(TodoStatus.PENDING);
        }
    }
}
