package com.pm.project.dto;

import com.pm.workflow.dto.TransitionDto;
import java.util.List;

public record ProjectDetailDto(Long id, String name, String key, String description,
                               Long ownerId, List<TransitionDto> workflow) {}
