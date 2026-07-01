package com.pm.sprint.dto;

import java.time.LocalDate;

public record CreateSprintRequest(String name, String goal, LocalDate startDate, LocalDate endDate) {}
