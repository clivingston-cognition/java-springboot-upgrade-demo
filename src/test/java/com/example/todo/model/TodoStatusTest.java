package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoStatus Enum Tests")
class TodoStatusTest {

    @Test
    @DisplayName("Should have correct display names")
    void shouldHaveCorrectDisplayNames() {
        assertThat(TodoStatus.PENDING.getDisplayName()).isEqualTo("Pending");
        assertThat(TodoStatus.IN_PROGRESS.getDisplayName()).isEqualTo("In Progress");
        assertThat(TodoStatus.COMPLETED.getDisplayName()).isEqualTo("Completed");
        assertThat(TodoStatus.CANCELLED.getDisplayName()).isEqualTo("Cancelled");
    }

    @Test
    @DisplayName("Should have four values")
    void shouldHaveFourValues() {
        assertThat(TodoStatus.values()).hasSize(4);
    }
}
