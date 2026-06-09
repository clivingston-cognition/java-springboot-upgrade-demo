package com.example.todo.repository;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
    }

    @Test
    void save_andFindById_works() {
        Todo todo = new Todo("Repo Test", "Description", Priority.HIGH, LocalDate.now().plusDays(1));
        Todo saved = todoRepository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(todoRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findByStatus_returnsMatchingTodos() {
        Todo pending = new Todo("Pending Task", null, Priority.LOW, null);
        pending.setStatus(TodoStatus.PENDING);
        todoRepository.save(pending);

        Todo completed = new Todo("Completed Task", null, Priority.LOW, null);
        completed.setStatus(TodoStatus.COMPLETED);
        todoRepository.save(completed);

        List<Todo> result = todoRepository.findByStatus(TodoStatus.PENDING);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Pending Task");
    }

    @Test
    void findByPriority_returnsMatchingTodos() {
        Todo high = new Todo("High Priority", null, Priority.HIGH, null);
        todoRepository.save(high);

        Todo low = new Todo("Low Priority", null, Priority.LOW, null);
        todoRepository.save(low);

        List<Todo> result = todoRepository.findByPriority(Priority.HIGH);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("High Priority");
    }

    @Test
    void findAllByOrderByCreatedAtDesc_returnsSortedResults() {
        Todo first = new Todo("First", null, Priority.LOW, null);
        todoRepository.save(first);

        Todo second = new Todo("Second", null, Priority.LOW, null);
        todoRepository.save(second);

        List<Todo> result = todoRepository.findAllByOrderByCreatedAtDesc();
        assertThat(result).hasSize(2);
    }

    @Test
    void findByStatusAndPriority_returnsMatchingTodos() {
        Todo match = new Todo("Match", null, Priority.HIGH, null);
        match.setStatus(TodoStatus.PENDING);
        todoRepository.save(match);

        Todo noMatch = new Todo("No Match", null, Priority.LOW, null);
        noMatch.setStatus(TodoStatus.COMPLETED);
        todoRepository.save(noMatch);

        List<Todo> result = todoRepository.findByStatusAndPriority(TodoStatus.PENDING, Priority.HIGH);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Match");
    }
}
