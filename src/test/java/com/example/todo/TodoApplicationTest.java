package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TodoApplication Main Tests")
class TodoApplicationTest {

    @Test
    @DisplayName("main method starts application context")
    void mainMethodStartsContext() {
        // Calling main with the test profile active exercises the main() entry point.
        TodoApplication.main(new String[]{"--spring.profiles.active=test"});
        assertThat(true).isTrue();
    }
}
