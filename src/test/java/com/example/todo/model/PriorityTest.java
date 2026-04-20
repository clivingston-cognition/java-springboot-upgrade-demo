package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Priority Enum Tests")
class PriorityTest {

    @Test
    @DisplayName("Each priority has the correct display name")
    void displayNames() {
        assertThat(Priority.LOW.getDisplayName()).isEqualTo("Low");
        assertThat(Priority.MEDIUM.getDisplayName()).isEqualTo("Medium");
        assertThat(Priority.HIGH.getDisplayName()).isEqualTo("High");
        assertThat(Priority.CRITICAL.getDisplayName()).isEqualTo("Critical");
    }

    @Test
    @DisplayName("All enum values are present")
    void allValuesPresent() {
        assertThat(Priority.values()).hasSize(4);
    }
}
