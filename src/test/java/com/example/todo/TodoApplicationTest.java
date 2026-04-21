package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("TodoApplication Main Method Test")
class TodoApplicationTest {

    @Test
    @DisplayName("main method runs without throwing")
    void mainMethodRuns() {
        assertThatCode(() -> TodoApplication.main(new String[]{"--spring.main.web-application-type=none", "--spring.profiles.active=test"}))
                .doesNotThrowAnyException();
    }
}
