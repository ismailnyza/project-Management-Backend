package com.pm.workflow.dto;

public record TransitionDto(Long id, String fromStatus, String toStatus, String name) {}
