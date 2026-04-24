package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
        @DisplayName("Should list todos without filters")
        void shouldListTodosWithoutFilters() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.getFilteredTodos(isNull(), isNull(), eq(0), eq(10), eq("createdAt"), eq("desc")))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("todos", "currentPage", "totalPages",
                            "totalItems", "sortBy", "direction", "statuses", "priorities", "stats"));
        }

        @Test
        @DisplayName("Should list todos with status filter")
        void shouldListTodosWithStatusFilter() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.getFilteredTodos(eq(TodoStatus.PENDING), isNull(), eq(0), eq(10), eq("createdAt"), eq("desc")))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/").param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("selectedStatus", TodoStatus.PENDING));
        }

        @Test
        @DisplayName("Should list todos with priority filter")
        void shouldListTodosWithPriorityFilter() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.getFilteredTodos(isNull(), eq(Priority.HIGH), eq(0), eq(10), eq("createdAt"), eq("desc")))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/").param("priority", "HIGH"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("selectedPriority", Priority.HIGH));
        }

        @Test
        @DisplayName("Should search todos when search param is provided")
        void shouldSearchTodos() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.searchTodos(eq("test"), anyInt(), anyInt())).thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/").param("search", "test"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("search", "test"));
        }

        @Test
        @DisplayName("Should use filtered todos when search is blank")
        void shouldUseFilteredWhenSearchBlank() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Collections.emptyList());
            when(todoService.getFilteredTodos(isNull(), isNull(), eq(0), eq(10), eq("createdAt"), eq("desc")))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/").param("search", "   "))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("Should support custom pagination and sorting")
        void shouldSupportCustomPaginationAndSorting() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Collections.emptyList());
            when(todoService.getFilteredTodos(isNull(), isNull(), eq(1), eq(5), eq("title"), eq("asc")))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(sampleStats);

            mockMvc.perform(get("/")
                            .param("page", "1")
                            .param("size", "5")
                            .param("sortBy", "title")
                            .param("direction", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("sortBy", "title"))
                    .andExpect(model().attribute("direction", "asc"));
        }
    }

    @Nested
    @DisplayName("GET /todos/new - Create Form")
    class ShowCreateForm {

        @Test
        @DisplayName("Should show the create form")
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
                            .param("description", "Description")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo created successfully!"));
        }

        @Test
        @DisplayName("Should return form with errors when validation fails")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(post("/todos/new")
                            .param("title", "")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attributeExists("priorities", "statuses"))
                    .andExpect(model().attribute("isEdit", false));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id} - View Todo")
    class ViewTodo {

        @Test
        @DisplayName("Should show todo detail page")
        void shouldShowTodoDetail() throws Exception {
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-detail"))
                    .andExpect(model().attributeExists("todo"));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id}/edit - Edit Form")
    class ShowEditForm {

        @Test
        @DisplayName("Should show the edit form with existing data")
        void shouldShowEditFormWithData() throws Exception {
            sampleResponse.setDueDate("2024-12-31 23:59");
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attributeExists("todoRequest", "todoId", "priorities", "statuses"))
                    .andExpect(model().attribute("isEdit", true));
        }

        @Test
        @DisplayName("Should show edit form when due date is null")
        void shouldShowEditFormWithNullDueDate() throws Exception {
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
        @DisplayName("Should update todo and redirect")
        void shouldUpdateTodoAndRedirect() throws Exception {
            when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "Updated Todo")
                            .param("description", "Updated")
                            .param("priority", "HIGH")
                            .param("status", "IN_PROGRESS"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo updated successfully!"));
        }

        @Test
        @DisplayName("Should return form with errors when validation fails on update")
        void shouldReturnFormOnUpdateValidationError() throws Exception {
            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "")
                            .param("priority", "HIGH")
                            .param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", true))
                    .andExpect(model().attributeExists("todoId", "priorities", "statuses"));
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
        @DisplayName("Should toggle todo completion and redirect")
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
