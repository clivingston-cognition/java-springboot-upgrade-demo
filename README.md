# Todo App — Spring Boot 3.3 + Maven

A full-featured todo list web application built with **Spring Boot 3.3**, **Java 17**, and **Maven**.

## Tech Stack

| Layer        | Technology                          |
|-------------|-------------------------------------|
| Framework   | Spring Boot 3.3.6                   |
| Build       | Apache Maven                        |
| Language    | Java 17                             |
| Web UI      | Thymeleaf                           |
| Database    | H2 (file-based, or in-memory for tests) |
| Migrations  | Flyway                              |
| Validation  | Jakarta Bean Validation             |
| Testing     | JUnit 5, Mockito, MockMvc, AssertJ  |

## Features

- Full CRUD for todos (create, read, update, delete)
- Toggle completion status
- Priority levels: LOW, MEDIUM, HIGH
- Status tracking: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
- Due date support
- Stats dashboard (total, pending, completed counts)
- REST API (`/api/todos`) and Thymeleaf web UI (`/`)
- Flyway database migrations
- Seed data on first run
- Spring Boot Actuator health endpoint

## Quick Start

```bash
# Build and run
./mvnw clean package
java -jar target/todo-app-1.0.0-SNAPSHOT.jar

# Or run directly
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

## API Endpoints

| Method | Endpoint                | Description            |
|--------|------------------------|------------------------|
| GET    | `/api/todos`           | List all todos         |
| GET    | `/api/todos/{id}`      | Get todo by ID         |
| POST   | `/api/todos`           | Create a new todo      |
| PUT    | `/api/todos/{id}`      | Update a todo          |
| PUT    | `/api/todos/{id}/toggle` | Toggle completion    |
| DELETE | `/api/todos/{id}`      | Delete a todo          |

## Web Routes

| Route              | Description          |
|-------------------|----------------------|
| `/`               | Todo list dashboard  |
| `/todos/new`      | Create todo form     |
| `/todos/{id}`     | Todo detail view     |
| `/todos/{id}/edit`| Edit todo form       |

## Running Tests

```bash
# Unit + integration tests
./mvnw clean verify
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/todo/
│   │   ├── config/          # DataInitializer (seed data)
│   │   ├── controller/      # REST + MVC controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── exception/       # Global error handling
│   │   ├── model/           # JPA entities & enums
│   │   ├── repository/      # Spring Data JPA repos
│   │   ├── service/         # Business logic
│   │   └── TodoApplication.java
│   └── resources/
│       ├── db/migration/    # Flyway SQL scripts
│       ├── static/css/      # Stylesheet
│       ├── templates/       # Thymeleaf templates
│       └── application.properties
└── test/                    # Unit & integration tests
```

## Planned: Gradle Migration

This project is intentionally built with Maven to serve as a baseline for a Maven-to-Gradle migration exercise.
