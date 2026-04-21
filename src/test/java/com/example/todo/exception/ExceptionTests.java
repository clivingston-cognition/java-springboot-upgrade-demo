package com.example.todo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Exception + Handler Tests")
class ExceptionTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("TodoNotFoundException exposes id and message")
    void todoNotFoundExceptionExposesId() {
        TodoNotFoundException ex = new TodoNotFoundException(42L);

        assertThat(ex.getTodoId()).isEqualTo(42L);
        assertThat(ex.getMessage()).contains("42");
    }

    @Test
    @DisplayName("handleTodoNotFound returns 404 with message")
    void handleTodoNotFound() {
        TodoNotFoundException ex = new TodoNotFoundException(99L);

        ResponseEntity<Map<String, Object>> response = handler.handleTodoNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message")).asString().contains("99");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    @DisplayName("handleValidationErrors aggregates field errors into body")
    void handleValidationErrors() throws Exception {
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "todo");
        binding.addError(new FieldError("todo", "title", "must not be blank"));
        binding.addError(new FieldError("todo", "priority", "must not be null"));

        Method method = DummyTarget.class.getDeclaredMethod("noop");
        org.springframework.core.MethodParameter param = new org.springframework.core.MethodParameter(method, -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, binding);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().get("error")).isEqualTo("Validation Failed");

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors)
                .containsEntry("title", "must not be blank")
                .containsEntry("priority", "must not be null");
    }

    @Test
    @DisplayName("handleGenericException returns 500 with generic message")
    void handleGenericException() {
        Exception unexpected = new RuntimeException("boom");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(unexpected);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred");
    }

    @SuppressWarnings("unused")
    private static final class DummyTarget {
        void noop() {
        }
    }
}
