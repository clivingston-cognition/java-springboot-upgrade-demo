package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo sampleTodo;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo("Test Todo", "Test Description", Priority.MEDIUM, LocalDate.now().plusDays(7));
        sampleTodo.setId(1L);
        sampleTodo.setStatus(TodoStatus.PENDING);
    }

    @Test
    void findAll_returnsTodosOrderedByCreatedAtDesc() {
        when(todoRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleTodo));

        List<Todo> result = todoService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Todo");
    }

    @Test
    void findById_existingId_returnsTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        Todo result = todoService.findById(1L);

        assertThat(result.getTitle()).isEqualTo("Test Todo");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findById(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_validRequest_savesAndReturnsTodo() {
        TodoRequest request = new TodoRequest("New Todo", "Description", TodoStatus.PENDING, Priority.HIGH, LocalDate.now());
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        Todo result = todoService.create(request);

        assertThat(result).isNotNull();
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void create_requestWithoutStatusAndPriority_usesDefaults() {
        TodoRequest request = new TodoRequest();
        request.setTitle("Minimal Todo");
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo result = todoService.create(request);

        assertThat(result.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    void update_existingTodo_updatesFields() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        TodoRequest request = new TodoRequest("Updated Title", "Updated Desc", TodoStatus.IN_PROGRESS, Priority.HIGH, LocalDate.now().plusDays(3));
        Todo result = todoService.update(1L, request);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void toggleComplete_pendingTodo_becomesCompleted() {
        sampleTodo.setStatus(TodoStatus.PENDING);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo result = todoService.toggleComplete(1L);

        assertThat(result.getStatus()).isEqualTo(TodoStatus.COMPLETED);
    }

    @Test
    void toggleComplete_completedTodo_becomesPending() {
        sampleTodo.setStatus(TodoStatus.COMPLETED);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo result = todoService.toggleComplete(1L);

        assertThat(result.getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    void delete_existingTodo_deletesSuccessfully() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        todoService.delete(1L);

        verify(todoRepository).delete(sampleTodo);
    }

    @Test
    void delete_nonExistingTodo_throwsException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.delete(99L))
                .isInstanceOf(TodoNotFoundException.class);
    }
}
