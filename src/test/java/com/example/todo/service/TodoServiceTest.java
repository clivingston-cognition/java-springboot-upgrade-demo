package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Todo Service Tests")
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo sampleTodo;
    private TodoRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo("Test Todo", "Test description", Priority.MEDIUM);
        sampleTodo.setId(1L);
        sampleTodo.setStatus(TodoStatus.PENDING);
        sampleTodo.setCreatedAt(LocalDateTime.now());
        sampleTodo.setUpdatedAt(LocalDateTime.now());

        sampleRequest = new TodoRequest();
        sampleRequest.setTitle("Test Todo");
        sampleRequest.setDescription("Test description");
        sampleRequest.setPriority(Priority.MEDIUM);
        sampleRequest.setStatus(TodoStatus.PENDING);
    }

    @Nested
    @DisplayName("Create Todo")
    class CreateTodoTests {

        @Test
        @DisplayName("Should create a new todo with default status")
        void shouldCreateTodoWithDefaultStatus() {
            when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

            TodoResponse response = todoService.createTodo(sampleRequest);

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Test Todo");
            assertThat(response.getDescription()).isEqualTo("Test description");
            assertThat(response.getPriority()).isEqualTo(Priority.MEDIUM);
            verify(todoRepository).save(any(Todo.class));
        }

        @Test
        @DisplayName("Should create todo with due date")
        void shouldCreateTodoWithDueDate() {
            sampleRequest.setDueDate("2024-12-31T23:59");
            sampleTodo.setDueDate(LocalDateTime.of(2024, 12, 31, 23, 59));
            when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

            TodoResponse response = todoService.createTodo(sampleRequest);

            assertThat(response).isNotNull();
            assertThat(response.getDueDate()).isNotNull();
            verify(todoRepository).save(any(Todo.class));
        }

        @Test
        @DisplayName("Should create todo with explicit status")
        void shouldCreateTodoWithExplicitStatus() {
            sampleRequest.setStatus(TodoStatus.IN_PROGRESS);
            sampleTodo.setStatus(TodoStatus.IN_PROGRESS);
            when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

            TodoResponse response = todoService.createTodo(sampleRequest);

            assertThat(response.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should create todo with null status defaulting to PENDING")
        void shouldDefaultToPendingWhenStatusNull() {
            sampleRequest.setStatus(null);
            when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

            TodoResponse response = todoService.createTodo(sampleRequest);

            assertThat(response.getStatus()).isEqualTo(TodoStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("Get Todo By ID")
    class GetTodoByIdTests {

        @Test
        @DisplayName("Should return todo when found")
        void shouldReturnTodoWhenFound() {
            when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

            TodoResponse response = todoService.getTodoById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("Test Todo");
        }

        @Test
        @DisplayName("Should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(todoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.getTodoById(999L))
                    .isInstanceOf(TodoNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("Get All Todos")
    class GetAllTodosTests {

        @Test
        @DisplayName("Should return paginated todos sorted by default")
        void shouldReturnPaginatedTodos() {
            Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
            when(todoRepository.findAll(any(Pageable.class))).thenReturn(page);

            Page<TodoResponse> result = todoService.getAllTodos(0, 10, "createdAt", "desc");

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Todo");
        }

        @Test
        @DisplayName("Should return todos sorted ascending")
        void shouldReturnTodosSortedAscending() {
            Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
            when(todoRepository.findAll(any(Pageable.class))).thenReturn(page);

            Page<TodoResponse> result = todoService.getAllTodos(0, 10, "title", "asc");

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Update Todo")
    class UpdateTodoTests {

        @Test
        @DisplayName("Should update all fields of an existing todo")
        void shouldUpdateAllFields() {
            TodoRequest updateRequest = new TodoRequest();
            updateRequest.setTitle("Updated Title");
            updateRequest.setDescription("Updated description");
            updateRequest.setPriority(Priority.HIGH);
            updateRequest.setStatus(TodoStatus.IN_PROGRESS);
            updateRequest.setDueDate("2024-12-31T23:59");

            Todo updatedTodo = new Todo("Updated Title", "Updated description", Priority.HIGH);
            updatedTodo.setId(1L);
            updatedTodo.setStatus(TodoStatus.IN_PROGRESS);
            updatedTodo.setDueDate(LocalDateTime.of(2024, 12, 31, 23, 59));
            updatedTodo.setCreatedAt(LocalDateTime.now());
            updatedTodo.setUpdatedAt(LocalDateTime.now());

            when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
            when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);

            TodoResponse response = todoService.updateTodo(1L, updateRequest);

            assertThat(response.getTitle()).isEqualTo("Updated Title");
            assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
            assertThat(response.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
            verify(todoRepository).save(any(Todo.class));
        }

        @Test
        @DisplayName("Should clear due date when not provided")
        void shouldClearDueDate() {
            TodoRequest updateRequest = new TodoRequest();
            updateRequest.setTitle("Test");
            updateRequest.setPriority(Priority.MEDIUM);
            updateRequest.setStatus(TodoStatus.PENDING);
            updateRequest.setDueDate(null);

            when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
            when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

            todoService.updateTodo(1L, updateRequest);

            verify(todoRepository).save(any(Todo.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent todo")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(todoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.updateTodo(999L, sampleRequest))
                    .isInstanceOf(TodoNotFoundException.class);

            verify(todoRepository, never()).save(any(Todo.class));
        }
    }

    @Nested
    @DisplayName("Delete Todo")
    class DeleteTodoTests {

        @Test
        @DisplayName("Should delete an existing todo")
        void shouldDeleteExistingTodo() {
            when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

            todoService.deleteTodo(1L);

            verify(todoRepository).delete(sampleTodo);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent todo")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(todoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.deleteTodo(999L))
                    .isInstanceOf(TodoNotFoundException.class);

            verify(todoRepository, never()).delete(any(Todo.class));
        }
    }

    @Nested
    @DisplayName("Toggle Complete")
    class ToggleCompleteTests {

        @Test
        @DisplayName("Should mark pending todo as completed")
        void shouldMarkPendingAsCompleted() {
            sampleTodo.setStatus(TodoStatus.PENDING);
            Todo completedTodo = new Todo("Test Todo", "Test description", Priority.MEDIUM);
            completedTodo.setId(1L);
            completedTodo.setStatus(TodoStatus.COMPLETED);
            completedTodo.setCreatedAt(LocalDateTime.now());
            completedTodo.setUpdatedAt(LocalDateTime.now());

            when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
            when(todoRepository.save(any(Todo.class))).thenReturn(completedTodo);

            TodoResponse response = todoService.toggleComplete(1L);

            assertThat(response.getStatus()).isEqualTo(TodoStatus.COMPLETED);
        }

        @Test
        @DisplayName("Should mark completed todo as pending")
        void shouldMarkCompletedAsPending() {
            sampleTodo.setStatus(TodoStatus.COMPLETED);
            Todo pendingTodo = new Todo("Test Todo", "Test description", Priority.MEDIUM);
            pendingTodo.setId(1L);
            pendingTodo.setStatus(TodoStatus.PENDING);
            pendingTodo.setCreatedAt(LocalDateTime.now());
            pendingTodo.setUpdatedAt(LocalDateTime.now());

            when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
            when(todoRepository.save(any(Todo.class))).thenReturn(pendingTodo);

            TodoResponse response = todoService.toggleComplete(1L);

            assertThat(response.getStatus()).isEqualTo(TodoStatus.PENDING);
        }

        @Test
        @DisplayName("Should throw exception when toggling non-existent todo")
        void shouldThrowExceptionWhenTogglingNonExistent() {
            when(todoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.toggleComplete(999L))
                    .isInstanceOf(TodoNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Get Todo Statistics")
    class GetStatisticsTests {

        @Test
        @DisplayName("Should return correct statistics")
        void shouldReturnStatistics() {
            when(todoRepository.count()).thenReturn(10L);
            when(todoRepository.countByStatus(TodoStatus.PENDING)).thenReturn(4L);
            when(todoRepository.countByStatus(TodoStatus.IN_PROGRESS)).thenReturn(3L);
            when(todoRepository.countByStatus(TodoStatus.COMPLETED)).thenReturn(2L);
            when(todoRepository.countByStatus(TodoStatus.CANCELLED)).thenReturn(1L);
            when(todoRepository.findByDueDateBeforeAndStatusNot(any(LocalDateTime.class), eq(TodoStatus.COMPLETED)))
                    .thenReturn(Collections.emptyList());

            Map<String, Object> stats = todoService.getTodoStatistics();

            assertThat(stats.get("total")).isEqualTo(10L);
            assertThat(stats.get("pending")).isEqualTo(4L);
            assertThat(stats.get("inProgress")).isEqualTo(3L);
            assertThat(stats.get("completed")).isEqualTo(2L);
            assertThat(stats.get("cancelled")).isEqualTo(1L);
            assertThat(stats.get("overdue")).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Get Filtered Todos")
    class GetFilteredTodosTests {

        @Test
        @DisplayName("Should filter by status and priority")
        void shouldFilterByStatusAndPriority() {
            Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
            when(todoRepository.findByStatusAndPriority(eq(TodoStatus.PENDING), eq(Priority.HIGH), any(Pageable.class)))
                    .thenReturn(page);

            Page<TodoResponse> result = todoService.getFilteredTodos(
                    TodoStatus.PENDING, Priority.HIGH, 0, 10, "createdAt", "desc");

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should filter by status only")
        void shouldFilterByStatusOnly() {
            Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
            when(todoRepository.findByStatus(eq(TodoStatus.PENDING), any(Pageable.class))).thenReturn(page);

            Page<TodoResponse> result = todoService.getFilteredTodos(
                    TodoStatus.PENDING, null, 0, 10, "createdAt", "desc");

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should filter by priority only")
        void shouldFilterByPriorityOnly() {
            Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
            when(todoRepository.findByPriority(eq(Priority.HIGH), any(Pageable.class))).thenReturn(page);

            Page<TodoResponse> result = todoService.getFilteredTodos(
                    null, Priority.HIGH, 0, 10, "createdAt", "desc");

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should return all when no filter applied")
        void shouldReturnAllWhenNoFilter() {
            Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
            when(todoRepository.findAll(any(Pageable.class))).thenReturn(page);

            Page<TodoResponse> result = todoService.getFilteredTodos(
                    null, null, 0, 10, "createdAt", "desc");

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Search Todos")
    class SearchTodosTests {

        @Test
        @DisplayName("Should search by keyword")
        void shouldSearchByKeyword() {
            Page<Todo> page = new PageImpl<>(Arrays.asList(sampleTodo));
            when(todoRepository.searchByKeyword(eq("test"), any(Pageable.class))).thenReturn(page);

            Page<TodoResponse> result = todoService.searchTodos("test", 0, 10);

            assertThat(result.getContent()).hasSize(1);
        }
    }
}
