package com.pm.project.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProjectRequest(@NotBlank String name, @NotBlank String key, String description) {}
