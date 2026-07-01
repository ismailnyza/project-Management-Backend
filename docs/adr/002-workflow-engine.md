# ADR 002: Workflow engine over free-form statuses

**Status**: Accepted  
**Date**: 2026-07-01

## Context
A project management tool needs status transitions. The naive approach is a `status` column on issues that can be set to anything. Jira's core value proposition is configurable workflows — statuses are meaningless without transition rules.

## Decision
Implement a workflow engine with `workflow_transitions` table (project_id, from_status, to_status). Every status change goes through `POST /issues/{id}/transition { transitionId }` which validates the transition exists in the project's workflow.

## Rationale
- **Data integrity**: Impossible to put an issue in an invalid state. The workflow IS the source of truth.
- **Configurability**: Each project gets its own workflow. Default: TODO → IN_PROGRESS → IN_REVIEW → DONE with back-transitions.
- **Auditability**: Every transition creates an activity log entry showing who moved what and when.
- **Interview conversation starter**: "How would you model a workflow engine?" is a classic system design question. Having built one is a strong signal.

## Consequences
- Added complexity: 3 extra tables (workflow_transitions, activity_log) and a dedicated service
- Transition validation adds a DB query per status change
- Bulk transitions (moving multiple issues) are not yet implemented but the schema supports it
