package com.pm.workflow.dto;

public record CreateTransitionRequest(String fromStatus, String toStatus, String name) {}
