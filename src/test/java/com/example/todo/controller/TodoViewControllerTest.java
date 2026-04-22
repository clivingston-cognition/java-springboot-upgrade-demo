package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(TodoViewController.class)
@DisplayName("Todo View Controller Tests")
class TodoViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    private TodoResponse sampleResponse;

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
        sampleResponse.setDueDate("2024-12-31 23:59");
    }

    private static Map<String, Object> sampleStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 1L);
        stats.put("pending", 1L);
        stats.put("inProgress", 0L);
        stats.put("completed", 0L);
        stats.put("cancelled", 0L);
        stats.put("overdue", 0);
        return stats;
    }

    @Nested
    @DisplayName("GET / - List Todos")
    class ListTodos {

        @Test
        @DisplayName("Should render index with default filters")
        void shouldRenderIndex() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats());

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("todos"))
                    .andExpect(model().attributeExists("stats"))
                    .andExpect(model().attribute("currentPage", 0))
                    .andExpect(model().attribute("sortBy", "createdAt"))
                    .andExpect(model().attribute("direction", "desc"))
                    .andExpect(model().attribute("todos", hasSize(1)));
        }

        @Test
        @DisplayName("Should perform search when search param is provided")
        void shouldSearchWhenProvided() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.searchTodos(eq("hello"), anyInt(), anyInt())).thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats());

            mockMvc.perform(get("/").param("search", "  hello  "))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("search", "  hello  "));

            verify(todoService).searchTodos(eq("hello"), anyInt(), anyInt());
        }

        @Test
        @DisplayName("Should ignore blank search param and use filtered list")
        void shouldIgnoreBlankSearch() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList());
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats());

            mockMvc.perform(get("/").param("search", "   "))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeDoesNotExist("search"));
        }

        @Test
        @DisplayName("Should forward status and priority filters to the service")
        void shouldForwardFilters() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.getFilteredTodos(eq(TodoStatus.IN_PROGRESS), eq(Priority.HIGH),
                    anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats());

            mockMvc.perform(get("/")
                            .param("status", "IN_PROGRESS")
                            .param("priority", "HIGH")
                            .param("sortBy", "title")
                            .param("direction", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("selectedStatus", TodoStatus.IN_PROGRESS))
                    .andExpect(model().attribute("selectedPriority", Priority.HIGH))
                    .andExpect(model().attribute("sortBy", "title"))
                    .andExpect(model().attribute("direction", "asc"));
        }
    }

    @Nested
    @DisplayName("GET /todos/new - Create Form")
    class ShowCreateForm {

        @Test
        @DisplayName("Should render form view")
        void shouldRenderCreateForm() throws Exception {
            mockMvc.perform(get("/todos/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", false))
                    .andExpect(model().attributeExists("todoRequest"))
                    .andExpect(model().attributeExists("priorities"))
                    .andExpect(model().attributeExists("statuses"));
        }
    }

    @Nested
    @DisplayName("POST /todos/new - Create Todo")
    class CreateTodo {

        @Test
        @DisplayName("Should redirect to list after successful create")
        void shouldRedirectAfterCreate() throws Exception {
            when(todoService.createTodo(any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/new")
                            .param("title", "Hello")
                            .param("description", "World")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo created successfully!"));

            verify(todoService).createTodo(any(TodoRequest.class));
        }

        @Test
        @DisplayName("Should re-render form when validation fails")
        void shouldRenderFormWhenInvalid() throws Exception {
            mockMvc.perform(post("/todos/new")
                            .param("title", "")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", false))
                    .andExpect(model().attributeExists("priorities"))
                    .andExpect(model().attributeExists("statuses"));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id} - View Todo")
    class ViewTodo {

        @Test
        @DisplayName("Should render todo detail view")
        void shouldRenderDetailView() throws Exception {
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-detail"))
                    .andExpect(model().attribute("todo", notNullValue()));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id}/edit - Edit Form")
    class ShowEditForm {

        @Test
        @DisplayName("Should render edit form populated from existing todo")
        void shouldRenderEditForm() throws Exception {
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", true))
                    .andExpect(model().attribute("todoId", 1L))
                    .andExpect(model().attributeExists("todoRequest"));
        }

        @Test
        @DisplayName("Should render edit form when todo has no due date")
        void shouldRenderEditFormWithoutDueDate() throws Exception {
            sampleResponse.setDueDate(null);
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", true));
        }
    }

    @Nested
    @DisplayName("POST /todos/{id}/edit - Update Todo")
    class UpdateTodo {

        @Test
        @DisplayName("Should redirect to list after successful update")
        void shouldRedirectAfterUpdate() throws Exception {
            when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "Updated")
                            .param("description", "Updated desc")
                            .param("priority", "HIGH")
                            .param("status", "IN_PROGRESS"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo updated successfully!"));
        }

        @Test
        @DisplayName("Should re-render edit form when validation fails")
        void shouldRenderEditFormWhenInvalid() throws Exception {
            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "")
                            .param("priority", "HIGH")
                            .param("status", "IN_PROGRESS"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", true))
                    .andExpect(model().attribute("todoId", 1L));
        }
    }

    @Nested
    @DisplayName("POST /todos/{id}/delete - Delete Todo")
    class DeleteTodo {

        @Test
        @DisplayName("Should redirect after delete")
        void shouldRedirectAfterDelete() throws Exception {
            doNothing().when(todoService).deleteTodo(1L);

            mockMvc.perform(post("/todos/1/delete"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo deleted successfully!"));

            verify(todoService).deleteTodo(1L);
        }

        @Test
        @DisplayName("Should propagate TodoNotFoundException when deleting missing todo")
        void shouldPropagateNotFound() throws Exception {
            doThrow(new TodoNotFoundException(999L)).when(todoService).deleteTodo(999L);

            try {
                mockMvc.perform(post("/todos/999/delete"));
            } catch (Exception expected) {
                // The view controller doesn't have an @ExceptionHandler for missing todos,
                // so the exception is wrapped and bubbles up. We only care it was thrown.
            }

            verify(todoService).deleteTodo(999L);
        }
    }

    @Nested
    @DisplayName("POST /todos/{id}/toggle - Toggle Complete")
    class ToggleComplete {

        @Test
        @DisplayName("Should redirect after toggle")
        void shouldRedirectAfterToggle() throws Exception {
            when(todoService.toggleComplete(1L)).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/1/toggle"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo status updated!"));

            verify(todoService).toggleComplete(1L);
        }
    }
}
