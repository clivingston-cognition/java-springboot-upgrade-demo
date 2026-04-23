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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
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

    @MockitoBean
    private TodoService todoService;

    private TodoResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new TodoResponse();
        sampleResponse.setId(1L);
        sampleResponse.setTitle("Existing Todo");
        sampleResponse.setDescription("desc");
        sampleResponse.setPriority(Priority.MEDIUM);
        sampleResponse.setStatus(TodoStatus.PENDING);
        sampleResponse.setCreatedAt("2024-01-01 10:00");
        sampleResponse.setUpdatedAt("2024-01-01 10:00");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 1L);
        stats.put("pending", 1L);
        stats.put("inProgress", 0L);
        stats.put("completed", 0L);
        stats.put("cancelled", 0L);
        stats.put("overdue", 0);
        when(todoService.getTodoStatistics()).thenReturn(stats);
    }

    @Nested
    @DisplayName("GET / - List Todos")
    class ListTodos {

        @Test
        @DisplayName("Should render index with filtered todos when no search provided")
        void shouldRenderIndexWithFilteredTodos() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("todos", "currentPage", "totalPages",
                            "totalItems", "sortBy", "direction", "statuses", "priorities", "stats"));

            verify(todoService).getFilteredTodos(any(), any(), eq(0), eq(10), eq("createdAt"), eq("desc"));
        }

        @Test
        @DisplayName("Should render index with search results when search param provided")
        void shouldRenderIndexWithSearch() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.searchTodos(eq("deploy"), anyInt(), anyInt())).thenReturn(page);

            mockMvc.perform(get("/").param("search", "deploy"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("search", "deploy"));

            verify(todoService).searchTodos(eq("deploy"), eq(0), eq(10));
        }

        @Test
        @DisplayName("Should ignore blank search and fall back to filtered list")
        void shouldIgnoreBlankSearch() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);

            mockMvc.perform(get("/").param("search", "   "))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));

            verify(todoService).getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should pass through pagination, sort and filter params")
        void shouldPassThroughParams() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(todoService.getFilteredTodos(eq(TodoStatus.PENDING), eq(Priority.HIGH),
                    eq(2), eq(5), eq("title"), eq("asc"))).thenReturn(page);

            mockMvc.perform(get("/")
                            .param("page", "2")
                            .param("size", "5")
                            .param("sortBy", "title")
                            .param("direction", "asc")
                            .param("status", "PENDING")
                            .param("priority", "HIGH"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("selectedStatus", TodoStatus.PENDING))
                    .andExpect(model().attribute("selectedPriority", Priority.HIGH));
        }
    }

    @Nested
    @DisplayName("GET /todos/new - Show Create Form")
    class ShowCreateForm {

        @Test
        @DisplayName("Should render blank create form")
        void shouldRenderCreateForm() throws Exception {
            mockMvc.perform(get("/todos/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", false))
                    .andExpect(model().attributeExists("todoRequest", "priorities", "statuses"));
        }
    }

    @Nested
    @DisplayName("POST /todos/new - Create Todo")
    class CreateTodo {

        @Test
        @DisplayName("Should redirect on success with flash message")
        void shouldRedirectOnSuccess() throws Exception {
            when(todoService.createTodo(any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/new")
                            .param("title", "Valid title")
                            .param("description", "Valid description")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo created successfully!"));
        }

        @Test
        @DisplayName("Should re-render form with errors when title blank")
        void shouldRerenderFormOnValidationErrors() throws Exception {
            mockMvc.perform(post("/todos/new")
                            .param("title", "")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", false))
                    .andExpect(model().attributeExists("priorities", "statuses"));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id} - View Todo")
    class ViewTodo {

        @Test
        @DisplayName("Should render detail view")
        void shouldRenderDetail() throws Exception {
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-detail"))
                    .andExpect(model().attribute("todo", sampleResponse));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id}/edit - Show Edit Form")
    class ShowEditForm {

        @Test
        @DisplayName("Should render edit form with existing data")
        void shouldRenderEditFormWithData() throws Exception {
            sampleResponse.setDueDate("2024-12-31 23:59");
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", true))
                    .andExpect(model().attribute("todoId", 1L))
                    .andExpect(model().attributeExists("todoRequest", "priorities", "statuses"));
        }

        @Test
        @DisplayName("Should render edit form when due date is null")
        void shouldRenderEditFormWithoutDueDate() throws Exception {
            sampleResponse.setDueDate(null);
            when(todoService.getTodoById(2L)).thenReturn(sampleResponse);

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
        @DisplayName("Should redirect on successful update")
        void shouldRedirectOnSuccessfulUpdate() throws Exception {
            when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "Updated title")
                            .param("description", "Updated description")
                            .param("priority", "HIGH")
                            .param("status", "IN_PROGRESS"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo updated successfully!"));

            verify(todoService).updateTodo(eq(1L), any(TodoRequest.class));
        }

        @Test
        @DisplayName("Should re-render form with errors when update invalid")
        void shouldRerenderFormOnUpdateErrors() throws Exception {
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
        @DisplayName("Should redirect after delete with flash message")
        void shouldRedirectAfterDelete() throws Exception {
            mockMvc.perform(post("/todos/1/delete"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("successMessage", "Todo deleted successfully!"));

            verify(todoService).deleteTodo(1L);
        }
    }

    @Nested
    @DisplayName("POST /todos/{id}/toggle - Toggle Complete")
    class ToggleTodo {

        @Test
        @DisplayName("Should redirect after toggling with flash message")
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
