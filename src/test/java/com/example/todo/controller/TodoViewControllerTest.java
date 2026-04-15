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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    @MockBean
    private TodoService todoService;

    private TodoResponse sampleResponse;
    private Map<String, Object> sampleStats;

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

        sampleStats = new HashMap<>();
        sampleStats.put("total", 1L);
        sampleStats.put("pending", 1L);
        sampleStats.put("inProgress", 0L);
        sampleStats.put("completed", 0L);
        sampleStats.put("cancelled", 0L);
        sampleStats.put("overdue", 0);
    }

    @Nested
    @DisplayName("GET / - List Todos")
    class ListTodos {

        @Test
        @DisplayName("Should list todos with default parameters")
        void shouldListTodosDefault() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("todos", "currentPage", "totalPages",
                            "totalItems", "sortBy", "direction", "statuses", "priorities", "stats"));
        }

        @Test
        @DisplayName("Should list todos with search keyword")
        void shouldListTodosWithSearch() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.searchTodos(eq("spring"), anyInt(), anyInt())).thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/").param("search", "spring"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("search", "spring"));
        }

        @Test
        @DisplayName("Should list todos with empty search falling back to filtered")
        void shouldListTodosWithEmptySearch() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Collections.emptyList());
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/").param("search", "   "))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("Should list todos filtered by status")
        void shouldListTodosFilteredByStatus() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.getFilteredTodos(eq(TodoStatus.PENDING), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/").param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("selectedStatus", TodoStatus.PENDING));
        }

        @Test
        @DisplayName("Should list todos filtered by priority")
        void shouldListTodosFilteredByPriority() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.getFilteredTodos(any(), eq(Priority.HIGH), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/").param("priority", "HIGH"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("selectedPriority", Priority.HIGH));
        }
    }

    @Nested
    @DisplayName("GET /todos/new - Show Create Form")
    class ShowCreateForm {

        @Test
        @DisplayName("Should show create form")
        void shouldShowCreateForm() throws Exception {
            mockMvc.perform(get("/todos/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attributeExists("todoRequest", "priorities", "statuses"))
                    .andExpect(model().attribute("isEdit", false));
        }
    }

    @Nested
    @DisplayName("POST /todos/new - Create Todo")
    class CreateTodo {

        @Test
        @DisplayName("Should create todo and redirect")
        void shouldCreateTodoAndRedirect() throws Exception {
            when(todoService.createTodo(any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/new")
                            .param("title", "New Todo")
                            .param("description", "A description")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo created successfully!"));

            verify(todoService).createTodo(any(TodoRequest.class));
        }

        @Test
        @DisplayName("Should return form when validation fails")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(post("/todos/new")
                            .param("title", "")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", false));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id} - View Todo")
    class ViewTodo {

        @Test
        @DisplayName("Should show todo detail")
        void shouldShowTodoDetail() throws Exception {
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-detail"))
                    .andExpect(model().attributeExists("todo"));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id}/edit - Show Edit Form")
    class ShowEditForm {

        @Test
        @DisplayName("Should show edit form with populated fields")
        void shouldShowEditForm() throws Exception {
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attributeExists("todoRequest", "todoId", "priorities", "statuses"))
                    .andExpect(model().attribute("isEdit", true));
        }

        @Test
        @DisplayName("Should show edit form with due date containing space")
        void shouldShowEditFormWithDueDate() throws Exception {
            TodoResponse responseWithDueDate = new TodoResponse();
            responseWithDueDate.setId(1L);
            responseWithDueDate.setTitle("Test");
            responseWithDueDate.setDescription("Desc");
            responseWithDueDate.setStatus(TodoStatus.PENDING);
            responseWithDueDate.setPriority(Priority.HIGH);
            responseWithDueDate.setDueDate("2024-12-31 23:59");

            when(todoService.getTodoById(1L)).thenReturn(responseWithDueDate);

            mockMvc.perform(get("/todos/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", true));
        }

        @Test
        @DisplayName("Should show edit form with null due date")
        void shouldShowEditFormWithNullDueDate() throws Exception {
            TodoResponse responseNoDueDate = new TodoResponse();
            responseNoDueDate.setId(2L);
            responseNoDueDate.setTitle("No due date");
            responseNoDueDate.setDescription("Desc");
            responseNoDueDate.setStatus(TodoStatus.PENDING);
            responseNoDueDate.setPriority(Priority.LOW);
            responseNoDueDate.setDueDate(null);

            when(todoService.getTodoById(2L)).thenReturn(responseNoDueDate);

            mockMvc.perform(get("/todos/2/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", true));
        }
    }

    @Nested
    @DisplayName("POST /todos/{id}/edit - Update Todo")
    class UpdateTodo {

        @Test
        @DisplayName("Should update todo and redirect")
        void shouldUpdateTodoAndRedirect() throws Exception {
            when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "Updated Todo")
                            .param("description", "Updated desc")
                            .param("priority", "HIGH")
                            .param("status", "IN_PROGRESS"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo updated successfully!"));
        }

        @Test
        @DisplayName("Should return form when update validation fails")
        void shouldReturnFormOnUpdateValidationError() throws Exception {
            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "")
                            .param("priority", "HIGH")
                            .param("status", "PENDING"))
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
        @DisplayName("Should delete todo and redirect")
        void shouldDeleteTodoAndRedirect() throws Exception {
            mockMvc.perform(post("/todos/1/delete"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo deleted successfully!"));

            verify(todoService).deleteTodo(1L);
        }
    }

    @Nested
    @DisplayName("POST /todos/{id}/toggle - Toggle Complete")
    class ToggleComplete {

        @Test
        @DisplayName("Should toggle and redirect")
        void shouldToggleAndRedirect() throws Exception {
            when(todoService.toggleComplete(1L)).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/1/toggle"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo status updated!"));

            verify(todoService).toggleComplete(1L);
        }
    }
}
