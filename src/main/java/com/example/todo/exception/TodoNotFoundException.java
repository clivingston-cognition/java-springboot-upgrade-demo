package com.example.todo.exception;

public class TodoNotFoundException extends RuntimeException {

    private final Long todoId;

    public TodoNotFoundException(Long id) {
        super("Todo not found with id: " + id);
        this.todoId = id;
    }

    public Long getTodoId() {
        return todoId;
    }
}
