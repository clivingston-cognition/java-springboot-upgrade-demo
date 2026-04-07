package com.example.todo.repository;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Todo Repository Tests")
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve a todo")
    void shouldSaveAndRetrieve() {
        Todo todo = new Todo("Test Todo", "Description", Priority.MEDIUM);
        Todo saved = todoRepository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Todo");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find todos by status")
    void shouldFindByStatus() {
        Todo pending = new Todo("Pending Todo", "Desc", Priority.LOW);
        pending.setStatus(TodoStatus.PENDING);
        todoRepository.save(pending);

        Todo completed = new Todo("Completed Todo", "Desc", Priority.LOW);
        completed.setStatus(TodoStatus.COMPLETED);
        todoRepository.save(completed);

        Page<Todo> result = todoRepository.findByStatus(TodoStatus.PENDING, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Pending Todo");
    }

    @Test
    @DisplayName("Should find todos by priority")
    void shouldFindByPriority() {
        Todo high = new Todo("High Priority", "Desc", Priority.HIGH);
        todoRepository.save(high);

        Todo low = new Todo("Low Priority", "Desc", Priority.LOW);
        todoRepository.save(low);

        Page<Todo> result = todoRepository.findByPriority(Priority.HIGH, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("High Priority");
    }

    @Test
    @DisplayName("Should find todos by status and priority")
    void shouldFindByStatusAndPriority() {
        Todo match = new Todo("Match", "Desc", Priority.HIGH);
        match.setStatus(TodoStatus.PENDING);
        todoRepository.save(match);

        Todo noMatch = new Todo("No Match", "Desc", Priority.LOW);
        noMatch.setStatus(TodoStatus.PENDING);
        todoRepository.save(noMatch);

        Page<Todo> result = todoRepository.findByStatusAndPriority(
                TodoStatus.PENDING, Priority.HIGH, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Match");
    }

    @Test
    @DisplayName("Should search by keyword in title")
    void shouldSearchByKeywordInTitle() {
        todoRepository.save(new Todo("Setup CI Pipeline", "Desc", Priority.HIGH));
        todoRepository.save(new Todo("Write Tests", "Desc", Priority.MEDIUM));

        Page<Todo> result = todoRepository.searchByKeyword("pipeline", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Pipeline");
    }

    @Test
    @DisplayName("Should search by keyword in description")
    void shouldSearchByKeywordInDescription() {
        todoRepository.save(new Todo("Task 1", "Configure GitHub Actions", Priority.HIGH));
        todoRepository.save(new Todo("Task 2", "Write unit tests", Priority.MEDIUM));

        Page<Todo> result = todoRepository.searchByKeyword("github", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDescription()).contains("GitHub");
    }

    @Test
    @DisplayName("Should search case-insensitively")
    void shouldSearchCaseInsensitive() {
        todoRepository.save(new Todo("IMPORTANT TASK", "Desc", Priority.HIGH));

        Page<Todo> result = todoRepository.searchByKeyword("important", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should find overdue todos")
    void shouldFindOverdueTodos() {
        Todo overdue = new Todo("Overdue Task", "Desc", Priority.HIGH);
        overdue.setStatus(TodoStatus.PENDING);
        overdue.setDueDate(LocalDateTime.now().minusDays(1));
        todoRepository.save(overdue);

        Todo notOverdue = new Todo("Future Task", "Desc", Priority.LOW);
        notOverdue.setStatus(TodoStatus.PENDING);
        notOverdue.setDueDate(LocalDateTime.now().plusDays(5));
        todoRepository.save(notOverdue);

        Todo completedOverdue = new Todo("Completed Overdue", "Desc", Priority.LOW);
        completedOverdue.setStatus(TodoStatus.COMPLETED);
        completedOverdue.setDueDate(LocalDateTime.now().minusDays(1));
        todoRepository.save(completedOverdue);

        List<Todo> overdueTodos = todoRepository.findByDueDateBeforeAndStatusNot(
                LocalDateTime.now(), TodoStatus.COMPLETED);

        assertThat(overdueTodos).hasSize(1);
        assertThat(overdueTodos.get(0).getTitle()).isEqualTo("Overdue Task");
    }

    @Test
    @DisplayName("Should count by status")
    void shouldCountByStatus() {
        Todo t1 = new Todo("T1", "D", Priority.LOW);
        t1.setStatus(TodoStatus.PENDING);
        todoRepository.save(t1);

        Todo t2 = new Todo("T2", "D", Priority.LOW);
        t2.setStatus(TodoStatus.PENDING);
        todoRepository.save(t2);

        Todo t3 = new Todo("T3", "D", Priority.LOW);
        t3.setStatus(TodoStatus.COMPLETED);
        todoRepository.save(t3);

        assertThat(todoRepository.countByStatus(TodoStatus.PENDING)).isEqualTo(2);
        assertThat(todoRepository.countByStatus(TodoStatus.COMPLETED)).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find by status ordered by priority desc")
    void shouldFindByStatusOrderedByPriority() {
        Todo low = new Todo("Low", "D", Priority.LOW);
        low.setStatus(TodoStatus.PENDING);
        todoRepository.save(low);

        Todo high = new Todo("High", "D", Priority.HIGH);
        high.setStatus(TodoStatus.PENDING);
        todoRepository.save(high);

        Todo critical = new Todo("Critical", "D", Priority.CRITICAL);
        critical.setStatus(TodoStatus.PENDING);
        todoRepository.save(critical);

        List<Todo> result = todoRepository.findByStatusOrderByPriorityDesc(TodoStatus.PENDING);

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Should delete a todo")
    void shouldDeleteTodo() {
        Todo todo = todoRepository.save(new Todo("Delete Me", "Desc", Priority.LOW));
        Long id = todo.getId();

        todoRepository.deleteById(id);

        assertThat(todoRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Should update a todo")
    void shouldUpdateTodo() {
        Todo todo = todoRepository.save(new Todo("Original", "Desc", Priority.LOW));

        todo.setTitle("Updated");
        todo.setPriority(Priority.HIGH);
        Todo updated = todoRepository.save(todo);

        assertThat(updated.getTitle()).isEqualTo("Updated");
        assertThat(updated.getPriority()).isEqualTo(Priority.HIGH);
    }
}
