package com.pm.sprint.dto;

import java.time.LocalDate;

public record SprintDto(Long id, Long projectId, String name, String goal,
                        LocalDate startDate, LocalDate endDate, boolean active) {}
