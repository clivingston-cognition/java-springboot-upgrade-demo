package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Priority Enum Tests")
class PriorityTest {

    @Test
    @DisplayName("Should return correct display names")
    void shouldReturnCorrectDisplayNames() {
        assertThat(Priority.LOW.getDisplayName()).isEqualTo("Low");
        assertThat(Priority.MEDIUM.getDisplayName()).isEqualTo("Medium");
        assertThat(Priority.HIGH.getDisplayName()).isEqualTo("High");
        assertThat(Priority.CRITICAL.getDisplayName()).isEqualTo("Critical");
    }

    @Test
    @DisplayName("Should have four values")
    void shouldHaveFourValues() {
        assertThat(Priority.values()).hasSize(4);
    }
}
