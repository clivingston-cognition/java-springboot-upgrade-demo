package com.example.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Enum Display Name Tests")
class EnumTest {

    @Test
    @DisplayName("Priority should expose display names for all values")
    void priorityDisplayNames() {
        assertThat(Priority.LOW.getDisplayName()).isEqualTo("Low");
        assertThat(Priority.MEDIUM.getDisplayName()).isEqualTo("Medium");
        assertThat(Priority.HIGH.getDisplayName()).isEqualTo("High");
        assertThat(Priority.CRITICAL.getDisplayName()).isEqualTo("Critical");
        assertThat(Priority.values()).hasSize(4);
        assertThat(Priority.valueOf("HIGH")).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("TodoStatus should expose display names for all values")
    void todoStatusDisplayNames() {
        assertThat(TodoStatus.PENDING.getDisplayName()).isEqualTo("Pending");
        assertThat(TodoStatus.IN_PROGRESS.getDisplayName()).isEqualTo("In Progress");
        assertThat(TodoStatus.COMPLETED.getDisplayName()).isEqualTo("Completed");
        assertThat(TodoStatus.CANCELLED.getDisplayName()).isEqualTo("Cancelled");
        assertThat(TodoStatus.values()).hasSize(4);
        assertThat(TodoStatus.valueOf("PENDING")).isEqualTo(TodoStatus.PENDING);
    }
}
