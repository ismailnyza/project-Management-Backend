package com.pm.issue.dto;

import com.pm.issue.enums.Priority;
import java.time.LocalDate;

public record UpdateIssueRequest(
    String title, String description, Priority priority,
    Long assigneeId, Long sprintId, Integer storyPoints, LocalDate dueDate
) {}
