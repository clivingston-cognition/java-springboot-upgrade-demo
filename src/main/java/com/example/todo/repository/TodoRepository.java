package com.example.todo.repository;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByStatus(TodoStatus status);

    List<Todo> findByPriority(Priority priority);

    List<Todo> findByStatusAndPriority(TodoStatus status, Priority priority);

    List<Todo> findAllByOrderByCreatedAtDesc();
}
