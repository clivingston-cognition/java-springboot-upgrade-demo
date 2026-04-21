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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
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
    private Map<String, Object> stats;

    @BeforeEach
    void setUp() {
        sampleResponse = new TodoResponse();
        sampleResponse.setId(1L);
        sampleResponse.setTitle("Sample Todo");
        sampleResponse.setDescription("desc");
        sampleResponse.setStatus(TodoStatus.PENDING);
        sampleResponse.setPriority(Priority.MEDIUM);

        stats = new HashMap<>();
        stats.put("total", 1L);
        stats.put("pending", 1L);
        stats.put("inProgress", 0L);
        stats.put("completed", 0L);
        stats.put("cancelled", 0L);
        stats.put("overdue", 0);
    }

    @Nested
    @DisplayName("GET / - List Todos")
    class ListTodos {

        @Test
        @DisplayName("Should render index with default filters")
        void rendersIndexWithDefaults() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(stats);

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("todos", "currentPage", "totalPages",
                            "totalItems", "sortBy", "direction", "statuses", "priorities", "stats"));
        }

        @Test
        @DisplayName("Should use search branch when search query present")
        void usesSearchBranch() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.searchTodos(eq("deploy"), anyInt(), anyInt())).thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(stats);

            mockMvc.perform(get("/").param("search", "  deploy  "))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("search", "  deploy  "));

            verify(todoService).searchTodos("deploy", 0, 10);
        }

        @Test
        @DisplayName("Should ignore blank search and use filter branch")
        void blankSearchUsesFilterBranch() throws Exception {
            Page<TodoResponse> page = new PageImpl<>(Arrays.asList(sampleResponse));
            when(todoService.getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(page);
            when(todoService.getTodoStatistics()).thenReturn(stats);

            mockMvc.perform(get("/").param("search", "   "))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));

            verify(todoService).getFilteredTodos(any(), any(), anyInt(), anyInt(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("GET /todos/new - Create Form")
    class ShowCreateForm {

        @Test
        @DisplayName("Should render the empty create form")
        void rendersEmptyForm() throws Exception {
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
        @DisplayName("Should redirect to / after valid create")
        void redirectsAfterCreate() throws Exception {
            when(todoService.createTodo(any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/new")
                            .param("title", "New Todo")
                            .param("description", "desc")
                            .param("priority", "HIGH")
                            .param("status", "PENDING"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(todoService).createTodo(any(TodoRequest.class));
        }

        @Test
        @DisplayName("Should re-render form when validation fails")
        void rerendersFormOnValidationError() throws Exception {
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
        @DisplayName("Should render detail view with todo attribute")
        void rendersDetail() throws Exception {
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-detail"))
                    .andExpect(model().attribute("todo", sampleResponse));
        }
    }

    @Nested
    @DisplayName("GET /todos/{id}/edit - Edit Form")
    class ShowEditForm {

        @Test
        @DisplayName("Should render edit form and convert due date formatting")
        void rendersEditForm() throws Exception {
            sampleResponse.setDueDate("2024-12-31 23:59");
            when(todoService.getTodoById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/todos/1/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("todoId", 1L))
                    .andExpect(model().attribute("isEdit", true))
                    .andExpect(model().attributeExists("todoRequest", "priorities", "statuses"));
        }

        @Test
        @DisplayName("Should render edit form when due date is null")
        void rendersEditFormWithNullDueDate() throws Exception {
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
        @DisplayName("Should redirect after successful update")
        void redirectsAfterUpdate() throws Exception {
            when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "Updated")
                            .param("description", "d")
                            .param("priority", "HIGH")
                            .param("status", "IN_PROGRESS"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("Should re-render edit form on validation error")
        void rerendersOnValidationError() throws Exception {
            mockMvc.perform(post("/todos/1/edit")
                            .param("title", "")
                            .param("priority", "MEDIUM")
                            .param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("todo-form"))
                    .andExpect(model().attribute("isEdit", true))
                    .andExpect(model().attribute("todoId", 1L));
        }
    }

    @Nested
    @DisplayName("POST /todos/{id}/delete")
    class DeleteTodo {

        @Test
        @DisplayName("Should delete todo and redirect home")
        void deletesAndRedirects() throws Exception {
            mockMvc.perform(post("/todos/1/delete"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(todoService).deleteTodo(1L);
        }
    }

    @Nested
    @DisplayName("POST /todos/{id}/toggle")
    class ToggleComplete {

        @Test
        @DisplayName("Should toggle todo and redirect home")
        void togglesAndRedirects() throws Exception {
            when(todoService.toggleComplete(1L)).thenReturn(sampleResponse);

            mockMvc.perform(post("/todos/1/toggle"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(todoService).toggleComplete(1L);
        }
    }
}
