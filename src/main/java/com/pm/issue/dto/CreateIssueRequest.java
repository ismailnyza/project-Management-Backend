package com.pm.issue.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.pm.issue.enums.IssueType;
import com.pm.issue.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateIssueRequest(
    @NotNull @Schema(example = "TASK", description = "Issue type") IssueType type,
    @NotBlank @Schema(example = "Fix login bug", description = "Issue title") String title,
    @Schema(example = "Users cannot log in via SSO", description = "Detailed description") String description,
    @Schema(example = "HIGH") Priority priority,
    @Schema(example = "TODO", description = "Initial status") String status,
    @Schema(example = "1", description = "Assignee user ID") Long assigneeId,
    @Schema(example = "5", description = "Parent issue ID for subtasks/stories") Long parentId,
    @Schema(example = "1") Long sprintId,
    @Schema(example = "3", description = "Story points estimate") Integer storyPoints,
    @Schema(example = "2026-12-31") LocalDate dueDate
) {}
