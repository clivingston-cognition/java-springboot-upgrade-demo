package com.example.todo.integration;

import com.example.todo.dto.TodoRequest;
import com.example.todo.model.Priority;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Todo API Integration Tests")
class TodoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
    }

    @Test
    @DisplayName("Full CRUD lifecycle: Create -> Read -> Update -> Delete")
    void fullCrudLifecycle() throws Exception {
        // CREATE
        TodoRequest createRequest = new TodoRequest("Integration Test Todo",
                "Full lifecycle test", Priority.HIGH, TodoStatus.PENDING, null);

        MvcResult createResult = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Integration Test Todo")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        Long todoId = objectMapper.readTree(responseBody).get("id").asLong();

        // READ
        mockMvc.perform(get("/api/todos/" + todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(todoId.intValue())))
                .andExpect(jsonPath("$.title", is("Integration Test Todo")));

        // UPDATE
        TodoRequest updateRequest = new TodoRequest("Updated Integration Todo",
                "Updated description", Priority.CRITICAL, TodoStatus.IN_PROGRESS, "2025-12-31T23:59");

        mockMvc.perform(put("/api/todos/" + todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Integration Todo")))
                .andExpect(jsonPath("$.priority", is("CRITICAL")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

        // Verify update persisted
        mockMvc.perform(get("/api/todos/" + todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Integration Todo")));

        // DELETE
        mockMvc.perform(delete("/api/todos/" + todoId))
                .andExpect(status().isNoContent());

        // Verify delete
        mockMvc.perform(get("/api/todos/" + todoId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should toggle todo completion status back and forth")
    void shouldToggleCompletionStatus() throws Exception {
        // Create
        TodoRequest request = new TodoRequest("Toggle Test", "Desc", Priority.MEDIUM, TodoStatus.PENDING, null);

        MvcResult result = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Long todoId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // Toggle to COMPLETED
        mockMvc.perform(patch("/api/todos/" + todoId + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        // Toggle back to PENDING
        mockMvc.perform(patch("/api/todos/" + todoId + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @DisplayName("Should list todos with pagination")
    void shouldListTodosWithPagination() throws Exception {
        // Create 15 todos
        for (int i = 0; i < 15; i++) {
            TodoRequest request = new TodoRequest("Todo " + i, "Desc " + i, Priority.MEDIUM, TodoStatus.PENDING, null);
            mockMvc.perform(post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }

        // First page
        mockMvc.perform(get("/api/todos").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", is(15)))
                .andExpect(jsonPath("$.totalPages", is(2)));

        // Second page
        mockMvc.perform(get("/api/todos").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    @DisplayName("Should filter todos by status")
    void shouldFilterByStatus() throws Exception {
        TodoRequest pending = new TodoRequest("Pending", "D", Priority.MEDIUM, TodoStatus.PENDING, null);
        TodoRequest inProgress = new TodoRequest("In Progress", "D", Priority.MEDIUM, TodoStatus.IN_PROGRESS, null);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pending)));
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inProgress)));

        mockMvc.perform(get("/api/todos").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("PENDING")));
    }

    @Test
    @DisplayName("Should search todos by keyword")
    void shouldSearchByKeyword() throws Exception {
        TodoRequest t1 = new TodoRequest("Deploy application", "To production", Priority.HIGH, TodoStatus.PENDING, null);
        TodoRequest t2 = new TodoRequest("Write tests", "Unit tests for service layer", Priority.MEDIUM, TodoStatus.PENDING, null);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t1)));
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t2)));

        mockMvc.perform(get("/api/todos/search").param("keyword", "deploy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Deploy application")));
    }

    @Test
    @DisplayName("Should return statistics")
    void shouldReturnStatistics() throws Exception {
        TodoRequest t1 = new TodoRequest("T1", "D", Priority.HIGH, TodoStatus.PENDING, null);
        TodoRequest t2 = new TodoRequest("T2", "D", Priority.MEDIUM, TodoStatus.COMPLETED, null);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t1)));
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t2)));

        mockMvc.perform(get("/api/todos/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.pending", is(1)))
                .andExpect(jsonPath("$.completed", is(1)));
    }

    @Test
    @DisplayName("Should return 400 for invalid create request")
    void shouldReturn400ForInvalidCreate() throws Exception {
        TodoRequest invalid = new TodoRequest("", "D", Priority.MEDIUM, TodoStatus.PENDING, null);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 for non-existent todo")
    void shouldReturn404ForNonExistent() throws Exception {
        mockMvc.perform(get("/api/todos/99999"))
                .andExpect(status().isNotFound());
    }
}
