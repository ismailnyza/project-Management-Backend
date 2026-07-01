# ADR 001: Spring Boot over Spark

**Status**: Accepted  
**Date**: 2026-07-01

## Context
The previous iteration used Spark (a micro-framework). For a portfolio project targeting senior engineering roles, the framework choice signals familiarity with industry-standard tools.

## Decision
Migrate to Spring Boot 3.4 with Spring Data JPA, Spring Security, and Flyway.

## Rationale
- **Hiring signal**: Every Java backend job listing mentions Spring Boot. Spark is unknown outside hobby projects.
- **Ecosystem**: Spring Security provides battle-tested auth out of the box. Spring Data JPA eliminates boilerplate repository code. Flyway handles schema migrations declaratively.
- **Testing**: `@SpringBootTest` with H2 in-memory DB enables full integration tests with zero external dependencies.
- **Observability**: Spring Boot Actuator provides health checks, metrics, and tracing hooks.

## Consequences
- Larger artifact size (~30MB vs ~5MB for Spark)
- Steeper initial setup (but the payoff is faster feature development)
- Required learning Spring's dependency injection and auto-configuration patterns
