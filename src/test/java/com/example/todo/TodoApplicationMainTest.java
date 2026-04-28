package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@DisplayName("TodoApplication Main Tests")
class TodoApplicationMainTest {

    @Test
    @DisplayName("Should run main method without error")
    void shouldRunMainMethod() {
        ConfigurableApplicationContext ctx = SpringApplication.run(
                TodoApplication.class,
                "--spring.profiles.active=test",
                "--server.port=0");
        ctx.close();
    }
}
