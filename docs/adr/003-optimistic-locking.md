# ADR 003: Optimistic locking for concurrent edits

**Status**: Accepted  
**Date**: 2026-07-01

## Context
Multiple users can edit the same issue simultaneously. Without concurrency control, the last write silently wins, potentially losing data.

## Decision
Use JPA's `@Version` annotation for optimistic locking. The `issues` table has a `version` column incremented on every update. If two users fetch version 5 and both try to save, the second save fails with an `OptimisticLockException`.

## Rationale
- **Standard**: Built into JPA, zero custom code. `@Version` on the entity and Hibernate handles the rest.
- **Non-blocking**: Unlike pessimistic locks, optimistic locking doesn't hold database locks. Better for read-heavy workloads.
- **User-friendly**: The conflict can be surfaced as a 409 Conflict response, allowing the client to re-fetch and retry.

## Alternatives considered
- **Pessimistic locking** (`SELECT ... FOR UPDATE`): Blocks other readers, bad for a collaborative tool where many users view the same issue.
- **Event sourcing**: Overkill for this scope. Adds event store, projections, and eventual consistency complexity.

## Consequences
- Clients must handle 409 responses and implement retry logic
- The version field is exposed in API responses so clients can send it back
- Does not prevent conflicts at the workflow level (two users moving the same issue through different transitions)
