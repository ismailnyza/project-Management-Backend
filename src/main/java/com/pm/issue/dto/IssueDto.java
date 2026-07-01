package com.pm.issue.dto;

import com.pm.issue.enums.IssueType;
import com.pm.issue.enums.Priority;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IssueDto(
    Long id, Long projectId, IssueType type, Priority priority, String status,
    String title, String description, Long assigneeId, String assigneeName,
    Long reporterId, String reporterName, Long parentId, Long sprintId,
    Integer storyPoints, LocalDate dueDate, Long version,
    LocalDateTime createdAt, LocalDateTime updatedAt
) {}
