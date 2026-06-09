package com.example.todo.config;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(TodoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Todo todo1 = new Todo("Set up project structure", "Configure Maven build and Spring Boot dependencies", Priority.HIGH, LocalDate.now().plusDays(1));
                todo1.setStatus(TodoStatus.COMPLETED);

                Todo todo2 = new Todo("Implement REST API", "Create CRUD endpoints for todo management", Priority.HIGH, LocalDate.now().plusDays(3));
                todo2.setStatus(TodoStatus.IN_PROGRESS);

                Todo todo3 = new Todo("Add Thymeleaf UI", "Build web interface using Thymeleaf templates", Priority.MEDIUM, LocalDate.now().plusDays(5));

                Todo todo4 = new Todo("Write unit tests", "Cover service and controller layers with JUnit 5 tests", Priority.MEDIUM, LocalDate.now().plusDays(7));

                Todo todo5 = new Todo("Migrate to Gradle", "Convert Maven build to Gradle build system", Priority.LOW, LocalDate.now().plusDays(14));

                repository.save(todo1);
                repository.save(todo2);
                repository.save(todo3);
                repository.save(todo4);
                repository.save(todo5);
            }
        };
    }
}
