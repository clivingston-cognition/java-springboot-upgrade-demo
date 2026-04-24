package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

@DisplayName("TodoApplication Main Method Tests")
class TodoApplicationTest {

    @Test
    @DisplayName("main method should invoke SpringApplication.run")
    void mainShouldInvokeSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(TodoApplication.class, new String[]{}))
                    .thenReturn(null);

            TodoApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(TodoApplication.class, new String[]{}));
        }
    }
}
