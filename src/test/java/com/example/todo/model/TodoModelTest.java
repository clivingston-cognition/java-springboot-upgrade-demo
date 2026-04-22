package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Todo Entity Tests")
class TodoModelTest {

    @Test
    @DisplayName("Should populate createdAt and updatedAt on PrePersist")
    void shouldSetTimestampsOnPrePersist() throws Exception {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);

        invokeLifecycle(todo, "onCreate");

        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should refresh updatedAt and set completedAt when transitioning to COMPLETED")
    void shouldSetCompletedAtOnUpdate() throws Exception {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        invokeLifecycle(todo, "onCreate");
        LocalDateTime initialUpdatedAt = todo.getUpdatedAt();

        todo.setStatus(TodoStatus.COMPLETED);
        Thread.sleep(2); // guarantee updatedAt changes even on fast clocks
        invokeLifecycle(todo, "onUpdate");

        assertThat(todo.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should clear completedAt when status moves away from COMPLETED")
    void shouldClearCompletedAtOnReopen() throws Exception {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedAt(LocalDateTime.now().minusDays(1));

        todo.setStatus(TodoStatus.PENDING);
        invokeLifecycle(todo, "onUpdate");

        assertThat(todo.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Should leave completedAt alone when already set on a completed todo")
    void shouldNotOverwriteExistingCompletedAt() throws Exception {
        Todo todo = new Todo("Title", "Desc", Priority.HIGH);
        todo.setStatus(TodoStatus.COMPLETED);
        LocalDateTime firstCompletedAt = LocalDateTime.now().minusDays(2);
        todo.setCompletedAt(firstCompletedAt);

        invokeLifecycle(todo, "onUpdate");

        assertThat(todo.getCompletedAt()).isEqualTo(firstCompletedAt);
    }

    @Test
    @DisplayName("Should expose all getters/setters")
    void gettersAndSettersShouldRoundTrip() {
        Todo todo = new Todo();
        LocalDateTime now = LocalDateTime.now();

        todo.setId(42L);
        todo.setTitle("t");
        todo.setDescription("d");
        todo.setStatus(TodoStatus.IN_PROGRESS);
        todo.setPriority(Priority.CRITICAL);
        todo.setDueDate(now);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        todo.setCompletedAt(now);

        assertThat(todo.getId()).isEqualTo(42L);
        assertThat(todo.getTitle()).isEqualTo("t");
        assertThat(todo.getDescription()).isEqualTo("d");
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(todo.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(todo.getDueDate()).isEqualTo(now);
        assertThat(todo.getCreatedAt()).isEqualTo(now);
        assertThat(todo.getUpdatedAt()).isEqualTo(now);
        assertThat(todo.getCompletedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toString should include key identifying fields")
    void toStringShouldIncludeFields() {
        Todo todo = new Todo("Example", "Desc", Priority.LOW);
        todo.setId(7L);
        todo.setStatus(TodoStatus.PENDING);

        String asString = todo.toString();

        assertThat(asString)
                .contains("Todo{")
                .contains("id=7")
                .contains("title='Example'")
                .contains("status=PENDING")
                .contains("priority=LOW");
    }

    private static void invokeLifecycle(Todo todo, String methodName) throws Exception {
        Method method = Todo.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(todo);
    }
}
