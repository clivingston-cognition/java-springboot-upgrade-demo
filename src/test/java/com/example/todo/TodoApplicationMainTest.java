package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TodoApplication Main Tests")
class TodoApplicationMainTest {

    @Test
    @DisplayName("Should run main method without error")
    void shouldRunMainMethod() {
        TodoApplication.main(new String[]{"--spring.main.allow-bean-definition-overriding=true"});
    }
}
