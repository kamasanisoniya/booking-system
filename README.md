# Resource Booking System

Spring Boot application for managing resources and reservations with JWT authentication and RBAC.

## Requirements
- Java 17+
- MySQL
- Maven

## Setup
1. Create DB: `CREATE DATABASE booking_db;`
2. Set env vars:
   - DB_URL (optional)
   - DB_USERNAME
   - DB_PASSWORD
   - JWT_SECRET
3. Run: `./mvnw spring-boot:run`

Swagger UI: http://localhost:8080/swagger-ui.html
