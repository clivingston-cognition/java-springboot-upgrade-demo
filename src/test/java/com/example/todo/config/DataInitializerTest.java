package com.example.todo.config;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializer Tests")
class DataInitializerTest {

    @InjectMocks
    private DataInitializer dataInitializer;

    @Mock
    private TodoRepository todoRepository;

    @Test
    @DisplayName("Should initialize sample data when repository is empty")
    void shouldInitializeDataWhenEmpty() throws Exception {
        when(todoRepository.count()).thenReturn(0L).thenReturn(6L);

        CommandLineRunner runner = dataInitializer.initData(todoRepository);
        assertThat(runner).isNotNull();
        runner.run();

        verify(todoRepository, times(6)).save(any(Todo.class));
    }

    @Test
    @DisplayName("Should not initialize data when repository already has data")
    void shouldNotInitializeWhenNotEmpty() throws Exception {
        when(todoRepository.count()).thenReturn(5L);

        CommandLineRunner runner = dataInitializer.initData(todoRepository);
        runner.run();

        verify(todoRepository, never()).save(any(Todo.class));
    }
}
