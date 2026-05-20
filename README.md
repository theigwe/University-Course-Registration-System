# Course Registration System — Backend

Spring Boot REST API for the Unilag course registration portal. Handles student sessions, course management, registration, and admin operations — all secured with JWT and role-based access control.

---

## Stack

| Layer | Technology |
|---|---|
| Runtime | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Security | Spring Security + JJWT 0.12.3 |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 16+ |
| Build | Maven |

---

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 16 running locally (or via Docker)

---

## Getting Started

```bash
# 1. Enter the backend directory
cd backend

# 2. Create the database
psql -U postgres -c "CREATE DATABASE registration;"

# 3. Run with defaults (see Environment Variables below)
mvn spring-boot:run
```

The server starts on **http://localhost:9095** with context path `/api`.
Full base URL: `http://localhost:9095/api`

---

## Environment Variables

All variables have working defaults for local development. Override via environment or a `.env` file.

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `registration` | Database name |
| `DB_USERNAME` | `postgres` | DB user |
| `DB_PASSWORD` | `password` | DB password |
| `JWT_SECRET` | `5367566B…` | HMAC-SHA256 signing key — **change in production** |
| `ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:4200,http://localhost:5173` | Comma-separated CORS origins |
| `ADMIN_USERNAME` | `admin` | Admin login username |
| `ADMIN_PASSWORD` | `admin123` | Admin login password — **change in production** |

---

## Project Structure

```
src/main/java/com/unilag/course_registration_system/
├── controller/          # REST controllers (@RequestMapping, @PreAuthorize)
├── service/             # Business logic interfaces
│   └── impl/            # Implementations
├── entity/              # JPA entities
├── repository/          # Spring Data JPA repositories
├── dto/
│   ├── request/         # Inbound payloads
│   └── response/        # Outbound payloads
├── model/               # JPA projections (read-only interfaces)
├── session/             # JWT filter, JWT service, CORS config, Security config
├── exception/           # Custom exceptions
└── utils/               # Response codes, helpers
```

---

## API Reference

All paths are relative to `/api`.

### Auth

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/sessions` | Public | Create student session, returns JWT |
| `POST` | `/admin/login` | Public | Admin login, returns JWT |

### Students

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/students/register` | `ADMIN` | Register a new student |
| `GET` | `/students/{studentId}` | `STUDENT` | Fetch own profile |

### Courses

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/courses/create` | `ADMIN` | Create a course |
| `GET` | `/courses` | `STUDENT` | Fetch courses for active semester / student's level & dept |

### Registrations

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/registrations` | `STUDENT` | Submit course registration |
| `GET` | `/registrations/status` | `STUDENT` | Check registration status for active session |
| `GET` | `/registrations/courses` | `STUDENT` | Get own registered courses |

### Faculties & Departments

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/faculty/create` | `ADMIN` | Create a faculty |
| `GET` | `/faculty/fetch` | `ADMIN` | List all faculties |
| `POST` | `/department/create` | `ADMIN` | Create a department |
| `GET` | `/department/fetch` | `ADMIN` | List all departments |

### Active Semester

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/active-semester/enable` | `ADMIN` | Open registration for a semester/session |
| `GET` | `/active-semester` | Public | Get the currently active registration period |

---

## Authentication

### Student flow

```
POST /api/sessions
Content-Type: application/json
{ "studentId": "STU-2025-001" }

→ 200 { "code": 200, "data": { "token": "...", "studentId": "STU-2025-001", "expiresAt": "..." } }
```

### Admin flow

```
POST /api/admin/login
Content-Type: application/json
{ "username": "admin", "password": "admin123" }

→ 200 { "code": 200, "data": { "token": "...", "username": "admin", "expiresAt": "..." } }
```

Pass the token on every subsequent request:

```
Authorization: Bearer <token>
```

Tokens expire after **24 hours** (`jwt.expiration-ms=86400000`).

### JWT claims

| Claim | Type | Notes |
|---|---|---|
| `role` | String | `ADMIN` or `STUDENT` |
| `studentId` | String | Student record ID |
| `email` | String | Student email |
| `firstName` | String | First name |

`JwtAuthenticationFilter` reads these claims and sets a Spring Security context with `ROLE_ADMIN` or `ROLE_STUDENT`, which powers `@PreAuthorize` on controllers.

---

## Role-Based Access

Controllers use `@PreAuthorize` annotations enabled by `@EnableMethodSecurity` on `SecurityConfig`:

```java
@PreAuthorize("hasRole('ADMIN')")   // admin-only
@PreAuthorize("hasRole('STUDENT')") // student-only
```

The filter chain itself permits all requests — role enforcement happens entirely at the method level. Unauthenticated requests to protected methods return `401 Unauthorized` (JSON, not HTML).

---

## Data Model

### Entity relationships

```
Faculty  1──* Department  1──* Student
                  │
                  └──* Course  *──* Registration ──* Student
                                      │
                                  (Semester embedded)

ActiveSemester  (one active row at a time)
```

### Key entity fields

**Student** — `studentId`, `firstName`, `lastName`, `email`, `phoneNumber`, `address`, `currentLevel`, `academicSession` (e.g. `2024/2025`), `@ManyToOne Department`

**Course** — `courseCode`, `courseTitle`, `creditUnit`, `prerequisite[]`, `availableSlots`, `level`, `semester` (`FIRST`|`SECOND`), `@ManyToOne Department`

**Registration** — `@ManyToOne Student`, `@ManyToMany Course`, `@Embedded Semester`, `status` (`PENDING`|`COMPLETED`)

**ActiveSemester** — `semesterName` (`FIRST`|`SECOND`), `academicSession`, `active` — only one row has `active = true` at a time; `enableSemester()` calls `deactivateAll()` before inserting a new record.

### Semester embeddable

`Semester` is reused as an embedded value object in both `Registration` and `RegistrationRequest`. Its `getDisplayLabel()` renders `FIRST` → "First Semester, 2024/2025 session".

---

## Registration Business Rules

`POST /registrations` validates in this order — first failure throws `NotFoundException`:

1. **Active semester** — an enabled registration period must exist.
2. **Semester match** — submitted semester must equal the active semester name.
3. **Session eligibility** — student's academic session start year ≥ active session start year  
   (parsed from `"YYYY/YYYY+1"` format — e.g. `2024/2025` → `2024`).
4. **No duplicate** — student has not already `COMPLETED` registration for this semester.
5. **Course count** — count of submitted course IDs must equal all courses for the student's level + department + semester.
6. **Credit units** — total credit units must be between **12 and 24** inclusive.
7. **Slot availability** — every course must have `availableSlots > 0`.

On success each selected course's `availableSlots` is decremented by 1.

---

## Response Envelope

Every endpoint returns the same wrapper:

```json
{
  "code": 200,
  "message": "Human-readable status",
  "data": { ... }
}
```

| Constant | Value |
|---|---|
| `GENERAL_SUCCESS_CODE` | `200` |
| `VALIDATION_FAILED_CODE` | `400` |

---

## CORS

Configured in `session/CorsConfig.java`. A `CorsFilter` bean runs before Spring Security.

- Origins from `cors.allowed-origins` (comma-separated, env-configurable).
- Pattern is `/**` — the `/api` context-path is stripped by the servlet container before matching.
- `allowCredentials = true` — required for the `Authorization` header to pass through from a browser.

---

## Building for Production

```bash
mvn clean package -DskipTests

java -jar target/course-registration-system-*.jar \
  --DB_HOST=prod-host \
  --DB_PORT=5432 \
  --DB_PASSWORD=<secret> \
  --JWT_SECRET=<64-char-hex> \
  --ADMIN_PASSWORD=<strong-password> \
  --ALLOWED_ORIGINS=https://your-frontend.com
```
