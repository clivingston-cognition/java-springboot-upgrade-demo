package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoApiController.class)
@DisplayName("Todo API Controller Tests")
class TodoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private TodoResponse sampleResponse;
    private TodoRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new TodoResponse();
        sampleResponse.setId(1L);
        sampleResponse.setTitle("Test Todo");
        sampleResponse.setDescription("Test description");
        sampleResponse.setStatus(TodoStatus.PENDING);
        sampleResponse.setPriority(Priority.MEDIUM);
        sampleResponse.setCreatedAt("2024-01-01 10:00");
        sampleResponse.setUpdatedAt("2024-01-01 10:00");

        sampleRequest = new TodoRequest();
        sampleRequest.setTitle("Test Todo");
        sampleRequest.setDescription("Test description");
        sampleRequest.setPriority(Priority.MEDIUM);
        sampleRequest.setStatus(TodoStatus.PENDING);
    }

    @Nested
    @DisplayName("POST /api/todos - Create Todo")
    class CreateTodo {

        @Test
        @DisplayName("Should create a todo and return 201")
        void shouldCreateTodo() throws Exception {
            when(todoService.createTodo(any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.title", is("Test Todo")))
                    .andExpect(jsonPath("$.status", is("PENDING")))
                    .andExpect(jsonPath("$.priority", is("MEDIUM")));

            verify(todoService).createTodo(any(TodoRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when title is blank")
        void shouldReturn400WhenTitleBlank() throws Exception {
            sampleRequest.setTitle("");

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when title is null")
        void shouldReturn400WhenTitleNull() throws Exception {
            sampleRequest.setTitle(null);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when title exceeds max length")
        void shouldReturn400WhenTitleTooLong() throws Exception {
            sampleRequest.setTitle("A".repeat(256));

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should create todo with all fields populated")
        void shouldCreateTodoWithAllFields() throws Exception {
            sampleRequest.setDueDate("2024-12-31T23:59");
            sampleRequest.setPriority(Priority.HIGH);
            sampleRequest.setStatus(TodoStatus.IN_PROGRESS);

            TodoResponse fullResponse = new TodoResponse();
            fullResponse.setId(2L);
            fullResponse.setTitle("Test Todo");
            fullResponse.setDescription("Test description");
            fullResponse.setStatus(TodoStatus.IN_PROGRESS);
            fullResponse.setPriority(Priority.HIGH);
            fullResponse.setDueDate("2024-12-31 23:59");

            when(todoService.createTodo(any(TodoRequest.class))).thenReturn(fullResponse);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.priority", is("HIGH")))
                    .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
        }
    }

    @Nested
    @DisplayName("GET /api/todos/{id} - Get Todo By ID")
    class GetTodoById {

        @Test
        @DisplayName("Should return todo when found")
        void shouldReturnTodoWhenFound() throws Exception {
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/todos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.title", is("Test Todo")));
        }

        @Test
        @DisplayName("Should return 404 when todo not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(todoService.getTodoById(999L)).thenThrow(new TodoNotFoundException(999L));

            mockMvc.perform(get("/api/todos/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is("Todo not found with id: 999")));
        }
    }

    @Nested
    @DisplayName("GET /api/todos - List Todos")
    class ListTodos {

        @Test
        @DisplayName("Should return paginated todos")
        void shouldReturnPaginatedTodos() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title", is("Test Todo")));
        }

        @Test
        @DisplayName("Should return todos filtered by status")
        void shouldReturnTodosFilteredByStatus() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.getFilteredTodos(eq(TodoStatus.PENDING), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/todos").param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));
        }

        @Test
        @DisplayName("Should return todos filtered by priority")
        void shouldReturnTodosFilteredByPriority() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.getFilteredTodos(any(), eq(Priority.HIGH), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/todos").param("priority", "HIGH"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));
        }

        @Test
        @DisplayName("Should return empty page when no todos exist")
        void shouldReturnEmptyPage() throws Exception {
            Page<TodoResponse> emptyPage = new PageImpl<>(Arrays.asList());
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/todos/search - Search Todos")
    class SearchTodos {

        @Test
        @DisplayName("Should return search results")
        void shouldReturnSearchResults() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.searchTodos(eq("test"), anyInt(), anyInt())).thenReturn(page);

            mockMvc.perform(get("/api/todos/search").param("keyword", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title", is("Test Todo")));
        }
    }

    @Nested
    @DisplayName("PUT /api/todos/{id} - Update Todo")
    class UpdateTodo {

        @Test
        @DisplayName("Should update todo and return 200")
        void shouldUpdateTodo() throws Exception {
            TodoResponse updatedResponse = new TodoResponse();
            updatedResponse.setId(1L);
            updatedResponse.setTitle("Updated Title");
            updatedResponse.setDescription("Updated description");
            updatedResponse.setStatus(TodoStatus.IN_PROGRESS);
            updatedResponse.setPriority(Priority.HIGH);

            sampleRequest.setTitle("Updated Title");
            sampleRequest.setDescription("Updated description");
            sampleRequest.setPriority(Priority.HIGH);
            sampleRequest.setStatus(TodoStatus.IN_PROGRESS);

            when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(updatedResponse);

            mockMvc.perform(put("/api/todos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("Updated Title")))
                    .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                    .andExpect(jsonPath("$.priority", is("HIGH")));

            verify(todoService).updateTodo(eq(1L), any(TodoRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent todo")
        void shouldReturn404WhenUpdatingNonExistent() throws Exception {
            when(todoService.updateTodo(eq(999L), any(TodoRequest.class)))
                    .thenThrow(new TodoNotFoundException(999L));

            mockMvc.perform(put("/api/todos/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when update payload is invalid")
        void shouldReturn400WhenUpdateInvalid() throws Exception {
            sampleRequest.setTitle("");

            mockMvc.perform(put("/api/todos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/todos/{id}/toggle - Toggle Complete")
    class ToggleComplete {

        @Test
        @DisplayName("Should toggle todo completion status")
        void shouldToggleComplete() throws Exception {
            TodoResponse toggledResponse = new TodoResponse();
            toggledResponse.setId(1L);
            toggledResponse.setTitle("Test Todo");
            toggledResponse.setStatus(TodoStatus.COMPLETED);

            when(todoService.toggleComplete(1L)).thenReturn(toggledResponse);

            mockMvc.perform(patch("/api/todos/1/toggle"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("COMPLETED")));
        }

        @Test
        @DisplayName("Should return 404 when toggling non-existent todo")
        void shouldReturn404WhenTogglingNonExistent() throws Exception {
            when(todoService.toggleComplete(999L)).thenThrow(new TodoNotFoundException(999L));

            mockMvc.perform(patch("/api/todos/999/toggle"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/todos/{id} - Delete Todo")
    class DeleteTodo {

        @Test
        @DisplayName("Should delete todo and return 204")
        void shouldDeleteTodo() throws Exception {
            doNothing().when(todoService).deleteTodo(1L);

            mockMvc.perform(delete("/api/todos/1"))
                    .andExpect(status().isNoContent());

            verify(todoService).deleteTodo(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent todo")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            doThrow(new TodoNotFoundException(999L)).when(todoService).deleteTodo(999L);

            mockMvc.perform(delete("/api/todos/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/todos/stats - Get Statistics")
    class GetStatistics {

        @Test
        @DisplayName("Should return todo statistics")
        void shouldReturnStatistics() throws Exception {
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", 10L);
            stats.put("pending", 4L);
            stats.put("inProgress", 3L);
            stats.put("completed", 2L);
            stats.put("cancelled", 1L);
            stats.put("overdue", 2);

            when(todoService.getTodoStatistics()).thenReturn(stats);

            mockMvc.perform(get("/api/todos/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total", is(10)))
                    .andExpect(jsonPath("$.pending", is(4)))
                    .andExpect(jsonPath("$.completed", is(2)))
                    .andExpect(jsonPath("$.overdue", is(2)));
        }
    }
}
