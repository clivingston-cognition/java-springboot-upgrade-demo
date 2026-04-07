package com.example.todo.config;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoStatus;
import com.example.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Profile("!test")
    public CommandLineRunner initData(TodoRepository todoRepository) {
        return args -> {
            if (todoRepository.count() == 0) {
                log.info("Initializing sample TODO data...");

                Todo todo1 = new Todo("Set up CI/CD pipeline", "Configure GitHub Actions for automated builds and deployments", Priority.HIGH);
                todo1.setStatus(TodoStatus.IN_PROGRESS);
                todo1.setDueDate(LocalDateTime.now().plusDays(3));

                Todo todo2 = new Todo("Write unit tests", "Add comprehensive test coverage for all service methods", Priority.HIGH);
                todo2.setStatus(TodoStatus.PENDING);
                todo2.setDueDate(LocalDateTime.now().plusDays(5));

                Todo todo3 = new Todo("Update API documentation", "Generate and publish Swagger/OpenAPI docs", Priority.MEDIUM);
                todo3.setStatus(TodoStatus.PENDING);
                todo3.setDueDate(LocalDateTime.now().plusDays(7));

                Todo todo4 = new Todo("Review pull requests", "Review and merge pending team PRs", Priority.MEDIUM);
                todo4.setStatus(TodoStatus.COMPLETED);

                Todo todo5 = new Todo("Database backup strategy", "Implement automated database backup and recovery plan", Priority.CRITICAL);
                todo5.setStatus(TodoStatus.PENDING);
                todo5.setDueDate(LocalDateTime.now().plusDays(1));

                Todo todo6 = new Todo("Refactor logging", "Migrate to structured JSON logging format", Priority.LOW);
                todo6.setStatus(TodoStatus.PENDING);
                todo6.setDueDate(LocalDateTime.now().plusDays(14));

                todoRepository.save(todo1);
                todoRepository.save(todo2);
                todoRepository.save(todo3);
                todoRepository.save(todo4);
                todoRepository.save(todo5);
                todoRepository.save(todo6);

                log.info("Sample data initialization complete. {} todos created.", todoRepository.count());
            }
        };
    }
}
