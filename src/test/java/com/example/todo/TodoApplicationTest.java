package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TodoApplication Main Method Test")
class TodoApplicationTest {

    @Test
    @DisplayName("Should start application context via main")
    void shouldStartApplicationContextViaMain() {
        TodoApplication.main(new String[]{
                "--spring.main.allow-bean-definition-overriding=true",
                "--spring.profiles.active=test"
        });
    }
}
