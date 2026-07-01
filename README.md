# Project Management API

[![CI](https://github.com/ismailnyza/project-Management-Backend/actions/workflows/ci.yml/badge.svg)](https://github.com/ismailnyza/project-Management-Backend/actions)

A full-stack Jira-like project management application — **React + TypeScript frontend, Spring Boot 3.4 backend, PostgreSQL, JWT auth with refresh tokens, workflow engine, optimistic locking, rate limiting, Flyway migrations, Swagger, Docker, CI/CD**.

## Quick Start

```bash
# Start backend + database
docker compose up -d

# In another terminal, start frontend
cd frontend && npm install && npm run dev
```

- **Frontend**: http://localhost:3000
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Demo login**: `alice@demo.com` / `demo123` (auto-seeded on first run)

### Without Docker

```bash
# Requires PostgreSQL running on localhost:5432
./gradlew bootRun
```

Or with custom DB:

```bash
DATABASE_URL=jdbc:postgresql://host:5432/mydb \
DATABASE_USER=user DATABASE_PASS=pass \
./gradlew bootRun
```

## Run Tests

```bash
./gradlew test   # Unit + integration + architecture tests (uses H2 in-memory)
```

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        BROWSER (React)                         │
│  Login → Projects → Kanban Board → Issue Modal (transitions)   │
│  ErrorBoundary · Loading states · Optimistic board updates     │
└───────────────────────────┬─────────────────────────────────────┘
                            │ REST + JWT Bearer
┌───────────────────────────▼─────────────────────────────────────┐
│                    SPRING BOOT 3.4 (Java 21)                    │
│                                                                 │
│  ┌──────────┐  ┌───────────┐  ┌────────────┐  ┌────────────┐  │
│  │  Config  │  │ Controller│  │  Service   │  │ Repository │  │
│  │          │  │           │  │            │  │            │  │
│  │ Security │  │ AuthCtrl  │  │ AuthSvc────┼──┤ UserRepo   │  │
│  │ JWT      │  │ ProjCtrl  │  │ ProjSvc────┼──┤ ProjRepo   │  │
│  │ CORS     │  │ IssueCtrl │  │ IssueSvc───┼──┤ IssueRepo  │  │
│  │ RateLimit│  │ Workflow  │  │ Workflow───┼──┤ WfRepo     │  │
│  │ TraceId  │  │ Comment   │  │ CommentSvc─┼──┤ CmntRepo   │  │
│  │ OpenAPI  │  │ SprintCtrl│  │ SprintSvc──┼──┤ SprintRepo │  │
│  │          │  │ UserCtrl  │  │ UserSvc────┼──┤            │  │
│  └──────────┘  └───────────┘  │ Activity───┼──┤ ActyRepo   │  │
│                               └────────────┘  └────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    DOMAIN ENGINE                          │  │
│  │  Workflow: Project → [Transition: from→to]               │  │
│  │  Issue: EPIC→STORY→SUBTASK hierarchy (auto-resolution)   │  │
│  │  Business rules: Block close with open subtasks          │  │
│  │  Optimistic locking: @Version on concurrent edits        │  │
│  │  Activity log: Immutable audit trail per issue           │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────────────┘
                            │ JDBC / Hibernate
┌───────────────────────────▼─────────────────────────────────────┐
│                      POSTGRESQL 16                              │
│  Flyway migrations · 9 tables · CASCADE deletes · Indexes      │
└─────────────────────────────────────────────────────────────────┘
```

### Package structure (package-by-feature)

```
com.pm/
├── auth/         JWT, refresh tokens, login/register
├── user/         User entity + CRUD
├── project/      Project entity + CRUD
├── workflow/     WorkflowTransition + validation engine
├── issue/        Issue entity (5 types, priorities, hierarchy)
├── comment/      Comment entity + CRUD
├── sprint/       Sprint entity + CRUD
├── activity/     ActivityLog — immutable audit trail
├── config/       SecurityConfig, JwtFilter, WebConfig, RateLimiter, SeedData, TraceId
└── common/       BaseEntity, ApiError, PageResponse, AuthHelper
```

## Architecture Decisions

See [/docs/adr/](/docs/adr/):
- [ADR 001](/docs/adr/001-spring-boot.md) — Why Spring Boot over Spark
- [ADR 002](/docs/adr/002-workflow-engine.md) — Workflow engine design
- [ADR 003](/docs/adr/003-optimistic-locking.md) — Concurrency control

## Domain Model

```
Project ──→ WorkflowTransition (from_status → to_status)
  │
  ├── Sprint
  │
  └── Issue (type: EPIC|STORY|TASK|BUG|SUBTASK)
        ├── parent (hierarchy)
        ├── assignee, reporter → User
        ├── subtasks → Issue[]
        ├── comments → Comment[]
        └── activity → ActivityLog[]
```

## The Workflow Engine

Every project gets a configurable workflow. Status changes go through the workflow:

```
POST /api/v1/issues/1/transition { "transitionId": 3 }
```

This validates: does transition #3 belong to this project? Does its `from_status` match the issue's current status? If not → 409 Conflict.

### Default workflow

```
TODO ──→ IN_PROGRESS ──→ IN_REVIEW ──→ DONE
  ↑          ↑               ↑            │
  └──────────┴───────────────┴────────────┘  (back-transitions)
```

Customize per project:

```bash
POST /api/v1/projects/1/workflow/transitions
{ "fromStatus": "IN_REVIEW", "toStatus": "DEPLOYED", "name": "Deploy" }
```

## Issue Hierarchy

| Type | Parent | Auto-behavior |
|------|--------|---------------|
| **EPIC** | (none) | Children auto-set to STORY |
| **STORY** | EPIC | Children auto-set to SUBTASK |
| **TASK** | (none) | Standalone work item |
| **BUG** | (none) | Like TASK with severity tracking |
| **SUBTASK** | STORY/TASK | Always has a parent |

Creating an issue under an EPIC automatically sets type to STORY. Under a STORY → SUBTASK.

## API Reference

### Auth

| Method | Path | Auth |
|--------|------|------|
| `POST` | `/api/v1/auth/register` | No |
| `POST` | `/api/v1/auth/login` | No |

### Projects

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/projects` | Create `{"name":"...","key":"PROJ"}` |
| `GET` | `/api/v1/projects` | List all |
| `GET` | `/api/v1/projects/{id}` | Detail with workflow |
| `DELETE` | `/api/v1/projects/{id}` | Cascade delete |

### Workflow

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/projects/{id}/workflow/transitions` | List transitions |
| `POST` | `/api/v1/projects/{id}/workflow/transitions` | Add transition |
| `DELETE` | `/api/v1/projects/{id}/workflow/transitions/{id}` | Remove transition |

### Issues

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/projects/{id}/issues` | Create: `{"type":"TASK","title":"...","priority":"HIGH"}` |
| `GET` | `/api/v1/projects/{id}/issues` | List with filters: `?status=TODO&type=BUG&assigneeId=1&search=login&page=1&size=20` |
| `GET` | `/api/v1/issues/{id}` | Detail with subtasks + comments |
| `PATCH` | `/api/v1/issues/{id}` | Update fields |
| `POST` | `/api/v1/issues/{id}/transition` | Workflow transition: `{"transitionId":3}` |
| `DELETE` | `/api/v1/issues/{id}` | Delete |

### Comments

| Method | Path |
|--------|------|
| `POST` | `/api/v1/issues/{id}/comments` `{"body":"..."}` |
| `DELETE` | `/api/v1/comments/{id}` |

### Sprints

| Method | Path |
|--------|------|
| `POST` | `/api/v1/projects/{id}/sprints` |
| `GET` | `/api/v1/projects/{id}/sprints` |
| `PATCH` | `/api/v1/sprints/{id}` |

### Users

| Method | Path |
|--------|------|
| `GET` | `/api/v1/users` |
| `GET` | `/api/v1/users/{id}` |

## Key Design Choices

- **Optimistic locking** (`@Version`): prevents lost updates on concurrent edits. 409 Conflict on version mismatch.
- **Trace IDs**: every request gets a trace ID (header `X-Trace-Id`). Included in all log output via MDC.
- **Structured errors**: `{ "status": 422, "error": "Validation failed", "message": "...", "details": { "title": "must not be blank" } }`
- **No DTO frameworks**: Records are used for DTOs. MapStruct/Lombok `@Builder` avoided — records are immutable, concise, and standard Java.
- **H2 for tests, PostgreSQL for dev**: `@ActiveProfiles("test")` switches to in-memory H2 with auto-DDL. No Testcontainers overhead for fast feedback.

## Tech Stack

| Layer | Choice |
|-------|--------|
| Framework | Spring Boot 3.4 |
| Security | Spring Security + JJWT (stateless JWT) |
| ORM | Spring Data JPA + Hibernate |
| DB | PostgreSQL (dev), H2 (test) |
| Migrations | Flyway |
| Validation | Bean Validation (jakarta.validation) |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Mockito + ArchUnit |
| Build | Gradle + Docker |

## Project Structure

```
src/main/java/com/pm/
├── PmApplication.java
├── config/          SecurityConfig, JwtFilter, WebConfig, ExceptionHandler
├── common/          BaseEntity, ApiError, PageResponse, TraceIdFilter
├── auth/            JWT provider, AuthService, login/register
├── user/            User entity, CRUD
├── project/         Project entity, CRUD
├── workflow/        WorkflowTransition entity + validation engine
├── issue/           Issue entity (types, priorities, hierarchy)
├── comment/         Comment entity, CRUD
├── sprint/          Sprint entity, CRUD
└── activity/        ActivityLog — immutable audit trail
```
