package com.example.todo.config;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializer Tests")
class DataInitializerTest {

    @Mock
    private TodoRepository todoRepository;

    @Test
    @DisplayName("Should initialize sample data when repository is empty")
    void shouldInitDataWhenEmpty() throws Exception {
        when(todoRepository.count()).thenReturn(0L).thenReturn(6L);
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DataInitializer initializer = new DataInitializer();
        CommandLineRunner runner = initializer.initData(todoRepository);

        assertThat(runner).isNotNull();
        runner.run();

        verify(todoRepository, times(6)).save(any(Todo.class));
    }

    @Test
    @DisplayName("Should skip initialization when repository has data")
    void shouldSkipWhenNotEmpty() throws Exception {
        when(todoRepository.count()).thenReturn(5L);

        DataInitializer initializer = new DataInitializer();
        CommandLineRunner runner = initializer.initData(todoRepository);

        assertThat(runner).isNotNull();
        runner.run();

        verify(todoRepository, never()).save(any(Todo.class));
    }
}
