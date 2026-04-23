package com.example.todo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Todo Not Found Exception Tests")
class TodoNotFoundExceptionTest {

    @Test
    @DisplayName("Should expose id and message")
    void shouldExposeIdAndMessage() {
        TodoNotFoundException ex = new TodoNotFoundException(7L);

        assertThat(ex.getTodoId()).isEqualTo(7L);
        assertThat(ex.getMessage()).isEqualTo("Todo not found with id: 7");
    }
}
