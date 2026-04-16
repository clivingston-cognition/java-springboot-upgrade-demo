package com.example.todo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoNotFoundException Tests")
class TodoNotFoundExceptionTest {

    @Test
    @DisplayName("Should store todoId and format message")
    void shouldStoreIdAndMessage() {
        TodoNotFoundException ex = new TodoNotFoundException(99L);

        assertThat(ex.getTodoId()).isEqualTo(99L);
        assertThat(ex.getMessage()).isEqualTo("Todo not found with id: 99");
    }
}
