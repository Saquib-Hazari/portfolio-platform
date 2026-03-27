# Portfolio Backend

A Spring Boot backend that powers authentication, blog content, comments, uploads, and admin operations for the portfolio frontend.

## Highlights
- JWT‑based auth (signup, login, logout)
- Blog CRUD with tags and images
- Comments and reactions
- Admin stats and moderation endpoints
- Flyway migrations with PostgreSQL

## Tech Stack
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- JWT (jjwt)

## Requirements
- Java 25 (as configured in `pom.xml`)
- PostgreSQL

## Configuration
You can use `application.yaml` defaults for local dev or override via environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/portfolio_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_base64_secret
ACCESS_EXPIRATION=15m
REFRESH_EXPIRATION=14d
```

## Run Locally
```bash
./mvnw spring-boot:run
```

Or build a jar:
```bash
./mvnw -DskipTests package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Core Endpoints (High‑Level)
- `POST /auth/signup`, `POST /auth/login`, `POST|GET /auth/logout`
- `GET /api/blogs`, `GET /api/blogs/{id}`
- `POST /api/blogs`, `PUT /api/blogs/{id}`, `DELETE /api/blogs/{id}` (admin)
- `GET /api/blogs/{id}/comments`, `POST /api/blogs/{id}/comments`, `DELETE /api/blogs/{id}/comments/{commentId}`
- `POST /api/comments/{commentId}/reactions`
- `POST /api/uploads/images`, `GET /api/uploads/images/{id}`, `DELETE /api/uploads/images/{id}`
- `GET /admin/stats`, `GET /admin/comments`, `DELETE /admin/comments/{commentId}`

## Notes
- CORS is currently limited to `http://localhost:5173` in `CorsConfig`. Update for production.
- Uploads are stored locally; for cloud deployments use object storage.

