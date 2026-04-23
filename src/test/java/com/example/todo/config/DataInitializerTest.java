package com.example.todo.config;

import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Data Initializer Tests")
class DataInitializerTest {

    @Test
    @DisplayName("Should seed six sample todos when repository is empty")
    void shouldSeedSampleData() throws Exception {
        TodoRepository repository = mock(TodoRepository.class);
        when(repository.count()).thenReturn(0L, 6L);

        CommandLineRunner runner = new DataInitializer().initData(repository);
        runner.run();

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(repository, times(6)).save(captor.capture());
        List<Todo> saved = captor.getAllValues();
        assertThat(saved).extracting(Todo::getTitle).contains(
                "Set up CI/CD pipeline",
                "Write unit tests",
                "Update API documentation",
                "Review pull requests",
                "Database backup strategy",
                "Refactor logging");
        assertThat(saved).anyMatch(t -> t.getStatus() == TodoStatus.COMPLETED);
        assertThat(saved).anyMatch(t -> t.getStatus() == TodoStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should not seed data when repository already has todos")
    void shouldNotSeedWhenDataExists() throws Exception {
        TodoRepository repository = mock(TodoRepository.class);
        when(repository.count()).thenReturn(5L);

        CommandLineRunner runner = new DataInitializer().initData(repository);
        runner.run();

        verify(repository, never()).save(any(Todo.class));
    }
}
