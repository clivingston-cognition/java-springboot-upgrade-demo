package com.example.todo.config;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Data Initializer Tests")
class DataInitializerTest {

    private final DataInitializer initializer = new DataInitializer();

    @Test
    @DisplayName("Should seed sample todos when repository is empty")
    void seedsTodosWhenEmpty() throws Exception {
        TodoRepository repo = mock(TodoRepository.class);
        when(repo.count()).thenReturn(0L, 6L);

        CommandLineRunner runner = initializer.initData(repo);
        runner.run();

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(repo, org.mockito.Mockito.times(6)).save(captor.capture());

        List<Todo> saved = captor.getAllValues();
        assertThat(saved).hasSize(6);
        assertThat(saved).extracting(Todo::getTitle).contains("Set up CI/CD pipeline", "Write unit tests");
    }

    @Test
    @DisplayName("Should skip seeding when repository already has data")
    void skipsWhenNotEmpty() throws Exception {
        TodoRepository repo = mock(TodoRepository.class);
        when(repo.count()).thenReturn(10L);

        CommandLineRunner runner = initializer.initData(repo);
        runner.run();

        verify(repo, never()).save(org.mockito.ArgumentMatchers.any(Todo.class));
    }
}
