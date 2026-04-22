package com.example.todo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 404 body for TodoNotFoundException")
    void shouldHandleTodoNotFound() {
        TodoNotFoundException ex = new TodoNotFoundException(42L);

        ResponseEntity<Map<String, Object>> response = handler.handleTodoNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.get("error")).isEqualTo("Not Found");
        assertThat(body).containsKey("timestamp");
        assertThat(body.get("message")).asString().contains("42");
    }

    @Test
    @DisplayName("Should return 400 body with field errors for MethodArgumentNotValidException")
    void shouldHandleValidationErrors() throws Exception {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "todoRequest");
        bindingResult.addError(new FieldError("todoRequest", "title", "Title is required"));
        bindingResult.addError(new FieldError("todoRequest", "priority", "Priority is required"));

        Method dummyMethod = DummyTarget.class.getDeclaredMethod("dummy", String.class);
        MethodParameter methodParameter = new MethodParameter(dummyMethod, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("error")).isEqualTo("Validation Failed");
        assertThat(body).containsKey("timestamp");

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
        assertThat(fieldErrors)
                .containsEntry("title", "Title is required")
                .containsEntry("priority", "Priority is required");
    }

    @Test
    @DisplayName("Should return 500 body for generic exceptions")
    void shouldHandleGenericException() {
        Exception ex = Mockito.mock(Exception.class);
        Mockito.when(ex.getMessage()).thenReturn("boom");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("An unexpected error occurred");
    }

    @SuppressWarnings("unused")
    private static class DummyTarget {
        void dummy(String input) {
        }
    }
}
