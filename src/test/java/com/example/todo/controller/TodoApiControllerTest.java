package com.example.todo.controller;

import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoApiController.class)
class TodoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo createSampleTodo() {
        Todo todo = new Todo("Sample Todo", "Description", Priority.MEDIUM, LocalDate.now().plusDays(5));
        todo.setId(1L);
        todo.setStatus(TodoStatus.PENDING);
        return todo;
    }

    @Test
    void getAll_returnsListOfTodos() throws Exception {
        Todo todo = createSampleTodo();
        when(todoService.findAll()).thenReturn(List.of(todo));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Sample Todo")));
    }

    @Test
    void getById_existingId_returnsTodo() throws Exception {
        Todo todo = createSampleTodo();
        when(todoService.findById(1L)).thenReturn(todo);

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Sample Todo")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void getById_nonExistingId_returns404() throws Exception {
        when(todoService.findById(99L)).thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        Todo todo = createSampleTodo();
        when(todoService.create(any())).thenReturn(todo);

        String json = """
                {"title": "New Todo", "description": "Desc", "priority": "HIGH"}
                """;

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Sample Todo")));
    }

    @Test
    void create_blankTitle_returns400() throws Exception {
        String json = """
                {"title": "", "description": "Desc"}
                """;

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_existingTodo_returns200() throws Exception {
        Todo updated = createSampleTodo();
        updated.setTitle("Updated");
        when(todoService.update(eq(1L), any())).thenReturn(updated);

        String json = """
                {"title": "Updated", "description": "Updated Desc", "priority": "HIGH"}
                """;

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated")));
    }

    @Test
    void toggleComplete_returns200() throws Exception {
        Todo toggled = createSampleTodo();
        toggled.setStatus(TodoStatus.COMPLETED);
        when(todoService.toggleComplete(1L)).thenReturn(toggled);

        mockMvc.perform(put("/api/todos/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    void delete_existingTodo_returns204() throws Exception {
        doNothing().when(todoService).delete(1L);

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExistingTodo_returns404() throws Exception {
        doThrow(new TodoNotFoundException(99L)).when(todoService).delete(99L);

        mockMvc.perform(delete("/api/todos/99"))
                .andExpect(status().isNotFound());
    }
}
