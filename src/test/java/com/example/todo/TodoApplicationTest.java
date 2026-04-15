package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoApplication Tests")
class TodoApplicationTest {

    @Test
    @DisplayName("Application class can be instantiated")
    void applicationClassExists() {
        TodoApplication app = new TodoApplication();
        assertThat(app).isNotNull();
    }
}
