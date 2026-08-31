# Resource Booking System

Spring Boot application to manage resources and reservations with JWT authentication and role-based access control. This README is tailored for the GitHub repository `kamasanisoniya/booking-system`.

---

## Quick facts
- Language: Java 17
- Framework: Spring Boot 3.x
- Build: Maven
- Dev DB: H2 (dev profile)
- Production DB: MySQL (configurable via env vars)
- Main class: `com.example.booking.BookingApplication`
- Jar: `target/booking-system-0.0.1-SNAPSHOT.jar`
- Docker: multi-stage Dockerfile included (builds jar inside image)

---

## Prerequisites
- JDK 17+
- Maven 3.x (or use Docker)
- Docker Desktop (recommended for local container testing)
- Git

---

## Quickstart (local — development)
1. Clone the repo
   git clone https://github.com/kamasanisoniya/booking-system.git
   cd booking-system

2. Build
   mvn -B -DskipTests package

3. Run (dev profile → in-memory H2 DB)
   java -jar target/booking-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

4. Open in browser
   - App: http://localhost:8080/
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console  
     JDBC URL: `jdbc:h2:mem:devdb`  User: `sa`  Password: (blank)

---

## Run with Docker (recommended)
A multi-stage Dockerfile is included. It runs Maven in the build stage and produces a runnable image.

Build:
  docker build -t booking-system:local .

Run:
  docker run --rm --name booking-local -e PORT=8080 -p 8080:8080 booking-system:local

Visit:
  http://localhost:8080/ and http://localhost:8080/h2-console

Windows note: Docker Desktop with WSL2 is recommended for Linux containers.

---

## Deployment hints (Render example)
If you deploy to Render (Git provider flow):
- Build command:
  mvn -B -DskipTests=false package
- Start command:
  java -jar target/booking-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
  (Because `server.port=${PORT:8080}` is configured, Render's `$PORT` is used automatically.)
- Environment variables to set for production:
  - `DB_URL` (e.g. `jdbc:mysql://host:3306/booking_db`)
  - `DB_USERNAME`
  - `DB_PASSWORD`
  - `JWT_SECRET`

If you prefer Docker on Render, the included multi-stage Dockerfile will build correctly.

---

## Configuration / Environment variables
application.properties reads these environment variables (defaults shown):
- `DB_URL` — default `jdbc:mysql://localhost:3306/booking_db`
- `DB_USERNAME` — default `root`
- `DB_PASSWORD` — default `password`
- `JWT_SECRET` — no safe default (set for production)
- `PORT` — server port; `server.port=${PORT:8080}`

Set these before starting in `prod` profile.

---

## Project layout (short)
- src/main/java/com/example/booking
  - BookingApplication.java
  - config/ — app & security configuration
  - controller/ — REST controllers (AuthController, ResourceController, ReservationController)
  - service/ — business logic
  - repository/ — Spring Data JPA repositories
  - entity/ — JPA entities (User, Role, Resource, Reservation)
  - security/ — JWT utilities, filters
  - dto/ — request/response DTOs
  - exception/ — handlers

---

## API (high level)
Use Swagger UI for full interactive API docs (`/swagger-ui.html`).

Common endpoints:
- Auth: `POST /api/auth/login` (returns JWT)
- Resources: `GET /api/resources`, `POST /api/resources` (admin)
- Reservations: `GET /api/reservations`, `POST /api/reservations`

Refer to controllers and Swagger for exact request/response formats.

---

## Testing
Run unit/integration tests:
  mvn test

Recommended: add integration tests that run under the `dev` profile (H2) for auth and reservation flows and include them in CI.

---

## CI example (GitHub Actions)
Minimal CI to run tests & package:
```yaml
name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: java-version: 17
      - run: mvn -B -DskipTests=false package

Swagger UI: http://localhost:8080/swagger-ui.html
