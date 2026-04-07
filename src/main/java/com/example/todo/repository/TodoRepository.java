package com.example.todo.repository;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByStatus(TodoStatus status, Pageable pageable);

    Page<Todo> findByPriority(Priority priority, Pageable pageable);

    Page<Todo> findByStatusAndPriority(TodoStatus status, Priority priority, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Todo> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Todo> findByDueDateBeforeAndStatusNot(LocalDateTime dateTime, TodoStatus status);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.status = :status")
    long countByStatus(@Param("status") TodoStatus status);

    List<Todo> findByStatusOrderByPriorityDesc(TodoStatus status);
}
