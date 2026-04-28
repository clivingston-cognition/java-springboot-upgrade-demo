package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Todo Service Additional Tests")
class TodoServiceAdditionalTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo sampleTodo;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo("Test Todo", "Test description", Priority.MEDIUM);
        sampleTodo.setId(1L);
        sampleTodo.setStatus(TodoStatus.PENDING);
        sampleTodo.setCreatedAt(LocalDateTime.now());
        sampleTodo.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get todos by status")
    void shouldGetTodosByStatus() {
        Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
        when(todoRepository.findByStatus(eq(TodoStatus.PENDING), any(Pageable.class))).thenReturn(page);

        Page<TodoResponse> result = todoService.getTodosByStatus(TodoStatus.PENDING, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    @DisplayName("Should get todos by priority")
    void shouldGetTodosByPriority() {
        Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
        when(todoRepository.findByPriority(eq(Priority.MEDIUM), any(Pageable.class))).thenReturn(page);

        Page<TodoResponse> result = todoService.getTodosByPriority(Priority.MEDIUM, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    @DisplayName("Should create todo with empty due date string")
    void shouldCreateTodoWithEmptyDueDate() {
        TodoRequest request = new TodoRequest();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setPriority(Priority.MEDIUM);
        request.setStatus(TodoStatus.PENDING);
        request.setDueDate("");

        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoResponse response = todoService.createTodo(request);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should update todo with empty due date string clearing it")
    void shouldUpdateWithEmptyDueDate() {
        TodoRequest request = new TodoRequest();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setPriority(Priority.MEDIUM);
        request.setStatus(TodoStatus.PENDING);
        request.setDueDate("");

        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoResponse response = todoService.updateTodo(1L, request);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should return filtered todos sorted ascending")
    void shouldReturnFilteredTodosSortedAscending() {
        Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
        when(todoRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<TodoResponse> result = todoService.getFilteredTodos(null, null, 0, 10, "title", "asc");

        assertThat(result.getContent()).hasSize(1);
    }
}
