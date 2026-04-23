package com.example.todo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should build 404 body for TodoNotFoundException")
    void shouldHandleTodoNotFound() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleTodoNotFound(new TodoNotFoundException(42L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.get("error")).isEqualTo("Not Found");
        assertThat(body.get("message")).isEqualTo("Todo not found with id: 42");
        assertThat(body.get("timestamp")).isNotNull();
    }

    @Test
    @DisplayName("Should aggregate field errors for validation failures")
    void shouldHandleValidationErrors() throws Exception {
        Method dummyMethod = Dummy.class.getDeclaredMethod("dummy", Object.class);
        MethodParameter parameter = new MethodParameter(dummyMethod, 0);

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "title", "Title is required"));
        bindingResult.addError(new FieldError("target", "priority", "Priority is required"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("error")).isEqualTo("Validation Failed");
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
        assertThat(fieldErrors)
                .containsEntry("title", "Title is required")
                .containsEntry("priority", "Priority is required");
    }

    @Test
    @DisplayName("Should produce 500 body for unexpected exceptions")
    void shouldHandleGenericException() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleGenericException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("An unexpected error occurred");
    }

    @SuppressWarnings("unused")
    private static class Dummy {
        void dummy(Object body) {
        }
    }
}
