package com.example.todo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoNotFoundException Tests")
class TodoNotFoundExceptionTest {

    @Test
    @DisplayName("Should contain the id in message and expose it via getter")
    void shouldContainIdInMessage() {
        TodoNotFoundException ex = new TodoNotFoundException(42L);

        assertThat(ex.getMessage()).isEqualTo("Todo not found with id: 42");
        assertThat(ex.getTodoId()).isEqualTo(42L);
    }
}
