# TODO Manager - Spring Boot Application

A sophisticated TODO list application built with **Java 11** and **Spring Boot 2.7.18**, featuring full CRUD operations, a Thymeleaf web UI, H2 embedded database with Flyway migrations, and comprehensive test coverage.

> **Note:** This application intentionally uses outdated versions (Java 11 + Spring Boot 2.7.x) — two major releases behind the current Java 21 + Spring Boot 3.x ecosystem.

---

## Tech Stack

| Component        | Version / Technology       |
|------------------|----------------------------|
| Java             | 11 (LTS)                   |
| Spring Boot      | 2.7.18                     |
| Database         | H2 (embedded, file-based)  |
| ORM              | Spring Data JPA / Hibernate|
| Migrations       | Flyway                     |
| UI               | Thymeleaf + CSS            |
| Validation       | Hibernate Validator (JSR-380) |
| Testing          | JUnit 5 + Mockito + MockMvc|
| Build Tool       | Maven 3.8+                 |

---

## Features

- **Full CRUD Operations** — Create, Read, Update, Delete todos via REST API and Web UI
- **Priority Levels** — LOW, MEDIUM, HIGH, CRITICAL
- **Status Tracking** — PENDING, IN_PROGRESS, COMPLETED, CANCELLED
- **Search** — Full-text search across titles and descriptions
- **Filtering** — Filter by status, priority, or both
- **Pagination & Sorting** — Paginated list views with configurable sort order
- **Statistics Dashboard** — Real-time counts by status + overdue tracking
- **Due Date Management** — Set due dates with overdue detection
- **Toggle Completion** — One-click complete/uncomplete
- **Responsive UI** — Clean, mobile-friendly Thymeleaf interface
- **H2 Console** — Built-in database browser at `/h2-console`
- **Actuator Endpoints** — Health, info, and metrics at `/actuator`
- **Flyway Migrations** — Version-controlled database schema

---

## Prerequisites

- **Java 11** (JDK) — [Download](https://adoptium.net/temurin/releases/?version=11)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi) (or use the included Maven Wrapper)

Verify your setup:

```bash
java -version   # Should show version 11.x
mvn -version    # Should show 3.8+
```

---

## Runbook: Build, Run & Test

### 1. Clone the Repository

```bash
git clone https://github.com/clivingston-cognition/java-springboot-upgrade-demo.git
cd java-springboot-upgrade-demo
```

### 2. Build the Application

```bash
# Using Maven Wrapper (recommended)
./mvnw clean package

# Or using system Maven
mvn clean package
```

This compiles the code, runs all tests, and produces a JAR in `target/`.

### 3. Run the Application

```bash
# Using Maven Wrapper
./mvnw spring-boot:run

# Or run the JAR directly
java -jar target/todo-app-1.0.0-SNAPSHOT.jar
```

The application starts on **http://localhost:8080**.

### 4. Access the Application

| URL                          | Description                     |
|------------------------------|---------------------------------|
| http://localhost:8080        | Web UI — Todo Manager           |
| http://localhost:8080/h2-console | H2 Database Console          |
| http://localhost:8080/actuator/health | Health Check             |

**H2 Console Connection Settings:**
- JDBC URL: `jdbc:h2:file:./data/tododb`
- Username: `sa`
- Password: *(leave empty)*

---

## Testing

### Run All Tests

```bash
# Unit tests + Integration tests
./mvnw clean test

# Or with verbose output
./mvnw clean test -X
```

### Run Specific Test Classes

```bash
# API Controller tests only
./mvnw test -Dtest=TodoApiControllerTest

# Service layer tests only
./mvnw test -Dtest=TodoServiceTest

# Repository tests only
./mvnw test -Dtest=TodoRepositoryTest

# Integration tests only
./mvnw test -Dtest=TodoIntegrationTest
```

### Run Integration Tests with Failsafe

```bash
./mvnw clean verify
```

### Test Coverage Summary

| Test Class                  | Coverage Area                                   | Tests |
|-----------------------------|------------------------------------------------|-------|
| `TodoApiControllerTest`     | REST API endpoints (MockMvc + mocked service)   | 15    |
| `TodoServiceTest`           | Business logic (unit tests with Mockito)         | 14    |
| `TodoRepositoryTest`        | JPA queries (DataJpaTest with H2)                | 10    |
| `TodoIntegrationTest`       | Full stack E2E (SpringBootTest + MockMvc)        | 8     |
| `TodoApplicationTests`      | Spring context loading                           | 1     |
| **Total**                   |                                                  | **48**|

---

## REST API Reference

### Endpoints

| Method   | URL                        | Description              | Request Body     |
|----------|----------------------------|--------------------------|------------------|
| `POST`   | `/api/todos`               | Create a new todo        | `TodoRequest`    |
| `GET`    | `/api/todos`               | List todos (paginated)   | —                |
| `GET`    | `/api/todos/{id}`          | Get a single todo        | —                |
| `GET`    | `/api/todos/search`        | Search todos             | —                |
| `PUT`    | `/api/todos/{id}`          | Update a todo            | `TodoRequest`    |
| `PATCH`  | `/api/todos/{id}/toggle`   | Toggle completion        | —                |
| `DELETE` | `/api/todos/{id}`          | Delete a todo            | —                |
| `GET`    | `/api/todos/stats`         | Get statistics           | —                |

### Query Parameters (GET /api/todos)

| Param       | Default      | Description                          |
|-------------|-------------|---------------------------------------|
| `page`      | `0`          | Page number (zero-based)             |
| `size`      | `10`         | Page size                            |
| `sortBy`    | `createdAt`  | Sort field                           |
| `direction` | `desc`       | Sort direction (`asc` or `desc`)     |
| `status`    | —            | Filter: PENDING, IN_PROGRESS, etc.   |
| `priority`  | —            | Filter: LOW, MEDIUM, HIGH, CRITICAL  |

### Example cURL Commands

```bash
# Create a todo
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot","description":"Complete the tutorial","priority":"HIGH","status":"PENDING"}'

# List all todos
curl http://localhost:8080/api/todos

# Get a specific todo
curl http://localhost:8080/api/todos/1

# Update a todo
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot 3","description":"Upgrade guide","priority":"CRITICAL","status":"IN_PROGRESS"}'

# Toggle completion
curl -X PATCH http://localhost:8080/api/todos/1/toggle

# Delete a todo
curl -X DELETE http://localhost:8080/api/todos/1

# Search
curl "http://localhost:8080/api/todos/search?keyword=spring"

# Get statistics
curl http://localhost:8080/api/todos/stats
```

---

## Project Structure

```
├── pom.xml                          # Maven build configuration
├── README.md                        # This file
├── src/
│   ├── main/
│   │   ├── java/com/example/todo/
│   │   │   ├── TodoApplication.java           # Main entry point
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java       # Sample data seeder
│   │   │   ├── controller/
│   │   │   │   ├── TodoApiController.java     # REST API controller
│   │   │   │   └── TodoViewController.java    # Thymeleaf view controller
│   │   │   ├── dto/
│   │   │   │   ├── TodoRequest.java           # Input DTO
│   │   │   │   └── TodoResponse.java          # Output DTO
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── TodoNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── Priority.java              # Priority enum
│   │   │   │   ├── Todo.java                  # JPA entity
│   │   │   │   └── TodoStatus.java            # Status enum
│   │   │   ├── repository/
│   │   │   │   └── TodoRepository.java        # Spring Data JPA repository
│   │   │   └── service/
│   │   │       └── TodoService.java           # Business logic
│   │   └── resources/
│   │       ├── application.properties         # App configuration
│   │       ├── application-test.properties    # Test configuration
│   │       ├── db/migration/
│   │       │   └── V1__create_todos_table.sql # Flyway migration
│   │       ├── static/css/
│   │       │   └── style.css                  # Application styles
│   │       └── templates/
│   │           ├── fragments/layout.html      # Shared layout
│   │           ├── index.html                 # Todo list view
│   │           ├── todo-detail.html           # Todo detail view
│   │           └── todo-form.html             # Create/Edit form
│   └── test/
│       └── java/com/example/todo/
│           ├── TodoApplicationTests.java
│           ├── controller/
│           │   └── TodoApiControllerTest.java
│           ├── integration/
│           │   └── TodoIntegrationTest.java
│           ├── repository/
│           │   └── TodoRepositoryTest.java
│           └── service/
│               └── TodoServiceTest.java
```

---

## Troubleshooting

| Issue                              | Solution                                                |
|------------------------------------|---------------------------------------------------------|
| `java: unsupported class version`  | Ensure JAVA_HOME points to JDK 11                      |
| Port 8080 in use                   | `server.port=9090` in application.properties or `-Dserver.port=9090` |
| H2 database locked                 | Stop other instances; delete `./data/tododb.mv.db`     |
| Tests failing                      | Run `./mvnw clean test` for a clean build               |
| Flyway migration error             | Delete `./data/` directory and restart                   |
