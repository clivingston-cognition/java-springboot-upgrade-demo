package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TodoApplication Main Method Test")
class TodoApplicationTest {

    @Test
    @DisplayName("Should start and close application context via main")
    void shouldStartApplicationContextViaMain() {
        // Call main() to cover that line, passing test profile and random port
        // to avoid conflicts with other test contexts.
        TodoApplication.main(new String[]{
                "--spring.profiles.active=test",
                "--server.port=0"
        });
    }
}
